package javacore.thread.control;

import javacore.thread.start.HelloRunnable;

import static javacore.util.MyLogger.log;

public class ThreadInfoMain {
    public static void main(String[] args) {
        Thread mainThread = Thread.currentThread();
        log("mainThread: " + mainThread);
        // log("mainThread.threadId: " + mainThread.threadId()); // 1 출력
        log("mainThread.getName: " + mainThread.getName());
        log("mainThread.get: " + mainThread.getPriority());
        log("mainThread.getThreadGroup: " + mainThread.getThreadGroup());
        log("mainThread.getState: " + mainThread.getState());

        Thread myThread = new Thread(new HelloRunnable(), "myThread");
        log("myThread: " + myThread);
        // log("myThread.threadId: " + myThread.threadId()); // 21 출력
        log("myThread.getName: " + myThread.getName());
        log("myThread.get: " + myThread.getPriority());
        log("myThread.getThreadGroup: " + myThread.getThreadGroup());
        log("myThread.getState: " + myThread.getState());
    }
}
