package javacore.intermediate.class5;

public class NestedOuterMain {
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
