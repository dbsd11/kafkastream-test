package model.node;

import com.mongodb.client.model.InsertOneOptions;
import model.Model;
import model.node.mongo.IMongoTemplate;
import model.node.mongo.MongoUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ranges.RangeException;

/**
 * Created by BSONG on 2017/9/12.
 */
abstract class NodeManager4Mongodb implements NodeManager {
    @Override
    public Node createNode(String name, Object... params) {
        if(StringUtils.isEmpty(name)){
            throw new RuntimeException("create node name is null");
        }
        if (params == null|| params.length != 3) {
            throw new RuntimeException("create node params is null or length != 3");
        }

        Node node = new Node() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Model getModel() {
                return (Model)params[2];
            }

            @Override
            public Node[] relates() {
                return new Node[0];
            }
        };
        MongoUtil.getCollection().insertOne(IMongoTemplate.fromObj(node), new InsertOneOptions());

        return null;
    }

    @Override
    public Node getNode(String name, Object... params) {
        return null;
    }

    @Override
    public void removeNode(String name, Object... params) {

    }
}

public abstract class NodeManagerImpl extends NodeManager4Mongodb {
}