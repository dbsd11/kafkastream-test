package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by BSONG on 2017/6/18.
 */
@Builder
@Getter
@NoArgsConstructor
public class ActionDto extends RollbackAbleFlowDto {
    private static volatile AtomicLong id;

    private long timeStamp;
    private int serialIndex;
    private int tenantId;
    private String robotId;
    private String content;
    private Map<String, Object> attrs;

    public long getId() {

        return id.incrementAndGet();
    }

    @Override
    public void getProps(Map<String, Object> props) {

    }

    @Override
    public FlowDto next() {
        return null;
    }
}
