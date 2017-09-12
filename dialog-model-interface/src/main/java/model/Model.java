package model;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by BSONG on 2017/8/8.
 */
public interface Model<T, O> extends Serializable {
    long serialVersionUID = -1L;

    O compute(T input, Object... params);

    default void load(byte[] modelContent) {
    }

    default void refresh() {
    }

    default byte[] serialBytes() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.close();
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return this.toString().getBytes();
        }
    }
}
