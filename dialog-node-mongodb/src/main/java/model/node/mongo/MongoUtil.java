package model.node.mongo;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

/**
 * Created by bdiao on 17/9/15.
 */
public class MongoUtil {
    private static MongoDbFactory dbFactory;
    private static MongoMappingContext mappingContext;
    public static final MongoTemplate template;

    public static MongoTemplate getTemplate() {
        return template;
    }

    public static MongoCollection<IMongoDocument> getCollection(String name) {
        return (MongoCollection) template.getCollection(name);
    }

    static {
        dbFactory = new SimpleMongoDbFactory(new MongoClientURI("mongodb://127.0.0.1:27017/dialog"));
        mappingContext = new MongoMappingContext();
        mappingContext.setInitialEntitySet(Collections.singleton(IMongoDocument.class));
        mappingContext.initialize();
        template = new MongoTemplate(dbFactory, new MappingMongoConverter(new DefaultDbRefResolver(dbFactory), mappingContext));
    }
}
