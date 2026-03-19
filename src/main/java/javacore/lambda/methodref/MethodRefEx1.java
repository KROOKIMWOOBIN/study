package javacore.lambda.methodref;

import java.util.function.Function;
import java.util.function.Supplier;

public class MethodRefEx1 {

    public static void main(String[] args) {
        // 1. 정적 메서드 참조 클래스::정적메서드
        Supplier<String> staticMethod1 = Person::greeting;
        System.out.println("staticMethod1 = " + staticMethod1.get());

        Function<String, String> staticMethod2 = Person::greetingWithName;
        System.out.println("staticMethod2 = " + staticMethod2.apply("Kim"));

        // 2. 특정 객체의 인스턴스 참조 객체::인스턴스메서드
        Person person1 = new Person("Kim");
        Supplier<String> instanceMethod1 = person1::introduce;
        System.out.println("instanceMethod1 = " + instanceMethod1.get());

        Person

        // 3. 생성자 참조
        Supplier<Person> newPerson = Person::new;
        System.out.println("newPerson = " + newPerson.get());

    }

}
