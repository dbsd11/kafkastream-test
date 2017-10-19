package task.tool;

import java.util.function.Function;

/**
 * Created by bdiao on 17/10/19.
 */
public abstract class BaseFeatureTool implements Function<String[], Object> {

    public abstract Object apply(String... features);
}
