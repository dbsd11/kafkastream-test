import dialog.DialogListener;
import dialog.tools.KafkaPropertiesConfigure;
import org.apache.kafka.clients.ClientRequest;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.NetworkClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.network.PlaintextChannelBuilder;
import org.apache.kafka.common.network.Selectable;
import org.apache.kafka.common.network.Selector;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.processor.internals.RecordCollector;
import org.apache.kafka.streams.processor.internals.StreamThread;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by BSONG on 2017/9/18.
 */
public class MainRunner {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new KafkaProducer(KafkaPropertiesConfigure.getProducerConfig().getProducerConfigs("0")).send(new ProducerRecord("dialog", 1111l, "asdsad")).get();
        new Thread(new DialogListener()).start();

    }
}
