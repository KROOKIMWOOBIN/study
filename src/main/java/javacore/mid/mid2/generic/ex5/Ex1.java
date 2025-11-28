package javacore.mid.mid2.generic.ex5;

import javacore.mid.mid2.generic.animal.Animal;
import javacore.mid.mid2.generic.animal.Cat;
import javacore.mid.mid2.generic.animal.Dog;

public class Ex1 {
    public static void main(String[] args) {
        Box<Object> obj = new Box<>();
        Box<Dog> dogBox = new Box<>();
        Box<Cat> catBox = new Box<>();

        dogBox.setValue(new Dog("멍멍이", 100));

        WildcardEx.printGenericV1(dogBox);
        WildcardEx.printWildcardV1(dogBox);

        WildcardEx.printGenericV2(dogBox);
        WildcardEx.printWildcardV2(dogBox);

        Dog returnDog = WildcardEx.printAndReturnGeneric(dogBox);
        Animal animal = WildcardEx.printAndReturnWildcard(dogBox);
    }
}
