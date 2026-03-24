package javacore.lambda.example;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Test003 {

    public static void main(String[] args) {
        List<List<Integer>> list = List.of(List.of(1, 2, 3), List.of(4, 5, 6));
        Stream<List<Integer>> stream = list.stream();
        System.out.println("stream = " + stream);
        stream.flatMap(Collection::stream)
                .forEach(n -> System.out.print(n + ", "));
    }

}
