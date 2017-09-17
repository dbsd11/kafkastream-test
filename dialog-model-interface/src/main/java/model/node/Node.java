package model.node;

import com.google.gson.annotations.SerializedName;
import model.Model;

import java.io.Serializable;

/**
 * Created by BSONG on 2017/8/8.
 */
public interface Node extends Serializable {
    long serialVersionUID = -1L;

    @SerializedName("name")
    String getName();

    @SerializedName("model")
    Model getModel();

    @SerializedName("relates")
    default Node[] relates() {
        return new Node[0];
    }
}
