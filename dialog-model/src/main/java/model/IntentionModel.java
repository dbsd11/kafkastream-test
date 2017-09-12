package model;

/**
 * Created by BSONG on 2017/8/8.
 */
public abstract class IntentionModel implements Model<String, Double> {

    public abstract String getResponse(String content);
}
