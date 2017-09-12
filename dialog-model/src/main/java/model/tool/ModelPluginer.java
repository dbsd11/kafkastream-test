package model.tool;

import model.Model;
import model.node.NodeManager;

/**
 * Created by BSONG on 2017/9/10.
 */
public class ModelPluginer {
    private static final NodeManager nodeManager = NodeManagerHolder.getInstance();

    private ModelPluginer() {
    }

    public static void addPlugin(Model model, String parentNodeName, String newNodeName) {
        nodeManager.createNode(newNodeName, parentNodeName, model);
    }

    public static void dePlugin(String parentNodeName, String nowNodeName) {
        nodeManager.removeNode(nowNodeName, parentNodeName);
    }
}
