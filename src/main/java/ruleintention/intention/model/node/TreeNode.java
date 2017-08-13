package ruleintention.intention.model.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BSONG on 2017/6/18.
 */
public abstract class TreeNode implements Node {
    private TreeNode father;
    private List<TreeNode> childs = new ArrayList<>();

    public Node father() {

        return father;
    }

    public Node[] childs() {

        return childs.toArray(new Node[0]);
    }
}