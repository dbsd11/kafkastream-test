package dialog.tools;

import data.ActionData;
import data.DialogData;
import data.ResponseData;
import flow.DialogFlowProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import util.BeanDelegator;
import util.Constants;

import java.util.Base64;

/**
 * Created by BSONG on 2017/8/7.
 */
@Slf4j
public class MsgProcessorSupplier implements ProcessorSupplier {

    private boolean formless;

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

        return Constants.HISTORY_WINDOWSIZE;
    }

    public static class DialogMsgProcessor extends AbstractProcessor<Long, DialogData> {

        private DialogFlowProcessor dialogFlowProcessor;

        private WindowStore<String, ActionData> dialogHistoryStore;
        private KeyValueStore<String, Double> dialogStatisticStore;
        private MsgProcessorSupplier supplier;

        public DialogMsgProcessor(MsgProcessorSupplier supplier) {
            this.supplier = supplier;
            this.dialogFlowProcessor = new DialogFlowProcessor();
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
                actionData.setId(dialogStatisticStore.get(getDialogCountKey(dialogData)) == null ? 0 : dialogStatisticStore.get(getDialogCountKey(dialogData)).intValue());
                actionData.setTenantId(dialogData.getTenantId());
                actionData.setRobotId(dialogData.getRobotId());
                actionData.setContent(dialogData.getContent());
                actionData.setTimeStamp(context().timestamp());
                //todo worker
                ResponseData responseData = dialogFlowProcessor.process(actionData);
                callBack(responseData);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("save dialog history:{}", actionData);
                dialogHistoryStore.put(key, actionData);

                log.info("save dialog statistic");
                String dialogCountKey = getDialogCountKey(dialogData);
                dialogStatisticStore.putIfAbsent(dialogCountKey, 0.0);
                Double dialogCountValue = dialogStatisticStore.get(dialogCountKey);
                if (dialogCountValue == null) {
                    dialogCountValue = 0.0;
                }
                dialogCountValue += 1;
                dialogStatisticStore.put(dialogCountKey, dialogCountValue);

                dialogStatisticStore.put(getDialogLastTimeKey(dialogData), (double) actionData.getTimeStamp());
            }
        }

        @Override
        public void init(ProcessorContext context) {
            super.init(context);
            this.dialogHistoryStore = (WindowStore<String, ActionData>) context().getStateStore("dialogHistory");
            this.dialogStatisticStore = (KeyValueStore<String, Double>) context().getStateStore("dialogStatistic");
            BeanDelegator.delegate(WindowStore.class, dialogHistoryStore);
            BeanDelegator.delegate(KeyValueStore.class, dialogStatisticStore);
        }

        @Override
        public void punctuate(long timestamp) {
            super.punctuate(timestamp);
            log.info("call dialog msg processor punctuate method");
        }

        static void callBack(ResponseData responseData) {
            log.info("response data:" + responseData);
        }

        String getKey(DialogData dialogData) {

            return String.valueOf(Base64.getEncoder().encode(String.join("_", String.valueOf(dialogData.getTenantId()), dialogData.getRobotId()).getBytes()));
        }

        String getDialogCountKey(DialogData dialogData) {

            return String.valueOf(Base64.getEncoder().encode(String.join("_", String.valueOf(dialogData.getTenantId()), dialogData.getRobotId(), "dialogcount").getBytes()));
        }

        String getDialogLastTimeKey(DialogData dialogData) {

            return String.valueOf(Base64.getEncoder().encode(String.join("_", String.valueOf(dialogData.getTenantId()), dialogData.getRobotId(), "dialoglasttime").getBytes()));
        }
    }
}

