package javacore.thread.cas.increment;

import java.util.concurrent.atomic.AtomicInteger;

public class MyAtomicInteger implements InclementInteger {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void inclement() {
        atomicInteger.incrementAndGet();
    }

    @Override
    public int get() {
        return atomicInteger.get();
    }

}
