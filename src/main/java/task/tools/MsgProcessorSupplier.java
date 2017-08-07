package task.tools;

import data.ActionData;
import data.DialogData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Created by BSONG on 2017/8/7.
 */
@Slf4j
public class MsgProcessorSupplier implements ProcessorSupplier {

    private boolean formless;

    private static long windowSize = 30 * 24 * 3600;

    public MsgProcessorSupplier() {
        this(false);
    }

    public MsgProcessorSupplier(boolean formless) {
        this.formless = formless;
    }

    @Override
    public Processor get() {
        return new DialogMsgProcessor(this);
    }

    public static long getWindowSize() {

        return windowSize;
    }

    public static class DialogMsgProcessor extends AbstractProcessor<Long, DialogData> {

        private WindowStore<String, List<ActionData>> dialogHistoryStore;
        private KeyValueStore<String, Double> dialogStatisticStore;
        private ProcessorContext context;
        private MsgProcessorSupplier supplier;

        public DialogMsgProcessor(MsgProcessorSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public void process(Long aLong, DialogData dialogData) {
            if (supplier.formless) {
                return;
            }
            if (dialogData == null) {
                throw new RuntimeException("can't process null dialogData");
            }

            log.info("process dialogData:{}", dialogData);

            long start = System.currentTimeMillis();
            String key = getKey(dialogData);
            ActionData actionData = new ActionData();
            try {
                //todo process message
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("save dialog history:{}", actionData);
                dialogHistoryStore.put(key, Collections.singletonList(actionData));

                log.info("save dialog statistic");
                String dialogCountKey = getDialogCountKey(dialogData);
                dialogStatisticStore.putIfAbsent(dialogCountKey, 0.0);
                Double dialogCountValue = dialogStatisticStore.get(dialogCountKey);
                if (dialogCountValue == null) {
                    dialogCountValue = 0.0;
                }
                dialogCountValue += 1;
                dialogStatisticStore.put(dialogCountKey, dialogCountValue);
            }
        }

        @Override
        public void init(ProcessorContext context) {
            super.init(context);
            this.context = context;
            this.dialogHistoryStore = (WindowStore<String, List<ActionData>>) this.context.getStateStore("dialogHistory");
            this.dialogStatisticStore = (KeyValueStore<String, Double>) this.context.getStateStore("dialogStatistic");
        }

        @Override
        public void punctuate(long timestamp) {
            super.punctuate(timestamp);
            log.info("call dialog msg processor punctuate method");
        }

        String getKey(DialogData dialogData) {

            return String.valueOf(Base64.getEncoder().encode(String.join("_", String.valueOf(dialogData.getTenantId()), dialogData.getRobotId()).getBytes()));
        }

        String getDialogCountKey(DialogData dialogData) {

            return String.valueOf(Base64.getEncoder().encode(String.join("_", String.valueOf(dialogData.getTenantId()), dialogData.getRobotId(), "dialogcount").getBytes()));
        }
    }
}

