package javacore.intermediate.class5;

import org.springframework.boot.context.properties.bind.Nested;

public class Ex1 {
    public static void main(String[] args) {
        NestedOuter.Nested nested = new NestedOuter.Nested();
        nested.print();

        System.out.println("nestedClass : " + nested.getClass());
    }
}
class NestedOuter {
    private static int outClassValue = 3;
    private int outInstanceValue = 2;
    static class Nested {
        private int nestedInstanceValue = 1;
        public void print() {
            System.out.println(nestedInstanceValue);
            System.out.println(outClassValue);
        }
    }
}
