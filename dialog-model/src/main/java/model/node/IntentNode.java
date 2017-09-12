package model.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.IntentionModel;
import node.NodeImpl;

/**
 * Created by BSONG on 2017/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentNode extends NodeImpl {
    private String name;
    private IntentionModel model;
}
