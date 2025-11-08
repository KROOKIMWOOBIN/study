package ex20251108;

public class MainCar {
    // 다형성 테스트
    public static void main(String[] args) {
        // 자식 인스턴스 참조
        Parent parent = new Child(); // 부모 인스턴스 참조하면 메모리에 자식 인스턴스 생성이 안되어 캐스팅 불가능
        parent.parentPrint();

        // 다운캐스팅
        Child child = (Child)parent;
        child.childPrint();
        child.parentPrint();

        // 업캐스팅
        parent = (Parent) child;
        parent.parentPrint();
    }

}

class Parent {
    public void parentPrint() {
        System.out.println("Parent::Parent");
    }
}

class Child extends Parent {
    public void childPrint() {
        System.out.println("Child::Child");
    }
}
