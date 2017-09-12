package data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.PropsWarper;

import java.io.Serializable;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData extends PropsWarper implements Serializable {
    private static final long serialVersionUID = -1L;

    private long actionId;
    private String actionContent;
    private int tenantId;
    private String robotId;
    private long timestamp;
    private Object response;
}
