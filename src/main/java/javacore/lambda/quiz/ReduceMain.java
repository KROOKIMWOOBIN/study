package javacore.lambda.quiz;

import java.util.List;

public class ReduceMain {

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4);
        System.out.println("리스트: " + list);
        System.out.println("합(누적 +): " + reduce(list, 0, (a, b) -> a + b));
        System.out.println("곱(누적 *): " + reduce(list, 1, (a, b) -> a * b));
    }

    private static int reduce(List<Integer> list, int initial, MyReduce reduce) {
        for (int i = 0; i < list.size(); i++) initial = reduce.run(initial, list.get(i));
        return initial;
    }

    @FunctionalInterface
    interface MyReduce {
        int run(int a, int b);
    }

}
