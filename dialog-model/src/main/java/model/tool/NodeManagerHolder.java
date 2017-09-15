package model.tool;

import model.node.NodeManager;
import model.node.NodeManagerImpl;

/**
 * Created by BSONG on 2017/9/12.
 */
class NodeManagerHolder extends NodeManagerImpl {

    private static volatile NodeManager instance = null;

    private NodeManagerHolder() {
    }

    static NodeManager getInstance() {
        if (instance == null) {
            instance = new NodeManagerHolder();
        }
        return instance;
    }
}
