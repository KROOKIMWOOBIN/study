package javacore.lambda;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class MyStreamMain {

    public static void main(String[] args) {
        List<Student> students = List.of(
                new Student("A", 70),
                new Student("B", 40),
                new Student("C", 90),
                new Student("D", 30)
        );
        
    }

    @Getter
    @AllArgsConstructor
    static class Student {
        private String name;
        private int score;
    }

}
