package dto;

import data.ResponseData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BSONG on 2017/6/18.
 */
public abstract class RollbackAbleFlowDto implements FlowDto {
    private RollbackAbleFlowDto father;

    public RollbackAbleFlowDto() {
    }

    @Override
    public FlowDto rollBack() {

        return father;
    }

    @Override
    public ResponseData getResponseData() {
        Map<String, Object> props = new HashMap<>();
        getProps(props);
        if (father != null) {
            father.getProps(props);
        }
        return new ResponseData().props(props);
    }

    public abstract void getProps(Map<String, Object> props);
}
