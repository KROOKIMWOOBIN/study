package javacore.basic.ex6.quiz1;

public class Main {
    public static void main(String[] args) {
        Animal[] animals = {new Dog(), new Cat(), new Caw()};
        animalSounds(animals);
    }

    // 변하지 않는 코드
    private static void animalSounds(Animal[] animals) {
        for (Animal animal : animals) {
            animal.sound();
        }
    }
}

class Animal {
    public void sound() {
        System.out.println("울음소리");
    }
}
class Dog extends Animal {
    @Override
    public void sound() {
        System.out.println("강아지::멍멍");
    }
}
class Cat extends Animal {
    @Override
    public void sound() {
        System.out.println("고양이::야옹");
    }
}
class Caw extends Animal {
    @Override
    public void sound() {
        System.out.println("소::음메");
    }
}
