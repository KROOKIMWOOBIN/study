package javacore.mid.mid1.class1;

import java.lang.System;

public class LangMain {
    public static void main(String[] args) {
        System.out.println("System Livery");
        Child child = new Child();
        child.childMechod();
        child.parentMethod();
        String string = child.toString(); // toString()은 Object 클래스의 Method
        System.out.println(string);
    }
}
class Parent extends Object { // 안써도 괜찮지만, 공부용으로 명시적으로 작성
    public void parentMethod() {
        System.out.println("Parent::parent");
    }
}
class Child extends Parent {
    public void childMechod() {
        System.out.println("Child:child");
    }
}