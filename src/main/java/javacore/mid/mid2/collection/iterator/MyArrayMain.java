package javacore.mid.mid2.collection.iterator;

import java.util.Iterator;

public class MyArrayMain {
    public static void main(String[] args) {
        MyArray myArray = new MyArray(new int[]{1, 2, 3, 4});
        Iterator<Integer> iterator = myArray.iterator();
        while (iterator.hasNext()) {
            System.out.println("iterator.next() = " + iterator.next());
        }

        System.out.println("for-each 사용");
        for (Integer integer : myArray) {
            System.out.println("integer = " + integer);
        }
    }
}
