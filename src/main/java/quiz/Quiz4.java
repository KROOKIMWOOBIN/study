package quiz;

// 인터페이스 레벨2
public class Quiz4 {
    public static void main(String[] args) {
        Animal[] animals = {new Cat(), new Caw()};
        animalSound(animals); // 출력 결과
    }
    private static void animalSound(Animal[] animals) {
        for(Animal animal : animals) {
            System.out.println(Animal.name);
            animal.sound();
        }
    }
}
interface Animal {
    String name = "동물";
    void sound();
}
class Cat implements Animal {
    String name = "고앙야";
    @Override
    public void sound() {
        System.out.println("야옹");
    }
}
class Caw implements Animal {
    String name = "개";
    @Override
    public void sound() {
        System.out.println("음메");
    }
}
