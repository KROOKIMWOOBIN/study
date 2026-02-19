package javacore.thread.sync;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class BankMain {

    public static void main(String[] args) throws InterruptedException {
        // BankAccount account = new BankAccountV1(1_000);
        // BankAccount account = new BankAccountV2(1_000);
        // BankAccount account = new BankAccountV3(1_000);
        // BankAccount account = new BankAccountV4(1_000);
        // BankAccount account = new BankAccountV5(1_000);
        BankAccount account = new BankAccountV6(1_000);
        Thread t1 = new Thread(new WithdrawTask(account, 800), "t1");
        Thread t2 = new Thread(new WithdrawTask(account, 800), "t2");
        t1.start();
        t2.start();

        sleep(500);
        log("t1 state: " + t1.getState());
        log("t2 state: " + t2.getState());

        t1.join();
        t2.join();
        log("최종 잔액: " + account.getBalance());
    }

}
