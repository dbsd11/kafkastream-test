package model.impl;

import lombok.Getter;
import lombok.Setter;
import model.IntentionModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by BSONG on 2017/8/13.
 */
@Getter
@Setter
public class MEMIntentionModel extends IntentionModel {
    private String state;
    private Map<String, Double> stateRate;
    private String response;

    @Override
    public Double compute(String input, Object... params) {
        if (params != null && params.length != 0) {
            if (!this.state.equals(params[0])) {
                return 0d;
            }
        }
        if (stateRate == null || stateRate.isEmpty()) {
            return 0d;
        }
        List<Map.Entry<String, Double>> stateRateList = new LinkedList<>(stateRate.entrySet());
        Collections.sort(stateRateList, (o1, o2) -> o1.getValue() == null ? -1 : o2.getValue() == null ? 1 : o2.getValue().compareTo(o1.getValue()));
        Map.Entry<String, Double> maxStateRateEntry = stateRateList.get(0);

        this.state = maxStateRateEntry.getKey();
        Double score = maxStateRateEntry.getValue();
        return score;
    }

    @Override
    public String getResponse(String content) {
        return response;
    }
}
