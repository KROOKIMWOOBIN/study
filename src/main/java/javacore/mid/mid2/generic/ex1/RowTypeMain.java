package javacore.mid.mid2.generic.ex1;

public class RowTypeMain {
    public static void main(String[] args) {
        GenericBox integerBox = new GenericBox();
        // GenericBox<Object> integerBox = new GenericBox<>();
        integerBox.setValue(10);
        Integer result = (Integer) integerBox.getValue();
        System.out.println("result: " + result);
    }
}
