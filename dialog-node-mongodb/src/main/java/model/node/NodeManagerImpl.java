package model.node;

import model.Model;
import model.node.mongo.IMongoCollection;
import model.node.mongo.IMongoDocument;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by BSONG on 2017/9/12.
 */
abstract class TreeNodeManager4Mongodb implements NodeManager {
    private static final String NODE_COLLECTION = "model_node";
    private static final String NODE_NAME = "name";
    private static final String NODE_MODEL = "model";
    private static final String NODE_RELATES = "relates";
    private static final String NODE_FATHER = "father";
    private static final String NODE_CHILDS = "childs";

    @Override
    public Node createNode(String name, Node node, Object... params) {
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("create node name is null");
        }
        if (params == null || params.length != 1) {
            throw new RuntimeException("create node params is null or length != 1");
        }

        IMongoDocument nodeDocument = new IMongoDocument();
        nodeDocument.putIfAbsent(NODE_NAME, node.getName());
        nodeDocument.putIfAbsent(NODE_MODEL, node.getModel().serialBytes());
        nodeDocument.putIfAbsent(NODE_FATHER, params[0]);
        nodeDocument.putIfAbsent(NODE_CHILDS, "[]");

        IMongoDocument fatherDocument = IMongoCollection.get(NODE_COLLECTION).findOne(BasicQuery.query(Criteria.where(NODE_NAME).is(nodeDocument.get(NODE_FATHER))));
        Set<String> childs = new HashSet<>(fatherDocument.get(NODE_CHILDS) == null ? Collections.emptyList() : (List) fatherDocument.get(NODE_CHILDS));
        childs.add(name);
        IMongoCollection.get(NODE_COLLECTION).updateFirst(BasicQuery.query(Criteria.where(NODE_NAME).is(fatherDocument.get(NODE_NAME))),
                Update.update(NODE_CHILDS, childs.toArray()));

        IMongoCollection.get(NODE_COLLECTION).insert(nodeDocument);
        return buildNode(nodeDocument);
    }

    @Override
    public Node getNode(String name, Object... params) {
        List<IMongoDocument> documentList = IMongoCollection.get(NODE_COLLECTION).aggregate(Aggregation.lookup(NODE_COLLECTION, NODE_CHILDS, NODE_NAME, NODE_CHILDS), Aggregation.match(Criteria.where(NODE_NAME).is(name)));
        if (CollectionUtils.isEmpty(documentList)) {
            return null;
        }

        return buildNode(documentList.get(0));
    }

    @Override
    public void removeNode(String name, Object... params) {
        IMongoDocument fatherDocument = IMongoCollection.get(NODE_COLLECTION).findOne(BasicQuery.query(Criteria.where(NODE_CHILDS).elemMatch(Criteria.byExample(name))));
        Set<String> childs = new HashSet<>(fatherDocument.get(NODE_CHILDS) == null ? Collections.emptyList() : (List) fatherDocument.get(NODE_CHILDS));
        childs.remove(name);
        IMongoCollection.get(NODE_COLLECTION).updateFirst(BasicQuery.query(Criteria.where(NODE_NAME).is(fatherDocument.get(NODE_NAME))),
                Update.update(NODE_CHILDS, childs.toArray()));

        IMongoCollection.get(NODE_COLLECTION).remove(BasicQuery.query(Criteria.where(NODE_NAME).is(name)));
    }

    Node buildNode(IMongoDocument document) {
        Node node = new Node() {
            @Override
            public String getName() {
                return document.get(NODE_NAME).toString();
            }

            @Override
            public Model getModel() {
                try {
                    Object modelData = document.get(NODE_MODEL);
                    if (modelData instanceof byte[]) {
                        return (Model) new ObjectInputStream(new ByteArrayInputStream((byte[]) modelData)).readObject();
                    }
                    List<Double> modelBytes = (List<Double>) modelData;
                    ByteBuffer bb = ByteBuffer.allocate(modelBytes.size());
                    modelBytes.forEach(byteDouble -> bb.put(byteDouble.byteValue()));
                    bb.flip();
                    return (Model) new ObjectInputStream(new ByteArrayInputStream(bb.array())).readObject();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public Node[] relates() {
                return childs();
            }

            public String father() {
                return document.get(NODE_FATHER).toString();
            }

            public Node[] childs() {
                List<IMongoDocument> childDocumentList = (List<IMongoDocument>) document.get(NODE_CHILDS);
                return childDocumentList.stream().map(iMongoDocument -> buildNode(iMongoDocument)).collect(Collectors.toList()).toArray(new Node[0]);
            }
        };
        node.getModel();
        return node;
    }
}

public abstract class NodeManagerImpl extends TreeNodeManager4Mongodb {

}