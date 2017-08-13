package flow.dto;

import util.PropsWarper;

/**
 * Created by BSONG on 2017/6/18.
 */
public abstract class RollbackAbleFlowDto extends PropsWarper implements FlowDto {
    private RollbackAbleFlowDto father;

    public RollbackAbleFlowDto() {
    }

    public FlowDto rollBack() {

        return father;
    }
}
