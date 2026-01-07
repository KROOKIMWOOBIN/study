package javacore.test;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        for (long i = 0; i < 100; i++) {
            Thread thread = new Thread(new MyJob());
            thread.start();
        }
        Thread.sleep(1_000);
        System.out.println(atomicInteger.get());
    }

    private static class MyJob implements Runnable {
        @Override
        public void run() {
            atomicInteger.incrementAndGet();
        }
    }

}