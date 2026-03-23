package javacore.lambda.example;

import java.util.ArrayList;
import java.util.List;

public class test001 {

    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        List<Integer> filtered = new ArrayList<>();
        for (Integer n : numbers) {
            System.out.println("filter: " + n);
            if (n > 2) {
                filtered.add(n);
            }
        }

        List<Integer> mapped = new ArrayList<>();
        for (Integer n : filtered) {
            System.out.println("map: " + n);
            mapped.add(n * 2);
        }

        System.out.println(mapped);
    }

}
