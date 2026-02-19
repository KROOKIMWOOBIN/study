package javacore.thread.control.interrupt;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class ThreadStopMainV1 {
    public static void main(String[] args) {
        MyTask task = new MyTask();
        Thread thread = new Thread(task, "work");
        thread.start();

        sleep(4_000);
        log("작업 중단 지시");
        task.runFlag = false;
    }
    static class MyTask implements Runnable {

        volatile boolean runFlag = true;

        @Override
        public void run() {
            while (runFlag) {
                log("작업 중");
                sleep(3_000);
            }
            log("자원 정리");
            log("자원 종료");
        }
    }
}
