package node;

import com.google.gson.annotations.Expose;
import model.node.Node;

import java.util.Collection;

/**
 * Created by BSONG on 2017/6/18.
 */
abstract class TreeNode implements Node {
    @Expose
    private TreeNode father;
    @Expose
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

    protected NodeImpl() {
    }

    @Override
    public Node[] relates() {
        return super.childs();
    }
}

