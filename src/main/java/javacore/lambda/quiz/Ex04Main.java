package javacore.lambda.quiz;

import java.util.ArrayList;
import java.util.List;

public class Ex04Main {

    public static void main(String[] args) {
        List<Integer> list = List.of(-3, -2, -1, 1, 2, 3, 5);
        System.out.println("원본 리스트: " + list);
        System.out.println("음수만: " + filter(list, i -> i < 0));
        System.out.println("짝수만: " + filter(list, i -> i % 2 == 0));
    }

    private static List<Integer> filter(List<Integer> list, MyPredicate predicate) {
        List<Integer> newList = new ArrayList<>();
        for (Integer i : list) {
            if (predicate.test(i)) {
                newList.add(i);
            }
        }
        return newList;
    }

    interface MyPredicate {
        boolean test(int value);
    }

}
