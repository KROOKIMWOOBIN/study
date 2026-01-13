package javacore.thread.executor.future;

import java.util.Random;
import java.util.concurrent.*;

import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class CallableMainV1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        Integer result = es.submit(new MyCallable()).get();
        log("result value = " + result);
        es.shutdown();
    }

    private static class MyCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            log("Callable Start");
            sleep(2_000);
            int value = new Random().nextInt(10);
            log("create value = " + value);
            log("Callable End");
            return value;
        }
    }

}
