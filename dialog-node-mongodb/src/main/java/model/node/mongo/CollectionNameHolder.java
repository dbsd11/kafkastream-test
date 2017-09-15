package model.node.mongo;

/**
 * Created by bdiao on 17/9/15.
 */
public class CollectionNameHolder {

    private static final ThreadLocal<String> value = new ThreadLocal<>();

    public static String get() {
        return CollectionNameHolder.value.get();
    }

    static void set(String value) {
        CollectionNameHolder.value.set(value);
    }
}
