package javacore.thread.start.test;

import static javacore.thread.util.MyLogger.log;

public class StartTest4 {
    public static void main(String[] args) {
        Thread threadA = new Thread(new PrintWork("A", 1_000), "Thread-A");
        Thread threadB = new Thread(new PrintWork("B", 500), "Thread-B");
        threadA.start();
        threadB.start();
    }
    private static class PrintWork implements Runnable {
        String content;
        int sleepMs;
        public PrintWork(String content, int sleepMs) {
            this.content = content;
            this.sleepMs = sleepMs;
        }
        @Override
        public void run() {
            while (true) {
                log(content);
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
