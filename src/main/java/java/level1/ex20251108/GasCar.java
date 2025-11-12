package java.level1.ex20251108;

public class GasCar extends Car {

    public void fillGas() {
        System.out.println("수소를 충전합니다.");
    }

    @Override
    public void move() {
        System.out.println("테스트");
    }

}
