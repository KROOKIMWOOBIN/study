package javacore.mid.mid2.generic.ex1;

public class BoxMain3 {
    public static void main(String[] args) {
        GenericBox<Integer> integerBox = new GenericBox<>();
        integerBox.setValue(10);
        Integer integer = integerBox.getValue();
        System.out.println("integer: " + integer);

        GenericBox<String> stringBox = new GenericBox<>();
        stringBox.setValue("hello");
        String string = stringBox.getValue();
        System.out.println("string: " + string);
    }
}
