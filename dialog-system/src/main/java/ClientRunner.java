import com.google.gson.Gson;
import data.DialogData;
import dialog.DialogListener;
import dialog.tools.KafkaPropertiesConfigure;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by BSONG on 2017/9/18.
 */
public class ClientRunner {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new KafkaProducer(KafkaPropertiesConfigure.getProducerConfig().getProducerConfigs("0")).send(new ProducerRecord("dialog", System.currentTimeMillis(), new Gson().toJson(new DialogData(0, "81d4bdc6-e295-46e2-a648-4ee8f14418da", "请问一个问题")))).get();
    }
}
