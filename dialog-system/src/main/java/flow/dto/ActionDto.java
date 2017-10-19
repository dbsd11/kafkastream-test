package flow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ruleintention.intention.DomainIntentEngine;
import ruleintention.rule.RuleEngine;
import task.TaskEngine;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActionDto extends RollbackAbleFlowDto {

    private long timeStamp;
    private String content;

    @Override
    public FlowDto next() {
        DomainIntentEngine.process(this);
        TaskEngine.process(this);
        RuleEngine.process(this);
        return DomainIntentEngine.next(this);
    }
}
