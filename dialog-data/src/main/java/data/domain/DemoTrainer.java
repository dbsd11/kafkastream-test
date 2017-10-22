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
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

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

    final static void memNewsModel() throws IOException {
        Pattern sectionPattern = Pattern.compile("^\\[\\w+\\]&");
        int askLen = "ask".length();
        int answerLen = "answer".length();
        BufferedReader br = new BufferedReader(new InputStreamReader(DemoTrainer.class.getResourceAsStream("Dialog_Get_News.txt")));
        String line = br.readLine();
        while (line != null) {
            if (!line.matches(sectionPattern.pattern())) {
                continue;
            }
            while ((line= br.readLine())!=null){
                if(line.matches(sectionPattern.pattern())){
                    break;
                }

            }

        }
    }

    public static void main(String[] args) {
        IMongoCollection.get("model_node").remove(BasicQuery.query(Criteria.where("_id").exists(true)));

        buildDefault();

        CommonDomainModel newsModel = new CommonDomainModel();
        newsModel.setFeatures(Arrays.asList("新闻", "news"));

        ModelPluginer.addPlugin("news", newsModel, "root");
        MEMIntentionModel memIntentionModel = new MEMIntentionModel();
        memIntentionModel.setState("hello");
        memIntentionModel.setResponse("");
        ModelPluginer.addPlugin("news.empty", memIntentionModel, "news");
        Model model = ModelLoader.load("news");
        int i = 0;
    }

}
