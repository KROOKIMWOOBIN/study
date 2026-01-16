package javacore.test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
        executor.execute(new MyJob());
        executor.execute(new MyJob()); // 여기서 작업을 더 이상 넣을 수 없으면, 버려버림
        System.out.println("종료");
        executor.shutdown();
    }

    private static class MyJob implements Runnable {
        @Override
        public void run() {
            System.out.println("실행");
        }
    }

}