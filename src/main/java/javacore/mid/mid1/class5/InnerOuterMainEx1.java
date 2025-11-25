package javacore.mid.mid1.class5;

public class InnerOuterMainEx1 {
    public static void main(String[] args) {
        Car myCar = new Car("Model Y", 100);
        myCar.start();
    }
}
class Car {
    private String model;
    private int chargeLevel;
    private Engine engine;

    Car(String model, int chargeLevel) {
        this.model = model;
        this.chargeLevel = chargeLevel;
        this.engine = new Engine();
    }

    public void start() {
        engine.start();
        System.out.println(model + " 시작 완료");
    }

    private class Engine {
        public void start() {
            System.out.println("충전 레벨 확인 : " + chargeLevel);
            System.out.println(model + "의 엔진을 구동합니다.");
        }
    }
}