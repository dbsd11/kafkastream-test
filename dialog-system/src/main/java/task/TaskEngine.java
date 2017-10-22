package task;

import flow.dto.FlowDto;
import flow.dto.ResultDto;
import flow.dto.TaskDto;
import model.node.mongo.IMongoCollection;
import model.node.mongo.IMongoDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import ruleintention.rule.enums.TaskType;
import task.model.BaseFeatureModel;
import task.tool.BaseFeatureTool;
import task.tool.GetNewsTool;
import util.Constants;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bdiao on 17/10/19.
 */
public class TaskEngine {
    private static Logger LOG = LoggerFactory.getLogger(TaskEngine.class);

    private static List<BaseFeatureTool> toolList = new ArrayList<>(Collections.emptyList());

    static {
        IMongoCollection toolsCollection = new IMongoCollection("model_tools");
        List<IMongoDocument> mongoDocuments = toolsCollection.findAll(BasicQuery.query(Criteria.where("")));
        toolList = mongoDocuments.stream().map(document -> {
            BaseFeatureTool tool = null;
            String toolName = document.getString("name");
            if (toolName.equals("GetNews")) {
                tool = document.getOrigin(GetNewsTool.class);
            }
            try {
                tool.setModel((BaseFeatureModel) new ObjectInputStream(new ByteArrayInputStream(document.getString("model").getBytes())).readObject());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            return tool;
        }).collect(Collectors.toList());
    }

    public static void process(FlowDto flowDto) {
        TaskDto taskDto = (TaskDto) flowDto;

        boolean matchTaskType = toolList.stream().anyMatch(tool -> {
            String[] features = tool.getModel().compute(taskDto.getContent());
            if (features != null) {
                taskDto.putProp(Constants.TASK_TYPE, TaskType.TASK);
                taskDto.putProp(Constants.ACTION_TASK, tool);
                taskDto.putProp(Constants.TASK_FEATURE, features);
                return true;
            }
            return false;
        });

        if (!matchTaskType) {
            taskDto.putProp(Constants.TASK_TYPE, TaskType.UNDEFINED);
        }
    }

    public static FlowDto next(FlowDto flowDto) {
        TaskDto taskDto = (TaskDto) flowDto;

        TaskType taskType = TaskType.valueOf(taskDto.getProp(Constants.TASK_TYPE).toString());
        if (taskType == TaskType.UNDEFINED) {
            return null;
        }
        if (taskType == TaskType.TASK_NEW) {
            return buildResult(taskDto, taskDto.getProp(Constants.TASK_RESPONSE));
        }
        if (taskType == TaskType.TASK) {
            BaseFeatureTool tool = (BaseFeatureTool) taskDto.getProp(Constants.ACTION_TASK);
            Object response = tool.apply((String[]) taskDto.getProp(Constants.TASK_FEATURE));
            return buildResult(taskDto, response);
        }
        return null;
    }

    static ResultDto buildResult(TaskDto taskDto, Object response) {

        return (ResultDto) ResultDto.builder().response(response).build().props(taskDto.getProps());
    }
}
