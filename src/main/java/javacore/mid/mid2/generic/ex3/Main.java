package javacore.mid.mid2.generic.ex3;

import javacore.mid.mid2.generic.animal.Animal;
import javacore.mid.mid2.generic.animal.Cat;
import javacore.mid.mid2.generic.animal.Dog;

public class Main {
    public static void main(String[] args) {
        AnimalHospital<Dog> dogHospital = new AnimalHospital<>();
        AnimalHospital<Cat> catHospital = new AnimalHospital<>();

        Dog dog = new Dog("강아지", 5);
        Cat cat = new Cat("냐옹이", 2);

        dogHospital.set(dog);
        dogHospital.checkup();

        catHospital.set(cat);
        catHospital.checkup();

        Dog biggerDog = dogHospital.bigger(new Dog("큰강아지", 10));
        System.out.println("biggerDog: " + biggerDog);
    }
}

class AnimalHospital<T extends Animal> {
    private T animal;
    public void set(T animal) {
        this.animal = animal;
    }
    public void checkup() {
        System.out.println("동물 이름: " + animal.getName());
        System.out.println("동물 크기: " + animal.getSize());
        animal.sound();
    }
    public T bigger(T target) {
        return animal.getSize() > target.getSize() ? animal : target;
    }
}
