package flow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.PropsWarper;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto extends PropsWarper implements FlowDto {

    private Object response;

    public FlowDto next() {
        return null;
    }
}
