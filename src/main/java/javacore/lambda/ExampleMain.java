package javacore.lambda;

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
        MyFunction3<Integer> function3_1 = x -> x * x;
        System.out.println("function3_1.run(10) = " + function3_1.run(10));
        MyFunction3<String> function3_2 = x -> "Hello " + x;
        System.out.println("function3_2.run(\"Generic\") = " + function3_2.run("Generic"));

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
    interface MyFunction3<T> {
        T run(T t);
    }

}
