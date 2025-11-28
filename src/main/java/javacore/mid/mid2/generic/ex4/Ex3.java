package javacore.mid.mid2.generic.ex4;

import javacore.mid.mid2.generic.animal.Animal;
import javacore.mid.mid2.generic.animal.Cat;
import javacore.mid.mid2.generic.animal.Dog;

public class Ex3 {
    public static void main(String[] args) {
        Dog dog = new Dog("멍멍이", 100);
        Cat cat = new Cat("냐옹이", 50);
        ComplexBox<Dog> hospital = new ComplexBox<>();
        hospital.set(dog);
        Cat returnCat = hospital.printAndReturn(cat);
        System.out.println("returnCat = " + returnCat);
    }
}
class ComplexBox<T extends Animal> {
    private T animal;
    public void set(T animal) {
        this.animal = animal;
    }
    public <Z> Z printAndReturn(Z z) {
        System.out.println("animal.className: " + animal.getClass().getName());
        System.out.println("z.className: " + z.getClass());
        return z;
    }
}