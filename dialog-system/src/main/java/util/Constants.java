package util;

/**
 * Created by BSONG on 2017/8/12.
 */
public interface Constants {
    String ACTION_TYPE = "type";
    String ACTION_INTENTION = "intention";
    String ACTION_ID = "id";
    String ACTION_SERIALINDEX = "serialIndex";
    String ACTION_RESPONSE = "response";
    String ACTION_MODEL = "actionModel";
    String ACTION_STATE="state";

    String ACTION_TASK = "task";

    String TASK_TYPE = "taskType";
    String TASK_FEATURE = "taskFeature";
    String TASK_RESPONSE = "taskResponse";

    String ID = "id";
    String TENANT_ID = "tenantId";
    String ROBOT_ID = "robotId";

    long HISTORY_WINDOWSIZE = 30 * 24 * 3600;
    int ACTION_START_SERIALINDEX = 0;
}
