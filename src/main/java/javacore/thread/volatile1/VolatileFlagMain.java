package javacore.thread.volatile1;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class VolatileFlagMain {

    public static void main(String[] args) {
        MyTask task = new MyTask();
        Thread t = new Thread(task, "work");
        log("runFlag: " + task.runFlag);
        t.start();
        sleep(1_000);
        log("runFlag => false");
        task.runFlag = false;
        log("runFlag: " + task.runFlag);
        log("main stop");
    }

    static class MyTask implements Runnable {

        // boolean runFlag = true;
        volatile boolean runFlag = true;

        @Override
        public void run() {
            log("task Start");
            while (runFlag) {
                // runFlag가 false로 변하면 탈출
            }
            log("task End");
        }

    }

}