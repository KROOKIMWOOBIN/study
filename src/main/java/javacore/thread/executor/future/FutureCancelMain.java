package javacore.thread.executor.future;

import java.util.concurrent.*;

import static javacore.util.MyLogger.log;

public class FutureCancelMain {

    // private static boolean mayInterruptRunning = true;
    private static boolean mayInterruptRunning = false;

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(1);
        Future<String> future = es.submit(new MyTask());
        // log("Future.state: " + future.state());

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log("future.cancel(" + mayInterruptRunning + ")");
        boolean cancelResult = future.cancel(mayInterruptRunning);
        log("cancel(" + mayInterruptRunning + ") result: " + cancelResult);

        try {
            log("future.result: " + future.get());
        } catch (CancellationException e) {
            log("'Future'는 이미 취소 되었습니다.");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        es.shutdown();

    }

    static class MyTask implements Callable<String> {

        @Override
        public String call() {
            try {
                for (int i = 0; i < 10; i++) {
                    log("작업 중: " + i);
                    Thread.sleep(1_000);
                }
            } catch (InterruptedException e) {
                log("인터럽트 발생");
                return "Interrupted";
            }
            return "Completed";
        }

    }

}
