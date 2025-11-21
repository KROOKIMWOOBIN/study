package javacore.intermediate.class7;

import java.util.Random;

public class Ex3Main {
    public static void main(String[] args) {
        hello(new Process() {
            @Override
            public void run() {
                int randomValue = new Random().nextInt(6) + 1;
                System.out.println("주사위 : " + randomValue);
            }
        });
        hello(new Process() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println("i = " + i);
                }
            }
        });
    }
    public static void hello(Process process) {
        process.run();
    }
}