package javacore.lambda.methodref;

import java.util.function.Supplier;

public class MethodRefEx1 {

    public static void main(String[] args) {
        // 1. 정적 메서드 참조 클래스::정적메서드
        Supplier<String> staticMethod = Person::greeting;
        System.out.println("staticMethod = " + staticMethod.get());

        // 2. 특정 객체의 인스턴스 참조 객체::인스턴스메서드
        Person person = new Person("Kim");
        Supplier<String> instanceMethod = person::introduce;
        System.out.println("instanceMethod = " + instanceMethod.get());

        // 3. 생성자 참조
        Supplier<Person> newPerson = Person::new;
        System.out.println("newPerson = " + newPerson.get());
    }

}
