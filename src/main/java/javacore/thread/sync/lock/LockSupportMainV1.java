package javacore.thread.sync.lock;

import java.util.concurrent.locks.LockSupport;

import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class LockSupportMainV1 {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new ParkTest(), "Thread-1");
        thread1.start();
        sleep(100);
        log("Thread-1 state: " + thread1.getState());

        LockSupport.unpark(thread1); // 1. unpark use
        // thread1.interrupt(); // 2. interrupt use
    }
    static class ParkTest implements Runnable {
        @Override
        public void run() {
            log("park start");
            LockSupport.park();
            log("part end, state: " + Thread.currentThread().getState());
            log("interrupt state: " + Thread.currentThread().isInterrupted());
        }
    }
}
