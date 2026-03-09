package javacore.lambda.quiz;

public class Ex02Main {

    public static void main(String[] args) {
        Print print = (weight, unit) -> System.out.println("무게 : " + weight + unit);
        print.run(10, "kg");
        print.run(50, "kg");
        print.run(200, "g");
        print.run(40, "g");
    }

    @FunctionalInterface
    interface Print {
        void run(int weight, String unit);
    }

}
