package javacore.thread.control.printer;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class MyPrinterV1 {
    public static void main(String[] args) {
        Printer printer = new Printer();
        Thread printerThread = new Thread(printer, "printer");
        printerThread.start();

        Scanner userInput = new Scanner(System.in);
        while (true) {
            log("프린터할 문서를 입력하세여. 종료 (q): ");
            String input = userInput.nextLine();
            if ("q".equals(input)) {
                printer.work = false;
                break;
            }
            printer.addJob(input);
        }
    }
    static class Printer implements Runnable {

        volatile boolean work = true;
        Queue<String> jobQueue = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            while (work) {
                if (jobQueue.isEmpty()) {
                    continue;
                }
                String job = jobQueue.poll();
                log("출력 시작: " + job + ", 대기 문서: " + jobQueue);
                sleep(3_000);
                log("출력 완료");
            }
            log("프린터 종료");
        }
        public void addJob(String input) {
            jobQueue.offer(input);
        }
    }
}
