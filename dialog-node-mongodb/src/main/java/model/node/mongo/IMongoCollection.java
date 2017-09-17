package model.node.mongo;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by BSONG on 2017/9/10.
 */
public class IMongoCollection {
    private String name;

    public IMongoCollection(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("can't get mongo collection with empty name");
        }
        this.name = name;
        CollectionNameHolder.set(name);
    }

    public static IMongoCollection get(String name) {
        return new IMongoCollection(name);
    }

    public IMongoDocument findById(ObjectId id) {
        return MongoUtil.getTemplate().findById(id, IMongoDocument.class);
    }

    public IMongoDocument findOne(Query query) {
        return MongoUtil.getTemplate().findOne(query, IMongoDocument.class);
    }

    public List<IMongoDocument> find(Query query) {
        return MongoUtil.getTemplate().find(query, IMongoDocument.class);
    }

    public List<IMongoDocument> findAll(Query query) {
        return MongoUtil.getTemplate().findAll(IMongoDocument.class);
    }

    public void insert(Object obj) {
        MongoUtil.getTemplate().insert(IMongoDocument.fromObj(obj));
    }

    public void insertAll(Collection<Object> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return;
        }
        MongoUtil.getTemplate().insertAll(objects.stream().map(IMongoDocument::fromObj).collect(Collectors.toList()));
    }

    public long updateFirst(Query query, Update update) {
        return MongoUtil.getTemplate().updateFirst(query, update, IMongoDocument.class).getModifiedCount();
    }

    public long updateMulti(Query query, Update update) {
        return MongoUtil.getTemplate().updateMulti(query, update, IMongoDocument.class).getMatchedCount();
    }

    public ObjectId upsert(Query query, Update update) {
        return MongoUtil.getTemplate().upsert(query, update, IMongoDocument.class).getUpsertedId().asObjectId().getValue();
    }

    public long remove(Query query) {
        return MongoUtil.getTemplate().remove(query, IMongoDocument.class).getDeletedCount();
    }

    public List<IMongoDocument> aggregate(AggregationOperation ... operations ){
        return MongoUtil.getTemplate().aggregate(new TypedAggregation(IMongoDocument.class, operations), IMongoDocument.class).getMappedResults();
    }
}
