package task.model;

import model.Model;

/**
 * Created by bdiao on 17/10/19.
 */
public class BaseFeatureModel implements Model<String, String[]> {

    @Override
    public String[] compute(String input, Object... params) {
        return new String[0];
    }
}
