package node;

import model.node.Node;

import java.util.Collection;

/**
 * Created by BSONG on 2017/6/18.
 */
abstract class TreeNode implements Node {
    private TreeNode father;
    private Collection<TreeNode> childs;

    public TreeNode father() {
        return father;
    }

    public TreeNode[] childs() {
        if (childs == null) {
            return new TreeNode[0];
        }
        return childs.toArray(new TreeNode[0]);
    }

    public boolean isLast() {
        return childs == null || childs.isEmpty();
    }
}

public abstract class NodeImpl extends TreeNode {

    @Override
    public Node[] relates() {
        return super.childs();
    }
}

