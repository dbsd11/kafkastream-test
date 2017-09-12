package flow;

import data.ActionData;
import data.ResponseData;
import flow.dto.ActionDto;
import flow.dto.FlowDto;
import flow.dto.ResultDto;
import util.Constants;

/**
 * Created by BSONG on 2017/8/7.
 */
public class DialogFlowProcessor implements FlowProcessor {

    @Override
    public FlowDto process(FlowDto flow) {
        if (flow == null) {
            return null;
        }
        FlowDto next = flow.next();
        if (next == null) {
            return flow;
        }
        return process(next);
    }

    public ResponseData process(ActionData actionData) {
        ActionDto actionDto = ActionDto.builder().content(actionData.getContent()).timeStamp(actionData.getTimeStamp()).build();
        actionDto.putProp(Constants.ACTION_ID, actionData.getId());
        actionDto.putProp(Constants.TENANT_ID, actionData.getTenantId());
        actionDto.putProp(Constants.ROBOT_ID, actionData.getRobotId());
        FlowDto result = process(actionDto);
        if (result == null) {
            return buildDefaultResponse(actionData);
        }
        return (ResponseData) buildResponse(actionData, ((ResultDto) result).getResponse()).props(((ResultDto) result).getProps());
    }

    ResponseData buildDefaultResponse(ActionData actionData) {
        return ResponseData.builder().actionId(actionData.getId()).actionContent(actionData.getContent())
                .tenantId(actionData.getTenantId()).robotId(actionData.getRobotId()).timestamp(actionData.getTimeStamp())
                .response("抱歉，咱不知道怎样回答")
                .build();
    }

    ResponseData buildResponse(ActionData actionData, Object response) {
        return ResponseData.builder().actionId(actionData.getId()).actionContent(actionData.getContent())
                .tenantId(actionData.getTenantId()).robotId(actionData.getRobotId()).timestamp(actionData.getTimeStamp())
                .response(response)
                .build();
    }
}
