package util;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by BSONG on 2017/8/12.
 */
public class BeanDelegator<T> {

    private static volatile Map<String, BeanDelegator<?>> intanceMap;

    private T delegatee;

    BeanDelegator(T delegatee) {
        this.delegatee = delegatee;
    }

    public static <E> void delegate(E delegatee) {
        delegate((Class) ((ParameterizedType) delegatee.getClass().getGenericInterfaces()[0]).getRawType(), delegatee);
    }

    public static <E> void delegate(Class<E> cls, E delegatee) {
        delegate(cls.getTypeName(), delegatee);
    }

    public static <E> void delegate(String name, E delegatee) {
        if (intanceMap == null) {
            intanceMap = new ConcurrentHashMap<>();
        }
        intanceMap.put(name, new BeanDelegator<>(delegatee));
    }

    public static <E> E get(Class<E> delegateeClass) {
        return get(delegateeClass.getTypeName());
    }

    public static <E> E get(String name) {
        return ((BeanDelegator<E>) intanceMap.get(name)).delegatee;
    }
}
