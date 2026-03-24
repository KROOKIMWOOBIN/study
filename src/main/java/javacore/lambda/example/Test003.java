package javacore.lambda.example;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Test003 {

    public static void main(String[] args) {
        List<List<Integer>> list = List.of(List.of(1, 2, 3), List.of(4, 5, 6));
        System.out.println("list = " + list);
        List<Integer> integerList = list.stream().flatMap(Collection::stream)
                .toList();
        System.out.println("integerList = " + integerList);
    }

}
