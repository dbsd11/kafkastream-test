package flow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ruleintention.intention.DomainIntentEngine;
import ruleintention.rule.enums.TaskType;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto extends RollbackAbleFlowDto {

    private TaskType type;
    private String content;
    private int state;

    @Override
    public FlowDto next() {

        return DomainIntentEngine.next(this);
    }
}
