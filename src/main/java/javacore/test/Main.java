package javacore.test;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new MyRejectedExecutionHandler());
        executor.execute(new MyJob());
        executor.execute(new MyJob());
        System.out.println("종료");
        executor.shutdown();
    }

    private static class MyRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println(r.getClass().getSimpleName() + " 거절함");
        }
    }

    private static class MyJob implements Runnable {
        @Override
        public void run() {
            System.out.println("실행");
        }
    }

}