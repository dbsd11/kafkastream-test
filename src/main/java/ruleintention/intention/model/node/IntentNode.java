package ruleintention.intention.model.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ruleintention.intention.model.IntentionModel;

/**
 * Created by BSONG on 2017/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentNode extends TreeNode {
    private String name;
    private IntentionModel model;

}
