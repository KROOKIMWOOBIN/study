package javacore.thread.start;

import static javacore.util.MyLogger.log;

public class InnerRunnableMainV1 {
    public static void main(String[] args) {
        log("main() start");
        MyRunnable runnable = new MyRunnable();
        Thread thread = new Thread(runnable);
        thread.start();
        log("main() end");
    }
    static public class MyRunnable implements Runnable {
        @Override
        public void run() {
            log("run()");
        }
    }
}
