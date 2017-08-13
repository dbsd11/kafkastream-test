package ruleintention.intention.model.node;

import ruleintention.intention.model.Model;

import java.io.Serializable;

/**
 * Created by BSONG on 2017/8/8.
 */
public interface Node extends Serializable {
    long serialVersionUID = -1L;

    String getName();

    Model getModel();
}
