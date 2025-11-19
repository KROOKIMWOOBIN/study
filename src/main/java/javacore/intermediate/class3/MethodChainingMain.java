package javacore.intermediate.class3;

public class MethodChainingMain {
    public static void main(String[] args) {
        // 이해하기 쉽도록 예제
        ValueAdder adder = new ValueAdder();
        ValueAdder adder1 = adder.add(1);
        ValueAdder adder2 = adder.add(2);
        ValueAdder adder3 = adder.add(3);
        System.out.println("adder1 value : " + adder1.getValue());
        System.out.println("adder2 value : " + adder2.getValue());
        System.out.println("adder3 value : " + adder3.getValue());

        // 자기 자신을 반환하고 있어 메서드를 체인으로 사용할 수 있다.
        ValueAdder chainAdder = new ValueAdder();
        System.out.println("chainAdder value : " + chainAdder.add(1).add(2).add(3).getValue());
    }
}
class ValueAdder {
    private int value;
    // 자기 자신 반환
    public ValueAdder add(int value) {
        this.value += value;
        return this;
    }
    public int getValue() {
        return value;
    }
}
