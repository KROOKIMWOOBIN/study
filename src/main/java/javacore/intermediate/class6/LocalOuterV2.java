package javacore.intermediate.class6;

public class LocalOuterV2 {
    private int outInstanceVar = 3;
    public void process(int paramVar) {
        int localVal = 1;
        class LocalPrinter implements Printer {
            int value = 0;
            @Override
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
        LocalOuterV2 localOuter = new LocalOuterV2();
        localOuter.process(2);
    }
}
