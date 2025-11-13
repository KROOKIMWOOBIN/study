package javacore.beginner.ex7;

import java.util.Scanner;

public class PayMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("결제 방식을 입력해주세요 : ");
        String bank = sc.nextLine();
        System.out.println("금액을 입력해주세요 : ");
        int money = sc.nextInt();
        processPay(bank, money);
    }
    private static void processPay(String bank, int money) {
        System.out.println("결제를 시작합니다. 은행 : " + bank + ", 금액 : " + money);
        Pay pay = new DefaultPay();
        if(bank.equals("카카오")) {
            pay = new KakaoPay();
        }
        else if(bank.equals("네이버")) {
            pay = new NaverPay();
        }
        pay.pay(money);
    }
}
interface Pay {
    void pay(int balance);
}
class KakaoPay implements Pay {
    @Override
    public void pay(int balance) {
        System.out.println("카카오페이 시스템과 연결합니다.");
        System.out.println(balance + "원 결제를 시도합니다.");
        System.out.println("결제가 성공했습니다.");
    }
}
class NaverPay implements Pay {
    @Override
    public void pay(int balance) {
        System.out.println("네이버페이 시스템과 연결합니다.");
        System.out.println(balance + "원 결제를 시도합니다.");
        System.out.println("결제가 성공했습니다.");
    }
}
class DefaultPay implements Pay {
    @Override
    public void pay(int balance) {
        System.out.println("해당 은행은 존재하지 않습니다.");
    }
}