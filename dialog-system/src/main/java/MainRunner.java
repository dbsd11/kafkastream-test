import dialog.DialogListener;

import java.util.concurrent.ExecutionException;

/**
 * Created by BSONG on 2017/9/18.
 */
public class MainRunner {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Thread(new DialogListener()).start();
    }
}
