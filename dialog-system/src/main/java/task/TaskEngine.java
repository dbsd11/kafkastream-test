package task;

import flow.dto.ActionDto;
import flow.dto.FlowDto;
import flow.dto.ResultDto;
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
        if (flowDto instanceof ResultDto) {
            return;
        }

        ActionDto actionDto = (ActionDto) flowDto;

        toolList.stream().anyMatch(tool -> {
            String[] features = tool.getModel().compute(actionDto.getContent());
            if (features != null) {
                actionDto.putProp(Constants.TASK_TYPE, TaskType.TASK);
                actionDto.putProp(Constants.ACTION_TASK, tool);
                actionDto.putProp(Constants.TASK_FEATURE, features);
                return true;
            }
            return false;
        });
    }
}
