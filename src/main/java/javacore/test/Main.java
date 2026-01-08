package javacore.test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {
        SpinLock lock = new SpinLock();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    log("비즈니스 로직 실행");
                } finally {
                    lock.unlock();
                }
            }
        };
        for (int i = 1; i <= 2; i++) {
            Thread thread = new Thread(task, "Thread" + i);
            thread.start();
        }
    }

    private static void log(Object obj) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String time = LocalTime.now().format(formatter);
        System.out.printf("%s [%9s] %s\n", time, Thread.currentThread().getName(), obj);
    }

    private static class SpinLock {

        AtomicBoolean lock = new AtomicBoolean(); // default = false

        public void lock() {
            while (!lock.compareAndSet(false, true)) {
                log("락 획득 실패");
            }
            log("락 획득 성공");
        }

        public void unlock() {
            lock.set(false); // 락 해제
            log("락 해제 성공");
        }

    }

}