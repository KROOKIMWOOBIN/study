package javacore.lambda;

import java.util.function.BinaryOperator;

public class MethodMain {

    public static void main(String[] args) {
        BinaryOperator<Integer> add = MethodMain::sum;
        System.out.println(add.apply(5, 10));
    }

    private static int sum(int a, int b) {
        return a + b;
    }

    static class Person {

        private String name;

        public Person() {
            this.name("Unknown");
        }

        public void Person(String name) {
            
        }
    }

}
