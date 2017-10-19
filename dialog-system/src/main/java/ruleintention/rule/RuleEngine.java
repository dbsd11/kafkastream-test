package ruleintention.rule;

import data.ActionData;
import flow.dto.ActionDto;
import flow.dto.FlowDto;
import flow.dto.ResultDto;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import ruleintention.rule.enums.ActionType;
import ruleintention.rule.enums.TaskType;
import util.BeanDelegator;
import util.Constants;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BSONG on 2017/6/18.
 */
public class RuleEngine {
    private static Pattern startDialogRule = Pattern.compile("您好.*|你好.*|hello|.*在吗|.*问题.*|");
    private static Pattern endDialogRule = Pattern.compile("结束.*|退出.*|end");
    private static Pattern taskRule = Pattern.compile("请问.*|问.*|我要.*|.*\\sask\\s.*");
    private static List<String> startDialogResponse = Arrays.asList("在呢", "请说", "您好");
    private static List<String> endDialogResponse = Arrays.asList("再见", "我会想你的", "多找我聊天啊");
    private static volatile WindowStore<String, List<ActionData>> history;
    private static volatile KeyValueStore<String, Double> statistic;

    public static void process(FlowDto flowDto) {
        if (flowDto instanceof ResultDto) {
            return;
        }
        if (history == null) {
            history = BeanDelegator.get(WindowStore.class);
        }
        if (statistic == null) {
            statistic = BeanDelegator.get(KeyValueStore.class);
        }

        ActionDto actionDto = (ActionDto) flowDto;
        Double lastDialogTime = statistic.get(getDialogLastTimeKey(actionDto));
        WindowStoreIterator<List<ActionData>> iterator = history.fetch(getKey(actionDto), lastDialogTime == null ? 0 : lastDialogTime.intValue(), System.currentTimeMillis());
        AtomicInteger serialIndex = new AtomicInteger();
        String lastIntent = null;
        while (iterator.hasNext()) {
            List<ActionData> actionDataList = iterator.next().value;
            for (ActionData actionData : actionDataList) {
                if (actionData.getResponse().getInt(Constants.ACTION_SERIALINDEX) == Constants.ACTION_START_SERIALINDEX) {
                    serialIndex.set(Constants.ACTION_START_SERIALINDEX);
                } else {
                    serialIndex.incrementAndGet();
                }
            }
            if (!iterator.hasNext() && actionDataList.size() != 0) {
                lastIntent = actionDataList.get(actionDataList.size() - 1).getResponse().getString(Constants.ACTION_INTENTION);
                break;
            }
        }

        ActionType actionType = ActionType.valueOf(actionDto.getString(Constants.ACTION_TYPE));
        if (actionType == ActionType.UNDEFINED) {
            switch (recognizeAction(actionDto.getContent())) {
                case ACT_NEW:
                    actionDto.putProp(Constants.ACTION_TYPE, ActionType.ACT_NEW);
                    actionDto.putProp(Constants.ACTION_SERIALINDEX, Constants.ACTION_START_SERIALINDEX);
                    actionDto.putProp(Constants.ACTION_RESPONSE, startDialogResponse.get(Double.valueOf(Math.random() * startDialogResponse.size()).intValue()));
                    break;
                case ACT_OUT:
                    actionDto.putProp(Constants.ACTION_TYPE, ActionType.ACT_OUT);
                    actionDto.putProp(Constants.ACTION_SERIALINDEX, serialIndex.intValue());
                    actionDto.putProp(Constants.ACTION_RESPONSE, endDialogResponse.get(Double.valueOf(Math.random() * endDialogResponse.size()).intValue()));
                    break;
                default:
                    actionDto.putProp(Constants.ACTION_SERIALINDEX, -1);
                    break;
            }
            return;
        }

        TaskType taskType = TaskType.valueOf(actionDto.getString(Constants.TASK_TYPE));
        if (taskType == TaskType.UNDEFINED) {
            switch (recognizeTask(actionDto.getContent())) {
                case TASK_NEW:
                    actionDto.putProp(Constants.TASK_TYPE, TaskType.TASK_NEW);
                    actionDto.putProp(Constants.TASK_RESPONSE, "尚不支持该任务");
                    break;
                default:
                    break;
            }
        }

        if (actionDto.getString(Constants.ACTION_INTENTION).equals(lastIntent)) {
            actionDto.putProp(Constants.ACTION_SERIALINDEX, serialIndex.intValue());
        } else {
            //todo 跳出之前意图进入新意图对话check
            actionDto.putProp(Constants.ACTION_SERIALINDEX, Constants.ACTION_START_SERIALINDEX);
        }
    }

    public static ActionType recognizeAction(String content) {
        if (startDialogRule.matcher(content.toLowerCase()).matches()) {
            return ActionType.ACT_NEW;
        }
        if (endDialogRule.matcher(content.toLowerCase()).matches()) {
            return ActionType.ACT_OUT;
        }
        return ActionType.UNDEFINED;
    }

    public static TaskType recognizeTask(String content) {
        if (taskRule.matcher(content.toLowerCase()).matches()) {
            return TaskType.TASK_NEW;
        }
        return TaskType.UNDEFINED;
    }

    static String getKey(ActionDto actionDto) {

        return String.valueOf(Base64.getEncoder().encode(String.join("_", actionDto.getString(Constants.TENANT_ID), actionDto.getString(Constants.ROBOT_ID)).getBytes()));
    }

    static String getDialogLastTimeKey(ActionDto actionDto) {

        return String.valueOf(Base64.getEncoder().encode(String.join("_", actionDto.getString(Constants.TENANT_ID), actionDto.getString(Constants.ROBOT_ID), "dialoglasttime").getBytes()));
    }

    public static void main(String[] args) {
        Matcher matcher = startDialogRule.matcher("请假一个问题");
        System.out.println(matcher.groupCount());
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
