package javacore.thread.control.join;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class JoinMainV2 {
    public static void main(String[] args) {
        log("start");
        SunTask task1 = new SunTask(1, 50);
        SunTask task2 = new SunTask(51, 100);
        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        thread1.start();
        thread2.start();

        // 정확한 타이밍을 맞추어 기다리기 어려움
        log("main thread sleep start");
        sleep(3_000);
        log("main thread sleep end");

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
