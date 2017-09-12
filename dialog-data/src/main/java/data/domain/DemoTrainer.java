package data.domain;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneOptions;
import model.DomainModel;
import model.impl.CommonDomainModel;
import model.node.DomainNode;
import model.node.mongo.IMongoTemplate;
import model.node.mongo.MongoUtil;
import model.tool.ModelLoader;
import model.tool.ModelPluginer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by BSONG on 2017/9/10.
 */
public class DemoTrainer {
    private static final Logger LOG = LoggerFactory.getLogger(DemoTrainer.class);

    final static void buildDefault() {
        MongoCollection<IMongoTemplate> nodeCollection = MongoUtil.getDB("dialog").getCollection("model_node", IMongoTemplate.class);

        DomainNode rootNode = new DomainNode();
        rootNode.setName("root");
        rootNode.setModel(new DefaultDomainModel());

        nodeCollection.insertOne(IMongoTemplate.fromObj(rootNode), new InsertOneOptions());
        System.out.println(IMongoTemplate.fromObj(rootNode));
    }

    public static class DefaultDomainModel extends DomainModel {

        private static final MongoCollection nodeCollection = MongoUtil.getDB().getCollection("model_node", IMongoTemplate.class);

        @Override
        public DomainNode compute(String input, Object... params) {
            List<DomainNode> chids = new LinkedList<>();
            nodeCollection.aggregate(Arrays.asList(Aggregates.unwind("childs"), Aggregates.lookup("model_node", "childs", "name", "childs"), Aggregates.match(Filters.eq("father", "root")))).forEach((Block<IMongoTemplate>) iMongoTemplate -> {
                DomainNode domainNode = iMongoTemplate.getOrigin(DomainNode.class);
                DomainModel model = (DomainModel) ModelLoader.load(domainNode.getName());
                if (model == null) {
                    throw new RuntimeException("can't find domain model with name:" + domainNode.getName());
                }
                domainNode.setModel(model);
                if (model.compute(domainNode, input, params) != null) {
                    chids.add(domainNode);
                }
            });
            return chids.isEmpty() ? null : chids.get(Double.valueOf(Math.random() * chids.size()).intValue());
        }

        @Override
        public DomainNode compute(DomainNode node, String input, Object... params) {
            return null;
        }
    }

    public static void main(String[] args) {
//        buildDefault();

        CommonDomainModel newsModel = new CommonDomainModel();
        newsModel.setFeatures(Arrays.asList("新闻","news"));

        ModelPluginer.addPlugin(newsModel, "root", "news");
    }
}
