package ex20251111;

public class CarMain {
    public static void main(String[] args) {
        Driver driver = new Driver();

        Car k1 = new K1();
        driver.setCar(k1);
        driver.run();

        Car k2 = new K2();
        driver.setCar(k2);
        driver.run();
    }
}
interface Car {
    void on();
    void off();
    void move();
}
class K1 implements Car {
    @Override
    public void on() {
        System.out.println("K1::시동을 겁니다.");
    }
    @Override
    public void off() {
        System.out.println("K1::시동을 끕니다.");
    }
    @Override
    public void move() {
        System.out.println("K1::움직입니다.");
    }
}
class K2 implements Car {
    @Override
    public void on() {
        System.out.println("K2::시동을 겁니다.");
    }
    @Override
    public void off() {
        System.out.println("K2::시동을 끕니다.");
    }
    @Override
    public void move() {
        System.out.println("K2::움직입니다.");
    }
}
class Driver {
    private Car car;
    public void setCar(Car car) {
        System.out.println(car);
        this.car = car;
    }
    public void run() {
        car.on();
        car.move();
        car.off();
    }
}
