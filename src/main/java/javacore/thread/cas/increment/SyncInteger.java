package javacore.thread.cas.increment;

public class SyncInteger implements InclementInteger {

    private int value;

    @Override
    public synchronized void inclement() {
        value++;
    }

    @Override
    public synchronized int get() {
        return value;
    }

}
