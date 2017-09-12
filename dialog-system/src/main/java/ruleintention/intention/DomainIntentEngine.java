package ruleintention.intention;

import flow.dto.ActionDto;
import flow.dto.FlowDto;
import flow.dto.ResultDto;
import model.DomainModel;
import model.IntentionModel;
import model.node.DomainNode;
import model.node.IntentNode;
import model.tool.ModelLoader;
import ruleintention.rule.enums.ActionType;
import util.Constants;

/**
 * Created by BSONG on 2017/6/19.
 */
public class DomainIntentEngine {

    public static void process(FlowDto flowDto) {
        if (flowDto instanceof ResultDto) {
            return;
        }

        DomainModel domainModel = ModelLoader.loadDefaultDomainModel();
        ActionDto actionDto = (ActionDto) flowDto;
        DomainNode domain = domainModel.compute(actionDto.getContent());
        while (domain != null && !domain.isLast()) {
            domain = domain.getModel().compute(actionDto.getContent(), domain);
        }
        if (domain == null) {
            actionDto.putProp(Constants.ACTION_TYPE, ActionType.UNDEFINED);
            return;
        }
        actionDto.putProp(Constants.ACTION_TYPE, ActionType.ACT);

        IntentNode[] intentions = (IntentNode[]) domain.childs();
        IntentNode intent = null;
        double maxScore = Double.MIN_VALUE;
        for (IntentNode intentNode : intentions) {
            double score = intentNode.getModel().compute(actionDto.getContent());
            if (score > maxScore) {
                maxScore = score;
                intent = intentNode;
            }
        }
        actionDto.putProp(Constants.ACTION_INTENTION, intent.getName());
        actionDto.putProp(Constants.ACTION_MODEL, intent.getModel());
    }

    public static FlowDto next(FlowDto flowDto) {
        if (flowDto instanceof ResultDto) {
            return null;
        }

        ActionDto actionDto = (ActionDto) flowDto;
        ActionType actionType = ActionType.valueOf(actionDto.getString(Constants.ACTION_TYPE));
        if (actionType == ActionType.UNDEFINED) {
            return null;
        }
        if (actionType == ActionType.ACT_NEW || actionType == ActionType.ACT_OUT) {
            return buildResult(actionDto, actionDto.getString(Constants.ACTION_RESPONSE));
        }
        return buildResult(actionDto, getDialogResponse(actionDto));
    }

    static Object getDialogResponse(ActionDto actionDto) {

        return ((IntentionModel) actionDto.getProp(Constants.ACTION_MODEL)).getResponse(actionDto.getContent());
    }

    static ResultDto buildResult(ActionDto actionDto, Object Response) {

        return (ResultDto) ResultDto.builder().response(Response).build().props(actionDto.getProps()).props(actionDto.getProps());
    }
}
