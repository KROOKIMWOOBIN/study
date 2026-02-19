package javacore.thread.control.join;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class JoinMainV4 {
    public static void main(String[] args) throws InterruptedException {
        log("start");
        SunTask task1 = new SunTask(1, 50);
        Thread thread1 = new Thread(task1);

        thread1.start();

        log("join(1_000) - main 스레드가 thread1 종료까지 1초 대기");
        thread1.join(1_000);
        log("main 스레드 대기 완료");

        log("task1.result: " + task1.result);
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
