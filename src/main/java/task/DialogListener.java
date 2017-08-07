package task;

import data.DialogData;
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
import task.tools.JsonDeserializer;
import task.tools.JsonSerializer;
import task.tools.KafkaPropertiesConfigure;
import task.tools.MsgProcessorSupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BSONG on 2017/8/7.
 */
@Slf4j
public class DialogListener implements Runnable {

    private KafkaStreams kafkaStreams;

    @Override
    public void run() {
        Serde<DialogData> dialogDataSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(DialogData.class));
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.addSource("dialogSource", new LongDeserializer(), dialogDataSerde.deserializer(), "dialog")
                .addProcessor("msgProcessor", new MsgProcessorSupplier(), "dialogSource")
                .addProcessor("emptyProcessor", new MsgProcessorSupplier(), "dialogSource")
                .addGlobalStore(Stores.create("dialogHistory").withStringKeys().withValues(ArrayList.class).persistent().windowed(100, 30 * 24 * 3600, 1, true).enableCaching().build().get(),
                        "dialogSource", new StringDeserializer(), new JsonDeserializer<>(ArrayList.class), "dialog", "msgProcessor",
                        new KStreamWindowReduce(TimeWindows.of(MsgProcessorSupplier.getWindowSize()), "dialogHistory", (dialogDataList1, dialogDataList2) -> new ArrayList<DialogData>() {{
                            addAll(dialogDataList1 == null ? Collections.emptyList() : (List<DialogData>) dialogDataList1);
                            addAll(dialogDataList2 == null ? Collections.emptyList() : (List<DialogData>) dialogDataList2);
                        }}))
                .addGlobalStore(Stores.create("dialogStatistic").withStringKeys().withValues(dialogDataSerde).persistent().enableCaching().build().get(),
                        "dialogSource", new StringDeserializer(), new DoubleDeserializer(), "dialog", "emptyProcessor",
                        new KStreamReduce("dialogStatistic", (value1, value2) -> (value1 == null ? 0 : (Double) value1 + (value2 == null ? 0 : (Double) value2))))
                .addSink("msgSink", "dialog_statistic_out", new StringSerializer(), new DoubleSerializer(), "emptyProcessor");

        this.kafkaStreams = new KafkaStreams(topologyBuilder, KafkaPropertiesConfigure.getConfig());
        this.kafkaStreams.start();

        log.info("Dialog message listener started");
    }
}
