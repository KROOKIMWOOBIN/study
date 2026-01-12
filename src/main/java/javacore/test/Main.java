package javacore.test;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new ProxyBank(new DefaultBank());
        Runnable job = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                bank.deposit(1000);
                bank.withdraw(1500);
            }
        };
        Thread thread1 = new Thread(job, "Thread1");
        Thread thread2 = new Thread(job, "Thread2");
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        bank.printMoney();
    }

    private interface Bank {
        void deposit(int money);
        void withdraw(int money);
        void printMoney();
    }

    private static class DefaultBank implements Bank {

        private int money;

        @Override
        public void deposit(int money) {
            System.out.println("[입금] " + money + "원");
            this.money += money;
            System.out.println("[현재잔액] " + this.money + "원");
        }

        @Override
        public void withdraw(int money) {
            if (this.money - money < 0) {
                System.out.println("[출금실패] 잔액: " + this.money + "원, 출금액: " + money + "원 [" + (money - this.money) + "원 부족]");
                return;
            }
            System.out.println("[출금] " + money + "원");
            this.money -= money;
            System.out.println("[현재잔액] " + this.money);
        }

        @Override
        public void printMoney() {
            System.out.println("[잔액] " + money + "원");
        }

    }

    private static class ProxyBank implements Bank {

        private final Bank bank;

        ProxyBank(Bank bank) {
            this.bank = bank;
        }

        @Override
        public synchronized void deposit(int money) {
            bank.deposit(money);
        }

        @Override
        public synchronized void withdraw(int money) {
            bank.withdraw(money);
        }

        @Override
        public synchronized void printMoney() {
            bank.printMoney();
        }

    }

}