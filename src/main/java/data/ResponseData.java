package data;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BSONG on 2017/6/18.
 */
@Getter
@NoArgsConstructor
public class ResponseData implements Serializable {
    private static final long serialVersionUID = -1L;

    private Map<String, Object> props = new HashMap<>();

    public ResponseData props(Map<String, Object> props) {
        if (props == null) {
            return this;
        }
        this.props.putAll(props);
        return this;
    }

    public ResponseData put(String key, Object value) {
        this.props.put(key, value);
        return this;
    }

    public Object get(String key) {
        return props.get(key);
    }
}
