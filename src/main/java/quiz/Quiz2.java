package quiz;

// 다형성 오버라이딩 퀴즈
public class Quiz2 {
    public static void main(String[] args) {
        Parent parent = new Child();
        System.out.println(parent.value); // 출력 결과
        parent.print(); // 출력 결과
    }
}

class Parent {
    public String value = "parent";
    public void print() {
        System.out.println("Parent.parent");
    }
}

class Child extends Parent {
    public String value = "child";
    @Override
    public void print() {
        System.out.println("Child.child");
    }
}
