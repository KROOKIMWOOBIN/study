package javacore.lambda.quiz;

public class ComposeMain {

    public static void main(String[] args) {
        MyTransformer toUpper = String::toUpperCase;
        MyTransformer addDeco = s -> "**" + s + "**";
        MyTransformer composeFunc = compose(toUpper, addDeco);
        String result = composeFunc.transform("hello");
        System.out.println("result = " + result);
    }

    private static MyTransformer compose(MyTransformer f1, MyTransformer f2) {
        return s -> f2.transform(f1.transform(s));
    }

    @FunctionalInterface
    interface MyTransformer {
        String transform(String s);
    }

}
