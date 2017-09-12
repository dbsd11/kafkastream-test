package model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.DomainModel;
import model.node.DomainNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by BSONG on 2017/8/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonDomainModel extends DomainModel {

    private Collection<String> features = null;

    @Override
    public DomainNode compute(DomainNode node, String input, Object... params) {
        if (node == null) {
            throw new RuntimeException("need model node is null" + params[0]);
        }

        if (features == null || input == null || features.stream().noneMatch(feature -> input.contains(feature))) {
            return null;
        }

        if (node.isLast()) {
            return node;
        }

        List<DomainNode> childs = Arrays.asList((DomainNode[]) node.relates()).stream().filter(domainNode -> domainNode.getModel().compute(input, params)!=null)
                .collect(Collectors.toList());

        return childs.isEmpty()?null:childs.get(Double.valueOf(Math.random()*childs.size()).intValue());
    }
}
