package javacore.mid.mid1.class7;

import javacore.mid.mid1.class6.Printer;

public class AnonymousOuter {
    private int outInstanceVar = 3;
    public void process(int paramVar) {
        int localVal = 1;

        Printer printer = new Printer() {
            int value = 0;
            @Override
            public void printData() {
                System.out.println(value);
                System.out.println(localVal);
                System.out.println(paramVar);
                System.out.println(outInstanceVar);
            }
        };
        printer.printData();
    }

    public static void main(String[] args) {
        AnonymousOuter main = new AnonymousOuter();
        main.process(2);
    }
}
