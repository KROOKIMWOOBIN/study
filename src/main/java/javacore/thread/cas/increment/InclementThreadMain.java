package javacore.thread.cas.increment;

import java.util.ArrayList;
import java.util.List;

import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class InclementThreadMain {

    private static final int THREAD_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        test(new BasicInteger());
        test(new VolatileInteger());
        test(new SyncInteger());
        test(new MyAtomicInteger());
    }

    private static void test(InclementInteger inclementInteger) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sleep(10);
                inclementInteger.inclement();
            }
        };
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread thread = new Thread(runnable);
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        int result = inclementInteger.get();
        log(inclementInteger.getClass().getSimpleName() + " result: " + result);
    }

}
