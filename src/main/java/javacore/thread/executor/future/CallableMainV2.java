package javacore.thread.executor.future;

import java.util.Random;
import java.util.concurrent.*;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class CallableMainV2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        log("submit() call");
        Future<Integer> future = es.submit(new MyCallable());
        log("future now return, future = " + future);
        log("future.get() [BLOCKING] Method call start -> Main Thread WAITING");
        Integer result = future.get();
        log("future.get() [BLOCKING] Method call end -> Main Thread RUNNABLE");
        log("result value = " + result);
        log("future end, future = " + future);
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
