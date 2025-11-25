package javacore.mid.mid1.class6;

import java.lang.reflect.Field;

public class LocalOuterV4 {
    private int outInstanceVar = 3;
    public Printer process(int paramVar) {
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

            public void printData2() {
                System.out.println(value);
                System.out.println(localVal);
                System.out.println(paramVar);
                System.out.println(outInstanceVar);
            }
        }

        LocalPrinter printer = new LocalPrinter();
        // printer.printData();
        return printer;
    }

    public static void main(String[] args) {
        LocalOuterV4 localOuter = new LocalOuterV4();
        Printer printer = localOuter.process(2);
        printer.printData();
        Field[] fields = printer.getClass().getDeclaredFields();

        for(Field field : fields) {
            System.out.println("Field : " + field);
        }
    }
}
