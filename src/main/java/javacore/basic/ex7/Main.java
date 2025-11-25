package javacore.basic.ex7;

public class Main {
    public static void main(String[] args) {
        AbstractAnimal[] animals = {new Dog(), new Dock()};
        animalSound(animals);

        InterfaceAnimal[] animals1 = {new Dog2()};
        interfaceAnimalSound(animals1);
    }
    private static void interfaceAnimalSound(InterfaceAnimal[] animals) {
        for(InterfaceAnimal animal : animals) {
            animal.sound();
        }
    }
    private static void animalSound(AbstractAnimal[] animals) {
        for(AbstractAnimal animal : animals) {
            animal.move();
            animal.sound();
        }
    }
}

interface InterfaceAnimal {
    void sound();
}
class Dog2 implements InterfaceAnimal {
    @Override
    public void sound() {
        System.out.println("멍멍");
    }
}

abstract class AbstractAnimal {
    public abstract void sound();
    public void move() {
        System.out.println("동물이 움직입니다.");
    }
}
class Dog extends AbstractAnimal {
    @Override
    public void sound() {
        System.out.println("멍멍");
    }
}
class Dock extends AbstractAnimal {
    @Override
    public void sound() {
        System.out.println("꽉꽉");
    }
}