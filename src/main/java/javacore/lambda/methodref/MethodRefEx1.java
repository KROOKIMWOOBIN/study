package javacore.lambda.methodref;

import java.util.function.Supplier;

public class MethodRefEx1 {

    public static void main(String[] args) {
        // 1. 정적 메서드 참조
        Supplier<String> staticMethod = Person::greeting;
        System.out.println("staticMethod = " + staticMethod.get());
    }

}
