package javacore.lambda.example;

import java.util.List;

public class Test002 {

    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        numbers.stream()
                .filter(n -> {
                    System.out.println("filter: " + n);
                    return n > 2;
                })
                .map(n -> {
                    System.out.println("map: " + n);
                    return n * 2;
                })
                .forEach(System.out::println);
    }

}
