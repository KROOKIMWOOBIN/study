package javacore.lambda.quiz;

public class BuildGreeterMain {

    public static void main(String[] args) {
        StringFunction helloGreeter = buildGreeter("Hello");
        StringFunction hiGreeter = buildGreeter("HI");
        System.out.println(helloGreeter.apply("Java"));
        System.out.println(hiGreeter.apply("Lambda"));
    }

    private static StringFunction buildGreeter(String greeting) {
        return name -> greeting + ", " + name;
    }

    @FunctionalInterface
    interface StringFunction {
        String apply(String str);
    }

}
