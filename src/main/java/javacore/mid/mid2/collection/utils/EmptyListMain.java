package javacore.mid.mid2.collection.utils;

import java.util.*;

public class EmptyListMain {
    public static void main(String[] args) {
        // 빈 가변 리스트 생성
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new LinkedList<>();

        // 빈 불변 리스트 생성
        List<Integer> list3 = Collections.emptyList(); // 자바5
        List<Integer> list4 = List.of(); // 자바9

        System.out.println("list3.getClass() = " + list3.getClass());
        System.out.println("list4.getClass() = " + list4.getClass());

        Integer[] arr = {1,2,3};
        List<Integer> list5 = Arrays.asList(arr);
        System.out.println("list5 = " + list5);
        list5.set(0, 100);
        System.out.println("list5 = " + list5);
        System.out.println("arr = " + Arrays.toString(arr));
    }
}
