package javacore.mid.mid1.class7;

import java.util.Random;

public class Ex2Main {
    public static void main(String[] args) {
        class Dice implements Process {
            @Override
            public void run() {
                int randomValue = new Random().nextInt(6) + 1;
                System.out.println("주사위 : " + randomValue);
            }
        }
        class Sum implements Process {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println("i = " + i);
                }
            }
        }
        hello(new Dice());
        hello(new Sum());
    }
    public static void hello(Process process) {
        process.run();
    }
}