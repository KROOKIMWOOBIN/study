package javacore.lambda.quiz;

import java.util.ArrayList;
import java.util.List;

public class MapMain {

    public static void main(String[] args) {
        List<String> list = List.of("hello", "java", "lambda");
        System.out.println("원본 리스트: " + list);
        System.out.println("대문자 변환 결과: " + map(list, String::toUpperCase));
        System.out.println("특수문자 데코 결과: " + map(list, s -> "***" + s + "***"));
    }

    private static List<String> map(List<String> list, StringFunction func) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(func.apply(s));
        }
        return newList;
    }

    @FunctionalInterface
    interface StringFunction {
        String apply(String s);
    }

}
