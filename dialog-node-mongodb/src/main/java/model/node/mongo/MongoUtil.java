package model.node.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

/**
 * Created by BSONG on 2017/9/10.
 */
public class MongoUtil {

    private static final MongoClient client = new MongoClient();

    public static MongoCollection<IMongoTemplate> getCollection() {
        return client.getDatabase("dialog").getCollection("model_node", IMongoTemplate.class);
    }
}
