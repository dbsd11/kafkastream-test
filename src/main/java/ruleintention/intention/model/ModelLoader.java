package ruleintention.intention.model;

import org.apache.commons.lang3.StringUtils;

/**
 * The model loader from database will be distributed
 * Created by BSONG on 2017/8/11.
 */
public class ModelLoader {

    public static Model load(String name) {
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("unsupported");
        }
        return null;
    }

    public static DomainModel loadDefaultDomainModel() {
        return null;
    }
}
