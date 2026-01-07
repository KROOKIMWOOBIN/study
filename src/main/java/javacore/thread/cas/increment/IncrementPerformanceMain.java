package javacore.thread.cas.increment;

import static javacore.thread.util.MyLogger.log;

public class IncrementPerformanceMain {

    private static final long COUNT = 100_000_000;

    public static void main(String[] args) {
        test(new BasicInteger());
        test(new VolatileInteger());
        test(new SyncInteger());
        test(new MyAtomicInteger());
    }

    private static void test(InclementInteger inclementInteger) {
        long startMs = System.currentTimeMillis();
        for (long i = 0; i < COUNT; i++) {
            inclementInteger.inclement();
        }
        long endMs = System.currentTimeMillis();
        log(inclementInteger.getClass().getSimpleName() + ": " + (endMs - startMs) + "ms");
    }

}
