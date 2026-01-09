package javacore.test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {
        Aniaml animal = new Dog();
        System.out.println(animal.name); // 필드 변수는 동적 바인딩이 안된다.
        animal.sound(); // 동적 바인딩으로 인해 멍멍이 출력된다.
    }

    private static class Aniaml {
        String name = "동물";
        void sound() {
            System.out.println("동물 소리");
        }
    }

    private static class Dog extends Aniaml {
        String name = "개";
        @Override
        void sound() {
            System.out.println("멍멍");
        }
    }

}