package javacore.thread.sync;

public interface BankAccount {

    boolean withdrew(int amount); // 출금

    int getBalance(); // 잔고 확인

}
