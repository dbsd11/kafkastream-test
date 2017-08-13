package flow.dto;

import java.io.Serializable;

/**
 * Created by BSONG on 2017/6/18.
 */
public interface FlowDto extends Serializable {
    long serialVersionUID = -1L;

    FlowDto next();
}
