package javacore.thread.executor.future;

import java.util.concurrent.*;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class FutureExceptionMain {

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(1);
        log("작업 전달");
        Future<Integer> future = es.submit(new ExCallable());
        sleep(1_000);
        try {
            // log("future.get() 호출 시도, future.state(): " + future.state());
            Integer result = future.get();
            log("result value = " + result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            log("e = " + e);
            Throwable cause = e.getCause();
            log(cause);
        }
    }

    private static class ExCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            log("Callable 실행, 예외 발생");
            throw new IllegalStateException("ex!");
        }
    }

}
