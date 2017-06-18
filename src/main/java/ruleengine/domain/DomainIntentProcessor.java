package ruleengine.domain;

import dto.FlowDto;

/**
 * Created by BSONG on 2017/6/19.
 */
public interface DomainIntentProcessor {

    TreeNode recognize(TreeNode root, FlowDto flowDto);

    default IntentNode treeRecognize(TreeNode root, FlowDto flowDto) {
        return null;
    }
}
