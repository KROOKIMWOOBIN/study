package javacore.thread.collection.simple;

import javacore.thread.collection.simple.list.BasicList;
import javacore.thread.collection.simple.list.SimpleList;
import javacore.thread.collection.simple.list.SyncProxyList;

import static javacore.util.MyLogger.log;

public class ListMain {

    public static void main(String[] args) throws InterruptedException {
        // test(new BasicList());
        // test(new SyncList());
        test(new SyncProxyList(new BasicList()));
    }

    private static void test(SimpleList list) throws InterruptedException {
        log(list.getClass().getSimpleName());
        Runnable addA = new Runnable() {
            @Override
            public void run() {
                list.add("A");
                log("Thread-1: list.add(\"A\")");
            }
        };
        Runnable addB = new Runnable() {
            @Override
            public void run() {
                list.add("B");
                log("Thread-2: list.add(\"B\")");
            }
        };
        Thread thread1 = new Thread(addA, "Thread-1");
        Thread thread2 = new Thread(addB, "Thread-2");
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("list = " + list);
    }

}
