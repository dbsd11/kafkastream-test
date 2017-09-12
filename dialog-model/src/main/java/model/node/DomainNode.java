package model.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.DomainModel;
import node.NodeImpl;

/**
 * Created by BSONG on 2017/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainNode extends NodeImpl {
    private String name;
    private DomainModel model;

    @Override
    public boolean isLast() {
        return super.isLast() || relates()[0] instanceof IntentNode;
    }
}
