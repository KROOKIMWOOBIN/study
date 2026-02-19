package javacore.thread.cas;

import java.util.concurrent.atomic.AtomicInteger;

import static javacore.util.MyLogger.log;

public class CasMainV2 {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger();
        log("start value: " + atomicInteger.get());

        int result1 = atomicInteger.incrementAndGet();
        log("result1: " + result1);

        int result2 = atomicInteger.incrementAndGet();
        log("result2: " + result2);

        int resultValue1 = incrementAndGet(atomicInteger);
        log("resultValue1: " + resultValue1);
        int resultValue2 = incrementAndGet(atomicInteger);
        log("resultValue2: " + resultValue2);

    }

    private static int incrementAndGet(AtomicInteger atomicInteger) {
        int getValue;
        boolean result;
        do {
            getValue = atomicInteger.get();
            log("getValue: " + getValue);
            result = atomicInteger.compareAndSet(getValue, getValue + 1);
            log("result: " + result);
        } while(!result);
        return getValue + 1;
    }

}
