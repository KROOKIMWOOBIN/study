package javacore.io.member;

import lombok.Getter;

@Getter
public class Member {

    private String id;
    private String name;
    private int age;

    public Member() {
    }

    public Member(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "[ID : " + id + " | NAME : " + name + " | AGE : " + age + "]";
    }

}
