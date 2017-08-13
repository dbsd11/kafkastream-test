package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by BSONG on 2017/6/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogData implements Serializable {

    private int tenantId;
    private String robotId;
    private String content;

}
