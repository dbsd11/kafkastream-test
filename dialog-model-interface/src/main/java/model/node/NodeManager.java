package model.node;

/**
 * Created by BSONG on 2017/9/12.
 */
public interface NodeManager {

    Node createNode(String name, Node node, Object... params);

    Node getNode(String name, Object... params);

    void removeNode(String name, Object... params);
}
