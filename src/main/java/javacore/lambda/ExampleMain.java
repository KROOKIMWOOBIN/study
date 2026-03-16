package javacore.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExampleMain {

    public static void main(String[] args) {
        MyFunction1 function1  = () -> System.out.println("Hello Lambda");
        function1.run();

        // 단일 표현식인 경우 {} 생략 가능
        MyFunction2 function2_1 = (a, b) -> a + b;
        System.out.println("function2_1.apply(10, 20) = " + function2_1.apply(10, 20));

        // 단일 표현식이 아닌 경우 {} 생략 불가능
        MyFunction2 function2_2 = (a, b) -> {
            System.out.println("단일 표현식이 아닌 경우");
            return a + b;
        };
        System.out.println("function2_2.apply(10 + 20) = " + function2_2.apply(10, 20));

        // 매개변수가 1개일 경우 () 생략 가능
        MyCall call = value -> value * 2;
        System.out.println("call.run(30) = " + call.run(30));

        // 제네릭 람다
        MyFunction3<Integer, Integer> function3_1 = x -> x * x;
        System.out.println("function3_1.run(10) = " + function3_1.run(10));
        MyFunction3<String, String> function3_2 = x -> "Hello " + x;
        System.out.println("function3_2.run(\"Generic\") = " + function3_2.run("Generic"));

        Function<String, String> function4 = s -> s + " Function";
        System.out.println(function4.apply("Default"));

        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        System.out.println(filter(list, e -> e % 2 == 0));
    }

    private static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return result;
    }

    @FunctionalInterface
    interface MyFunction1 {
        void run();
    }

    @FunctionalInterface
    interface MyFunction2 {
        int apply(int a, int b);
    }

    @FunctionalInterface
    interface MyCall {
        int run(int value);
    }

    @FunctionalInterface
    interface MyFunction3<R, T> {
        R run(T t);
    }

}
