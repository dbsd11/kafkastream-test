package model;

import model.node.DomainNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BSONG on 2017/8/8.
 */
public abstract class DomainModel implements Model<String, DomainNode> {
    private static Map<String, DomainNode> tempMap;

    @Override
    public DomainNode compute(String input, Object... params) {
        if (params == null || params.length == 0 || !(params[0] instanceof DomainNode)) {
            throw new RuntimeException("this domain model compute need DomainNode param");
        }

        DomainNode result = compute((DomainNode) params[0], input, params);
        if (tempMap == null) {
            tempMap = new HashMap<>();
        }
        tempMap.put(input, result);
        return result;
    }

    public abstract DomainNode compute(DomainNode node, String input, Object... params);
}
