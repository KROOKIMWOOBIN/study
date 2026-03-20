package javacore.lambda.methodref;

import java.util.function.Function;
import java.util.function.Supplier;

public class MethodRefEx {

    public static void main(String[] args) {
        // 1. 정적 메서드 참조 클래스::정적메서드
        Supplier<String> staticMethod1 = Person::greeting;
        System.out.println("staticMethod1 = " + staticMethod1.get());

        Function<String, String> staticMethod2 = Person::greetingWithName;
        System.out.println("staticMethod2 = " + staticMethod2.apply("Kim"));

        // 2. 특정 객체의 인스턴스 참조 객체::인스턴스메서드
        Person person = new Person("Kim");
        Supplier<String> instanceMethod1 = person::introduce;
        System.out.println("instanceMethod1 = " + instanceMethod1.get());

        Function<Integer, String> instanceMethod2 = person::introduceWithNumber;
        System.out.println("instanceMethod2 = " + instanceMethod2.apply(30));

        // 3. 생성자 참조
        Supplier<Person> newPerson1 = Person::new;
        System.out.println("newPerson = " + newPerson1.get());

        Function<String, Person> newPerson2 = Person::new;
        System.out.println("newPerson2 = " + newPerson2.apply("Kim"));

        // 4. 임의 객체의 인스턴스 메서드 참(특정 타입의)
        Person person1 = new Person("A");
        Person person2 = new Person("B");
        Person person3 = new Person("C");
        Function<Person, String> 

    }

}
