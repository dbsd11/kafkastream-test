package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
public class TaskDto extends RollbackAbleFlowDto {

    @Override
    public void getProps(Map<String, Object> props) {

    }

    @Override
    public FlowDto next() {
        return null;
    }
}
