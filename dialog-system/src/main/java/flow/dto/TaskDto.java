package flow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ruleintention.rule.RuleEngine;
import task.TaskEngine;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto extends RollbackAbleFlowDto {

    private long timeStamp;
    private String content;
    private int state;

    @Override
    public FlowDto next() {
        TaskEngine.process(this);
        RuleEngine.process(this);

        return TaskEngine.next(this);
    }
}