package javacore.thread.cas.increment;

public class BasicInteger implements InclementInteger {

    private int value;

    @Override
    public void inclement() {
        value++;
    }

    @Override
    public int get() {
        return value;
    }

}
