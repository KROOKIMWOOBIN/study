package javacore.thread.start.test;

import static javacore.thread.util.MyLogger.log;

public class StartTest2 {
    public static void main(String[] args) {
        Thread thread = new Thread(new CounterThread(), "counter");
        thread.start();
    }

    private static class CounterThread implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                log("value: " + i);
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
