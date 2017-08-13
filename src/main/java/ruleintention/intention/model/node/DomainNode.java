package ruleintention.intention.model.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ruleintention.intention.model.DomainModel;

/**
 * Created by BSONG on 2017/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainNode extends TreeNode {
    private String name;
    private DomainModel model;

    public boolean isLast() {
        Node[] childs = childs();
        return childs == null || childs.length == 0 || (childs[0] instanceof IntentNode);
    }
}
