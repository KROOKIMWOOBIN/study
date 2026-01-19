package javacore.test;

public class Main {

    public static void main(String[] args) {
        Money money = new Money(1000);
        System.out.println(money.add(500).subtract(200).getAmount());
    }

}

final class Money {

    private final int amount;

    public Money(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Money add(int value) {
        return new Money(this.amount + value);
    }

    public Money subtract(int value) {
        return new Money(this.amount - value);
    }

}