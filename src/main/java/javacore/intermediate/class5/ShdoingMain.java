package javacore.intermediate.class5;

public class ShdoingMain {

    public int value = 1;

    class Inner {
        public int value = 2;
        void go() {
            int value = 3;
            System.out.println(value);
            System.out.println(this.value);
            System.out.println(ShdoingMain.this.value);
        }
    }

    public static void main(String[] args) {
        ShdoingMain shdoingMain = new ShdoingMain();
        ShdoingMain.Inner inner = shdoingMain.new Inner();
        inner.go();
    }
}
