package util;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by BSONG on 2017/8/7.
 */
@Getter
@NoArgsConstructor
public class PropsWarper {
    private Map<String, Object> props = new HashMap<>();

    public PropsWarper props(Map<String, Object> props) {
        this.props.putAll(props);
        return this;
    }

    public PropsWarper putProp(String key, Object value) {
        props.put(key, value);
        return this;
    }

    public Object getProp(String key) {
        return props.get(key);
    }

    public int getInt(String key) {
        return (Integer) props.getOrDefault(key, 0);
    }

    public long getLong(String key) {
        return (Long) props.getOrDefault(key, 0L);
    }

    public double getDouble(String key) {
        return (Double) props.getOrDefault(key, 0.0d);
    }

    public String getString(String key) {
        return (String) props.getOrDefault(key, null);
    }

    public Set<String> keySet() {
        return props.keySet();
    }

    public boolean containsKey(String key) {
        return props.containsKey(key);
    }
}
