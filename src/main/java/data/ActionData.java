package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by BSONG on 2017/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionData implements Serializable{

    private long id;

    private long timeStamp;
    private int serialIndex;
    private int tenantId;
    private String robotId;
    private String content;
    private Map<String, Object> attrs;
}
