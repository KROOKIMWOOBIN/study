package javacore.basic.ex6;

public class Main {
    public static void main(String[] args) {
        // 자식 인스턴스가 자식 인스턴스 참조
        Child child = new Child();
        child.print();
        System.out.println(child.value);

        // 부모 인스턴스가 부모 인스턴스 참조
        Parent parent = new Parent();
        parent.print();
        System.out.println(parent.value);

        // 부모 인스턴스가 자식 인스턴스 참조(다형적 참조)
        Parent parent1 = new Child();
        parent1.print();
        System.out.println(parent.value);
    }

    // 부모 인스턴스에 자식 인스턴스가 있는 지 확인
    private static void checkInstance(Parent parent) {
        parent.print();
        if (parent instanceof Child child) {
            child.print();
        }
    }
}

class Parent {
    public String value = "parent";
    public void print() {
        System.out.println("Parent.parent");
    }
}

class Child extends Parent{
    public String value = "child";
    @Override
    public void print() {
        System.out.println("Child.child");
    }
}
