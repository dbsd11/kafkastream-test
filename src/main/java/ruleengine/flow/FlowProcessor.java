package ruleengine.flow;

import data.ResponseData;
import dto.FlowDto;

/**
 * Created by BSONG on 2017/6/18.
 */
public interface FlowProcessor {

    FlowDto process(FlowDto flow);

    default ResponseData seqProcess(FlowDto flow) {
        if (flow == null) {
            throw new RuntimeException("can't process null flow");
        }
        FlowDto result = flow;
        while ((flow = flow.next()) != null) {
            result = flow;
        }
        return result.getResponseData();
    }
}
