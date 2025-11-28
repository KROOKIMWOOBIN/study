package javacore.etc;

public class Main {
    public static void main(String[] args) {
        Animal dog = new Dog();
        dog.sound();
    }
}
interface Car {
    void sound();
}
interface Animal {
    void sound();
}
class Dog implements Car, Animal {
    @Override
    public void sound() {
        System.out.println("멍멍");
    }
}