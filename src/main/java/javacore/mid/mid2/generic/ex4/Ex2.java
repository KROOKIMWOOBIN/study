package javacore.mid.mid2.generic.ex4;

import javacore.mid.mid2.generic.animal.Animal;
import javacore.mid.mid2.generic.animal.Cat;
import javacore.mid.mid2.generic.animal.Dog;

public class Ex2 {
    public static void main(String[] args) {
        Dog dog = new Dog("멍멍이", 5);
        Cat cat = new Cat("야옹", 3);
        AnimalMethod.checkup(dog);
        AnimalMethod.checkup(cat);

        Dog targetDog = new Dog("큰 멍멍이", 200);
        Dog bigger = AnimalMethod.bigger(dog, targetDog);
        System.out.println("bigger: " + bigger);
    }
}
class AnimalMethod {
    public static <T extends Animal> void checkup(T t) {
        System.out.println("동물 이름: " + t.getName());
        System.out.println("동물 크기: " + t.getSize());
        t.sound();
    }
    public static <T extends Animal> T bigger(T t1, T t2) {
        return t1.getSize() > t2.getSize() ? t1 : t2;
    }
}
