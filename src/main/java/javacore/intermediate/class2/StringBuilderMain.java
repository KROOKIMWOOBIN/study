package javacore.intermediate.class2;

public class StringBuilderMain {
    public static void main(String[] args) {
        String result = "";
        long start = System.currentTimeMillis();
        for(int i = 1; i <= 100000; i++) {
            result += "String";
        }
        long end = System.currentTimeMillis();
        System.out.println("String Result : " + (end - start) + "ms");

        StringBuilder sb = new StringBuilder();
        start = System.currentTimeMillis();
        for(int i = 1; i <= 100000; i++) {
            sb.append("String");
        }
        end = System.currentTimeMillis();
        result = sb.toString();
        System.out.println("StringBuilder Result : " + (end - start) + "ms");
    }
}
