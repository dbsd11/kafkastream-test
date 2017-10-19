package dialog;

import data.DialogData;
import dialog.tools.JsonDeserializer;
import dialog.tools.JsonSerializer;
import dialog.tools.KafkaPropertiesConfigure;
import dialog.tools.MsgProcessorSupplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.apache.kafka.common.serialization.DoubleSerializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.addSource("dialogSource", new LongDeserializer(), dialogDataSerde.deserializer(), "dialog")
                .addProcessor("msgProcessor", new MsgProcessorSupplier(), "dialogSource")
                .addProcessor("emptyProcessor", new MsgProcessorSupplier(true), "dialogSource")
                .addGlobalStore(Stores.create("dialogHistory").withStringKeys().withValues(dialogDataSerde).persistent().windowed(100, 30 * 24 * 3600, 2, true).enableCaching().disableLogging().build(),
                        "historySource", new LongDeserializer(), dialogDataSerde.deserializer(), "historyTopic", "historyProcessor",
                        new KStreamWindowReduce(TimeWindows.of(MsgProcessorSupplier.getWindowSize()), "dialogHistory", (dialogData1, dialogData2) -> dialogData1))
                .addGlobalStore(Stores.create("dialogStatistic").withStringKeys().withValues(dialogDataSerde).persistent().enableCaching().disableLogging().build(),
                        "statisticSource", new StringDeserializer(), new DoubleDeserializer(), "statisticTopic", "statisticProcessor",
                        new KStreamReduce("dialogStatistic", (value1, value2) -> (value1 == null ? 0 : (Double) value1 + (value2 == null ? 0 : (Double) value2))))
                .addSink("msgSink", "dialog_statistic_out", new StringSerializer(), new DoubleSerializer(), "statisticProcessor");

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
