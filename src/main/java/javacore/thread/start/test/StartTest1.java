package javacore.thread.start.test;

import static javacore.util.MyLogger.log;

public class StartTest1 {
    public static void main(String[] args) {
        CounterThread counterThread = new CounterThread();
        counterThread.start();
    }

    private static class CounterThread extends Thread {
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
