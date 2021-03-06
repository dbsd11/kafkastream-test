package dialog.tools;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

/**
 * Created by BSONG on 2017/8/7.
 */
public class KafkaPropertiesConfigure {

    private static Properties properties = new Properties() {{
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        put(StreamsConfig.APPLICATION_ID_CONFIG, "1");
        put(StreamsConfig.CONSUMER_PREFIX + ConsumerConfig.GROUP_ID_CONFIG, "0");
        put(StreamsConfig.PRODUCER_PREFIX + ProducerConfig.CLIENT_ID_CONFIG, "0");
        put(StreamsConfig.PRODUCER_PREFIX + ProducerConfig.BATCH_SIZE_CONFIG, "100");
    }};

    private static Properties producerProperties = new Properties() {{
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        put(StreamsConfig.APPLICATION_ID_CONFIG, "1");
        put(StreamsConfig.PRODUCER_PREFIX + ProducerConfig.CLIENT_ID_CONFIG, "0");
        put(StreamsConfig.PRODUCER_PREFIX + ProducerConfig.BATCH_SIZE_CONFIG, "100");
        put(StreamsConfig.PRODUCER_PREFIX+ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        put(StreamsConfig.PRODUCER_PREFIX+ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    }};

    private static Properties consumerProperties = new Properties() {{
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        put(StreamsConfig.APPLICATION_ID_CONFIG, "1");
        put(StreamsConfig.PRODUCER_PREFIX + ProducerConfig.CLIENT_ID_CONFIG, "0");
        put(StreamsConfig.CONSUMER_PREFIX + ConsumerConfig.GROUP_ID_CONFIG, "0");
        put(StreamsConfig.CONSUMER_PREFIX + ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        put(StreamsConfig.CONSUMER_PREFIX + ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    }};

    public static StreamsConfig getConfig() {

        return new StreamsConfig(properties);
    }

    public static StreamsConfig getConsumerConfig() {

        return new StreamsConfig(consumerProperties);
    }

    public static StreamsConfig getProducerConfig() {

        return new StreamsConfig(producerProperties);
    }
}
