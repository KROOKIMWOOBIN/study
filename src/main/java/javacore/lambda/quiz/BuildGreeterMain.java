package javacore.lambda.quiz;

public class BuildGreeterMain {

    public static void main(String[] args) {
        StringFunction helloGreeter = buildGreeter("Hello");
        StringFunction hiGreeter = buildGreeter("HI");
        System.out.println(helloGreeter.apply("Java"));
        System.out.println(hiGreeter.apply("Lambda"));
        System.out.println("=== === === === === ===");
        StringFunction helloGreeterFull = buildGreeterFull("Hello");
        StringFunction hiGreeterFull = buildGreeterFull("HI");
        System.out.println(helloGreeterFull.apply("Java"));
        System.out.println(hiGreeterFull.apply("Lambda"));
    }

    private static StringFunction buildGreeter(String greeting) {
        return name -> greeting + ", " + name;
    }

    private static StringFunction buildGreeterFull(String greeting) {
        StringFunction function = new StringFunction() {
            @Override
            public String apply(String name) {
                return greeting + ", " + name;
            }
        };
        return function;
    }

    @FunctionalInterface
    interface StringFunction {
        String apply(String str);
    }

}
