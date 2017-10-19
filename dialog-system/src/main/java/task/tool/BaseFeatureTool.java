package task.tool;

import lombok.Getter;
import lombok.Setter;
import task.model.BaseFeatureModel;

import java.util.function.Function;

/**
 * Created by bdiao on 17/10/19.
 */
@Getter
@Setter
public abstract class BaseFeatureTool implements Function<String[], Object> {

    private String name;

    private BaseFeatureModel model;

    public abstract Object apply(String... features);
}
