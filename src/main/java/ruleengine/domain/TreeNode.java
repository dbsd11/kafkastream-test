package ruleengine.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BSONG on 2017/6/18.
 */
interface Node extends Serializable {
    long serialVersionUID = -1L;

    Node father();

    Node[] childs();
}

public abstract class TreeNode implements Node {
    private TreeNode father;
    private List<TreeNode> childs = new ArrayList<>();

    public TreeNode() {
    }

    @Override
    public Node father() {

        return father;
    }

    @Override
    public Node[] childs() {

        return childs.toArray(new Node[0]);
    }
}