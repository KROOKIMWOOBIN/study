package javacore.intermediate.class5;

public class Ex3 {
    public static void main(String[] args) {
        InnerOuter outer = new InnerOuter();
        InnerOuter.Inner inner = outer.new Inner();
        inner.print();
        System.out.println(inner.getClass());
    }
}
class InnerOuter {
    private static int outClassValue = 3;
    private int outInstanceValue = 2;

    class Inner {
        private int innerInstanceValue = 1;

        public void print() {
            System.out.println(innerInstanceValue);
            System.out.println(outInstanceValue);
            System.out.println(outClassValue);
        }
    }
}