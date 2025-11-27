package javacore.mid.mid2.generic.ex4;

public class Main {
    public static void main(String[] args) {
        Integer i = 10;
        Object object = GenericMethod.objMethod(i);

        System.out.println("명시적 타입 인자 전달");
        Integer result = GenericMethod.genericMethod(i);
        Integer integerValue = GenericMethod.<Integer>numberMethod(10);
        Double doubleValue = GenericMethod.<Double>numberMethod(20.0);
    }
}

class GenericMethod {
    public static Object objMethod(Object obj) {
        System.out.println("Object print: " + obj);
        return obj;
    }
    public static <T> T genericMethod(T obj) {
        System.out.println("Generic print: " + obj);
        return obj;
    }
    public static <T extends Number> T numberMethod(T obj) {
        System.out.println("Number print: " + obj);
        return obj;
    }
}
