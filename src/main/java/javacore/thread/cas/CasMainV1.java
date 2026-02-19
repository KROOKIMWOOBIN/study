package javacore.thread.cas;

import java.util.concurrent.atomic.AtomicInteger;

import static javacore.util.MyLogger.log;

public class CasMainV1 {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger();
        log("start value: " + atomicInteger.get());

        boolean result1 = atomicInteger.compareAndSet(0, 1);
        log("result1: " + result1 + ", value: " + atomicInteger.get());

        boolean result2 = atomicInteger.compareAndSet(0, 1);
        log("result2: " + result2 + ", value: " + atomicInteger.get());
    }

}
