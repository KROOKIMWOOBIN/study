package javacore.thread.control;

import static javacore.thread.util.MyLogger.log;

public class ThreadInfoMain {
    public static void main(String[] args) {
        Thread mainThread = Thread.currentThread();
        log("mainThread: " + mainThread);
        // log("mainThread.threadId: " + mainThread.threadId()); // 1 출력
        log("mainThread.getName: " + mainThread.getName());
        log("mainThread.get: " + mainThread.getPriority());
        log("mainThread.getThreadGroup: " + mainThread.getThreadGroup());
        log("mainThread.getState: " + mainThread.getState());
    }
}
