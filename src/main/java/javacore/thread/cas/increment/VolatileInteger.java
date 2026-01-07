package javacore.thread.cas.increment;

public class VolatileInteger implements InclementInteger {

    private volatile int value;

    @Override
    public void inclement() {
        value++;
    }

    @Override
    public int get() {
        return value;
    }

}
