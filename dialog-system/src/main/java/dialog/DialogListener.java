package dialog;

import data.ActionData;
import data.DialogData;
import dialog.tools.JsonDeserializer;
import dialog.tools.JsonSerializer;
import dialog.tools.KafkaPropertiesConfigure;
import dialog.tools.MsgProcessorSupplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.internals.KStreamReduce;
import org.apache.kafka.streams.kstream.internals.KStreamWindowReduce;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by BSONG on 2017/8/7.
 */
@Slf4j
public class DialogListener implements Runnable {

    private KafkaStreams kafkaStreams;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void run() {
        Serde<DialogData> dialogDataSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(DialogData.class));
        Serde<ActionData> actionDataSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(ActionData.class));

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder
                .addGlobalStore(Stores.create("dialog").withLongKeys().withValues(dialogDataSerde).inMemory().disableLogging().build(), "dialogSource",
                        new LongDeserializer(), dialogDataSerde.deserializer(), "dialog", "msgProcessor", new MsgProcessorSupplier())
                .addGlobalStore(Stores.create("dialogHistory").withStringKeys().withValues(actionDataSerde).persistent().windowed(100, 30 * 24 * 3600, 2, true).enableCaching().disableLogging().build(),
                        "historySource", new StringDeserializer(), actionDataSerde.deserializer(), "historyTopic", "historyProcessor",
                        new KStreamWindowReduce(TimeWindows.of(MsgProcessorSupplier.getWindowSize()), "dialogHistory", (dialogData1, dialogData2) -> dialogData1))
                .addGlobalStore(Stores.create("dialogStatistic").withStringKeys().withDoubleValues().persistent().enableCaching().disableLogging().build(),
                        "statisticSource", new StringDeserializer(), new DoubleDeserializer(), "statisticTopic", "statisticProcessor",
                        new KStreamReduce("dialogStatistic", (value1, value2) -> (value1 == null ? value2 : (Double) value1 + (value2 == null ? 0 : (Double) value2))));

        this.kafkaStreams = new KafkaStreams(topologyBuilder, KafkaPropertiesConfigure.getConfig());

        log.info("Dialog message listener started");

        executor.scheduleAtFixedRate(() -> {
            if (!kafkaStreams.state().isRunning()) {
                kafkaStreams.close();
                kafkaStreams = new KafkaStreams(topologyBuilder, KafkaPropertiesConfigure.getConfig());
                kafkaStreams.start();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
}
