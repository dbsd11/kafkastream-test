package ruleintention.intention.model;

/**
 * Created by BSONG on 2017/8/8.
 */
public interface Model<T, O> {

    O compute(T input, Object... params);
}
