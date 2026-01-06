package javacore.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static javacore.thread.util.ThreadUtils.sleep;

public class Main {

    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        lock.wait();
    }

}