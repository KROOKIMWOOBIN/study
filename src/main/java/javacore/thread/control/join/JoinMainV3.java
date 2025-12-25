package javacore.thread.control.join;

import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class JoinMainV3 {
    public static void main(String[] args) throws InterruptedException {
        log("start");
        SunTask task1 = new SunTask(1, 50);
        SunTask task2 = new SunTask(51, 100);
        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        thread1.start();
        thread2.start();

        log("join() - main 스레드가 thread1, thread2 종료까지 대기");
        thread1.join();
        thread2.join();
        log("main 스레드 대기 완료");

        log("task1.result: " + task1.result);
        log("task2.result: " + task2.result);
        int sumAll = task1.result + task2.result;
        log("task1 + task2 = " + sumAll);
        log("end");
    }
    static class SunTask implements Runnable {

        int startValue;
        int endValue;
        int result;

        public SunTask(int startValue, int endValue) {
            this.startValue = startValue;
            this.endValue = endValue;
        }

        @Override
        public void run() {
            log("작업 시작");
            sleep(2_000);
            int sum = 0;
            for (int i = startValue; i <= endValue; i++) {
                sum += i;
            }
            result += sum;
            log("작업 종료 result: " + result);
        }
    }
}
