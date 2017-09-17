package model.tool;

import model.Model;
import model.node.NodeManager;
import node.NodeImpl;
import org.springframework.data.geo.Box;

/**
 * Created by BSONG on 2017/9/10.
 */
public class ModelPluginer {
    private static final NodeManager nodeManager = NodeManagerHolder.getInstance();

    private ModelPluginer() {
    }

    public static void addPlugin(String nodeName, Model model, Object... params) {
        NodeImpl node = new NodeImpl() {
            @Override
            public String getName() {
                return nodeName;
            }

            @Override
            public Model getModel() {
                return model;
            }
        };
        nodeManager.createNode(nodeName, node, params);
    }

    public static void dePlugin(String nodeName, Object... params) {
        nodeManager.removeNode(nodeName, params);
    }
}
