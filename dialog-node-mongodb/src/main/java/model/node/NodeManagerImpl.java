package model.node;

import model.Model;
import model.node.mongo.IMongoCollection;
import model.node.mongo.IMongoDocument;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by BSONG on 2017/9/12.
 */
abstract class NodeManager4Mongodb implements NodeManager {
    private static final String NODE_COLLECTION = "model_node";
    private static final String NODE_NAME = "name";
    private static final String NODE_FATHER = "father";
    private static final String NODE_MODEL = "model";
    private static final String NODE_CHILDS = "childs";


    @Override
    public Node createNode(String name, Object... params) {
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("create node name is null");
        }
        if (params == null || params.length != 2) {
            throw new RuntimeException("create node params is null or length != 3");
        }

        Node node = new Node() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Model getModel() {
                return (Model) params[1];
            }

            @Override
            public Node[] relates() {
                return null;
            }
        };

        IMongoDocument document = new IMongoDocument();
        document.put(NODE_NAME, node.getName());
        document.put(NODE_FATHER, params[0]);
        document.put(NODE_MODEL, node.getModel().serialBytes());
        document.put(NODE_CHILDS, "[]");

        IMongoCollection.get(NODE_COLLECTION).insert(document);
        return node;
    }

    @Override
    public Node getNode(String name, Object... params) {
        List<IMongoDocument> documentList = IMongoCollection.get(NODE_COLLECTION).aggregate(Aggregation.unwind(NODE_CHILDS), Aggregation.lookup(NODE_COLLECTION, NODE_CHILDS, NODE_NAME, NODE_CHILDS), Aggregation.match(Criteria.where(NODE_NAME).is(name)));
        if (CollectionUtils.isEmpty(documentList)) {
            return null;
        }

        return buildNode(documentList.get(0));
    }

    @Override
    public void removeNode(String name, Object... params) {

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
                    return (Model) new ObjectInputStream(new ByteArrayInputStream(document.get(NODE_MODEL).toString().getBytes())).readObject();
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
        return node;
    }
}

public abstract class NodeManagerImpl extends NodeManager4Mongodb {

    protected NodeManagerImpl() {
    }
}