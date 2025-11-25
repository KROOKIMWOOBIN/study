package javacore.basic.ex5;

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

        // 일시적 다운캐스팅
        ((Child)parent).childPrint();

        // 업캐스팅
        parent = child;
        parent.parentPrint();
    }

    // 다운 캐스팅 전, 부모 인스턴스에 무엇을 참조중인지 검증
    private static void checkCasting(Parent parent) {
        parent.parentPrint();
        if (parent instanceof Child child) { // 다운 캐스팅 가능할 시 자식 인스턴스 바로 선언하여 사용 가능
            System.out.println("자식 인스턴스 맞음");
            child.childPrint();
        }
        else {
            System.out.println("자식 인스턴스 아님");
        }
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
