package util;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by BSONG on 2017/8/12.
 */
public class BeanDelegator<T> {

    private static volatile Map<Class, BeanDelegator<?>> intanceMap;

    private T delegatee;

    BeanDelegator(T delegatee) {
        this.delegatee = delegatee;
    }

    public static <E> void delegate(E delegatee) {
        if (intanceMap == null) {
            intanceMap = new ConcurrentHashMap<>();
        }
        intanceMap.put((Class) ((ParameterizedType) delegatee.getClass().getGenericInterfaces()[0]).getRawType(), new BeanDelegator<>(delegatee));
    }

    public static <E> void delegate(Class<E> cls, E delegatee) {
        if (intanceMap == null) {
            intanceMap = new ConcurrentHashMap<>();
        }
        intanceMap.put(cls, new BeanDelegator<>(delegatee));
    }

    public static <E> E get(Class<E> delegateeClass) {
        return ((BeanDelegator<E>) intanceMap.get(delegateeClass)).delegatee;
    }
}
