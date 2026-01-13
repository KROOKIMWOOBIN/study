package javacore.thread.executor.future;

import java.util.Random;

import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class RunnableMain {

    public static void main(String[] args) throws InterruptedException {
        MyRunnable task = new MyRunnable();
        Thread thread = new Thread(task, "Thread1");
        thread.start();
        thread.join();
        int result = task.value;
        log("result value = " + result);
    }

    private static class MyRunnable implements Runnable {
        int value;
        @Override
        public void run() {
            log("Runnable Start");
            sleep(2_000);
            value = new Random().nextInt(10);
            log("create value = " + value);
            log("Runnable End");
        }
    }

}
