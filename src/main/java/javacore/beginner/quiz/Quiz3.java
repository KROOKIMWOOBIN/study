package javacore.beginner.quiz;

// 추상화 활용 레벨3
public class Quiz3 {
    public static void main(String[] args) {
        Dog dog = new Dog();
        animalSound(dog);
    }
    private static void animalSound(AbstractAnimal animal) {
        System.out.println(animal.name);
        animal.sound();
    }
}
abstract class AbstractAnimal {
    String name = "동물";
    abstract void sound();
}
class Dog extends AbstractAnimal{
    String name = "개";
    @Override
    void sound() {
        System.out.println("멍멍");
    }
}
