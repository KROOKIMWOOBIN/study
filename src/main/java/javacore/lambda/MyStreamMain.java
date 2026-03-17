package javacore.lambda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class MyStreamMain {

    public static void main(String[] args) {
        List<Student> students = List.of(
                new Student("A", 70),
                new Student("B", 40),
                new Student("C", 90),
                new Student("D", 30)
        );
        System.out.println("점수가 50이상이면서, 90이하인 A, B 학생만 출력");
        MyStream.of(students)
                .filter(s -> s.getScore() >= 50)
                .filter(s -> s.getScore() <= 90)
                .filter(s -> "A".equals(s.getName()) || "B".equals(s.getName()))
                .forEach(name -> System.out.println("Name : " + name));
    }

    @Getter
    @ToString
    @AllArgsConstructor
    static class Student {
        private String name;
        private int score;
    }

}
