package model.impl;

import model.DomainModel;
import model.IntentionModel;
import model.node.DomainNode;
import model.node.IntentNode;
import model.node.Node;
import model.node.mongo.IMongoCollection;
import model.node.mongo.IMongoDocument;
import model.tool.ModelLoader;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by BSONG on 2017/9/18.
 */
public class RootDomainModel extends DomainModel {

    private static final IMongoCollection nodeCollection = IMongoCollection.get("model_node");

    @Override
    public DomainNode compute(String input, Object... params) {
        List<DomainNode> chids = new LinkedList<>();
        nodeCollection.aggregate(Aggregation.lookup("model_node", "childs", "name", "childs"), Aggregation.match((Criteria.where("father").is("root")))).forEach(iMongoDocument -> {
            DomainNode domainNode = buildNode(iMongoDocument);
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

    DomainNode buildNode(IMongoDocument document) {
        DomainNode node = new DomainNode() {
            @Override
            public String getName() {
                return document.get("name").toString();
            }

            @Override
            public DomainModel getModel() {
                try {
                    Object modelData = document.get("model");
                    List<Double> modelBytes = (List<Double>) modelData;
                    ByteBuffer bb = ByteBuffer.allocate(modelBytes.size());
                    modelBytes.forEach(byteDouble -> bb.put(byteDouble.byteValue()));
                    bb.flip();
                    return (DomainModel) new ObjectInputStream(new ByteArrayInputStream(bb.array())).readObject();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public IntentNode[] relates() {
                List<org.bson.Document> childDocumentList = (List<org.bson.Document>) document.get("childs");
                return childDocumentList.stream().map(childDocument -> {
                    IntentNode intentNode = new IntentNode() {
                        @Override
                        public String getName() {
                            return childDocument.get("name").toString();
                        }

                        @Override
                        public IntentionModel getModel() {
                            try {
                                Object modelData = childDocument.get("model");
                                List<Double> modelBytes = (List<Double>) modelData;
                                ByteBuffer bb = ByteBuffer.allocate(modelBytes.size());
                                modelBytes.forEach(byteDouble -> bb.put(byteDouble.byteValue()));
                                bb.flip();
                                return (IntentionModel) new ObjectInputStream(new ByteArrayInputStream(bb.array())).readObject();
                            } catch (Exception e) {
                                return null;
                            }
                        }
                    };
                    return intentNode;
                }).collect(Collectors.toList()).toArray(new IntentNode[0]);
            }
        };
        return node;
    }
}