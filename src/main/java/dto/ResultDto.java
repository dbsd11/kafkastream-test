package dto;

import data.ResponseData;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
public class ResultDto implements FlowDto {

    public FlowDto next() {
        return null;
    }

    public FlowDto rollBack() {
        return null;
    }

    public ResponseData getResponseData() {
        return null;
    }
}
