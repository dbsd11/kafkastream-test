package model.tool;

import model.DomainModel;
import model.Model;
import model.node.Node;
import model.node.NodeManager;
import org.apache.commons.lang3.StringUtils;

/**
 * The model loader from database will be distributed
 * Created by BSONG on 2017/8/11.
 */
public class ModelLoader {
    private static final NodeManager nodeManager = NodeManagerHolder.getInstance();

    private static DomainModel defaultModel;
    private static long lastLoaded;

    private ModelLoader() {
    }

    public static Model load(String name, Object... params) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        Node node = nodeManager.getNode(name, params);
        return node == null ? null : node.getModel();
    }

    public static DomainModel loadDefaultDomainModel() {
        if (defaultModel == null || (System.currentTimeMillis() - lastLoaded > 10 * 60 * 1000)) {
            defaultModel = (DomainModel) nodeManager.getNode("root").getModel();
            lastLoaded = System.currentTimeMillis();
        }
        return defaultModel;
    }
}
