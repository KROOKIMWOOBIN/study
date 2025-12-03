package javacore.mid.mid2.collection.set;

public class HashStart4 {

    static final int CAPACITY = 10;

    public static void main(String[] args) {
        Integer[] inputArray = new Integer[CAPACITY];
        add(inputArray, 1);
        add(inputArray, 2);
        add(inputArray, 5);
        add(inputArray, 8);
        add(inputArray, 14);
        add(inputArray, 99);
        int searchValue = hashIndex(14);
        Integer result = inputArray[searchValue];
        System.out.println("result = " + result);
    }

    private static void add(Integer[] inputArray, int value) {
        int hashIndex = hashIndex(value);
        inputArray[hashIndex] = value;
    }

    static int hashIndex(int value) {
        return value % CAPACITY;
    }
}
