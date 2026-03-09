package javacore.lambda.quiz;

public class Ex01Main {

    public static void main(String[] args) {
        Print print = s -> {
            System.out.println("=== 시작 ===");
            System.out.println(s);
            System.out.println("=== 종료 ===");
        };
        print.run("Good Morning!");
        print.run("Good Afternoon!");
        print.run("Good Evening!");
    }

    @FunctionalInterface
    interface Print {
        void run(String s);
    }

}
