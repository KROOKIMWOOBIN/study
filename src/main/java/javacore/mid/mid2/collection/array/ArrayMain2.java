package javacore.mid.mid2.collection.array;

import java.util.Arrays;

public class ArrayMain2 {

    private static int[] arr = {1, 2, 0, 0, 0};

    public static void main(String[] args) {
        printArray();
        System.out.println("배열의 첫번째 위치에 3 추가 O(n)");
        addFirst(3);
        printArray();
        System.out.println("배열의 중간(2)에 4 추가 O(n/2)");
        addAtIndex(2, 4);
        printArray();
        System.out.println("배열의 마지막에 5 추가 O(1)");
        addLast(5);
        printArray();
    }

    private static void printArray() {
        System.out.println(Arrays.toString(arr));
    }

    private static void addFirst(int value) {
        for (int i = arr.length - 1; i > 0; i--) {
            arr[i] = arr[i - 1];
        }
        arr[0] = value;
    }

    private static void addAtIndex(int index, int value) {
        for (int i = arr.length - 1; i > index; i--) {
            arr[i] = arr[i - 1];
        }
        arr[index] = value;
    }

    private static void addLast(int value) {
        arr[arr.length - 1] = value;
    }

}
