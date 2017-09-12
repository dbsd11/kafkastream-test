package model.node.mongo;

import com.mongodb.async.SingleResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Created by BSONG on 2017/9/11.
 */
public class SingleResultCallbackHandler implements SingleResultCallback {
    private static final Logger LOG = LoggerFactory.getLogger(SingleResultCallbackHandler.class);

    private Object[] result;
    private CompletableFuture future;

    @Override
    public void onResult(Object o, Throwable throwable) {
        try {
            if (throwable != null) {
                LOG.error(throwable.getMessage(), throwable);
            }
            future.complete(o);
            result[0] = future.get();
        } catch (Exception e) {
            LOG.error("error in mongo single value callback handler", o);
        }
    }


    public static SingleResultCallbackHandler getInstance(Object[] result) {
        if (result == null || result.length == 0) {
            throw new RuntimeException("single value callback handler need result array to get result");
        }
        SingleResultCallbackHandler handler = new SingleResultCallbackHandler();
        handler.result = result;
        handler.future = new CompletableFuture();
        return handler;
    }
}
