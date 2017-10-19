package task;

import flow.dto.ActionDto;
import flow.dto.FlowDto;
import flow.dto.ResultDto;

/**
 * Created by bdiao on 17/10/19.
 */
public class TaskEngine {

    public static void process(FlowDto flowDto) {
        if (flowDto instanceof ResultDto) {
            return;
        }

        ActionDto actionDto = (ActionDto) flowDto;


    }
}
