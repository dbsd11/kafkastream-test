package model.impl;

import model.IntentionModel;

/**
 * Created by BSONG on 2017/8/13.
 */
public class MEMIntentionModel extends IntentionModel {
    @Override
    public Double compute(String input, Object... params) {
        return 0d;
    }

    @Override
    public String getResponse(String content) {
        return "";
    }
}
