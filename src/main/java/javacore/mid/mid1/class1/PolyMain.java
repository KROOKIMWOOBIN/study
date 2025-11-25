package javacore.mid.mid1.class1;

public class PolyMain {
    public static void main(String[] args) {
        Object[] objects = {new Car(), new Dog()};
        action(objects);
    }
    private static void action(Object[] objects) {
        for(Object object : objects) {
            // 다운 캐스팅을 해야지 자식에 메서드에 접근 가능
            if(object instanceof Car) {
                ((Car) object).move();
            }
            else if(object instanceof Dog) {
                ((Dog) object).sound();
            }
            else {
                System.out.println("존재하지 않는 클래스 입니다.");
            }
        }
    }
}
class Car {
    public void move() {
        System.out.println("자동차가 이동합니다.");
    }
}
class Dog {
    public void sound() {
        System.out.println("멍멍");
    }
}
