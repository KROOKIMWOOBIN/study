package javacore.thread.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static javacore.util.MyLogger.log;
import static javacore.util.ThreadUtils.sleep;

public class BankAccountV6 implements BankAccount {

    private int balance;

    private final Lock lock = new ReentrantLock();

    public BankAccountV6(int initialBalance) {
        this.balance = initialBalance;
    }

    @Override
    public boolean withdrew(int amount) {
        log("거래 시작: " + getClass().getSimpleName());

        try {
            if (!lock.tryLock(10, TimeUnit.SECONDS)) {
                log("[진입 실패] 이미 처리중인 작업이 있습니다.");
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 검증 로직
        log("[검증 시작] 출금액: " + amount + ", 잔액: " + balance);
        if (balance < amount) {
            log("[검증 실패] 출금액: " + amount + ", 잔액: " + balance);
            lock.unlock();
            return false;
        }
        log("[검증 완료] 출금액: " + amount + ", 잔액: " + balance);
        // 출금 시작
        sleep(1_000);
        balance = balance - amount;
        log("[출금 완료] 출금액: " + amount + ", 잔액: " + balance);
        log("거래 종료");
        lock.unlock();
        return true;
    }

    @Override
    public int getBalance() {
        lock.lock(); // ReentrantLock 이용하여 Lock 걸기
        try {
            return balance;
        } finally {
            lock.unlock(); // ReentrantLock 이용하여 Lock 해제
        }
    }
}
