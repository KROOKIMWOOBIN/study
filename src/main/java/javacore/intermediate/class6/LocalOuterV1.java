package javacore.intermediate.class6;

public class LocalOuterV1 {
    private int outInstanceVar = 3;
    public void process(int paramVar) {
        int localVal = 1;
        class LocalPrinter {
            int value = 0;
            public void printData() {
                System.out.println(value);
                System.out.println(localVal);
                System.out.println(paramVar);
                System.out.println(outInstanceVar);
            }
        }

        LocalPrinter printer = new LocalPrinter();
        printer.printData();
    }

    public static void main(String[] args) {
        LocalOuterV1 localOuter = new LocalOuterV1();
        localOuter.process(2);
    }
}
