package kwb.study;

public class Student {
    String name;
    int age;
    int score;

    Student(String name, int age) {
        this(name, age, 0);
    }

    Student(String name, int age, int score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    void out() {
        System.out.println("name : " + name + ",age : " + age + ",score : " + score);
    }

}
