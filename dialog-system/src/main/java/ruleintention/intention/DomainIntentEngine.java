package ruleintention.intention;

import flow.dto.ActionDto;
import flow.dto.FlowDto;
import flow.dto.ResultDto;
import flow.dto.TaskDto;
import model.DomainModel;
import model.IntentionModel;
import model.node.DomainNode;
import model.node.IntentNode;
import model.tool.ModelLoader;
import org.apache.commons.lang3.StringUtils;
import ruleintention.rule.enums.ActionType;
import util.Constants;

/**
 * Created by BSONG on 2017/6/19.
 */
public class DomainIntentEngine {

    public static void process(FlowDto flowDto) {
        ActionDto actionDto = (ActionDto) flowDto;

        DomainModel domainModel = ModelLoader.loadDefaultDomainModel();
        DomainNode domain = domainModel.compute(actionDto.getContent());
        while (domain != null && !domain.isLast()) {
            domain = domain.getModel().compute(actionDto.getContent(), domain);
        }
        if (domain == null) {
            actionDto.putProp(Constants.ACTION_TYPE, ActionType.UNDEFINED);
            return;
        }

        IntentNode[] intentions = (IntentNode[]) domain.relates();
        IntentNode intent = null;
        double maxScore = 0;
        for (IntentNode intentNode : intentions) {
            double score = intentNode.getModel().compute(actionDto.getContent());
            if (score > maxScore) {
                maxScore = score;
                intent = intentNode;
            }
        }
        if (maxScore != 0) {
            actionDto.putProp(Constants.ACTION_TYPE, ActionType.ACT);
            actionDto.putProp(Constants.ACTION_INTENTION, intent.getName());
            actionDto.putProp(Constants.ACTION_MODEL, intent.getModel());
        }
    }

    public static FlowDto next(FlowDto flowDto) {
        ActionDto actionDto = (ActionDto) flowDto;

        ActionType actionType = ActionType.valueOf(actionDto.getString(Constants.ACTION_TYPE));
        if (actionType == ActionType.UNDEFINED) {
            return null;
        }
        if (actionType == ActionType.ACT_NEW || actionType == ActionType.ACT_OUT) {
            return buildResult(actionDto, actionDto.getString(Constants.ACTION_RESPONSE));
        }
        if (actionType == ActionType.FREECHAT) {
            return buildResult(actionDto, "您吃饭了吗，来聊会儿天");
        }
        if (actionType == ActionType.ACT) {
            Object response = ((IntentionModel) actionDto.getProp(Constants.ACTION_MODEL)).getResponse(actionDto.getContent());
            if (response != null && !StringUtils.isEmpty(response.toString())) {
                return buildResult(actionDto, response);
            }
            return TaskDto.builder().timeStamp(actionDto.getTimeStamp()).state(-1).content(actionDto.getContent()).build();
        }
        return null;
    }

    static ResultDto buildResult(ActionDto actionDto, Object response) {

        return (ResultDto) ResultDto.builder().response(response).build().props(actionDto.getProps());
    }
}
