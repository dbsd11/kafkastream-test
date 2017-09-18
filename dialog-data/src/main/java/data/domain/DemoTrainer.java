package data.domain;

import model.Model;
import model.impl.CommonDomainModel;
import model.impl.MEMIntentionModel;
import model.impl.RootDomainModel;
import model.node.DomainNode;
import model.node.mongo.IMongoCollection;
import model.tool.ModelLoader;
import model.tool.ModelPluginer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by BSONG on 2017/9/10.
 */
public class DemoTrainer implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(DemoTrainer.class);

    final static void buildDefault() {
        DomainNode rootNode = new DomainNode();
        rootNode.setName("root");
        rootNode.setModel(new RootDomainModel());
        IMongoCollection.get("model_node").insert(rootNode);
    }

    public static void main(String[] args) {
        buildDefault();

        CommonDomainModel newsModel = new CommonDomainModel();
        newsModel.setFeatures(Arrays.asList("新闻", "news"));

        ModelPluginer.addPlugin("news", newsModel, "root");
        MEMIntentionModel memIntentionModel = new MEMIntentionModel();
        ModelPluginer.addPlugin("empty", memIntentionModel, "news");
        Model model = ModelLoader.load("news");
        int i = 0;
    }
}
