package javacore.mid.mid1.class2;

import lombok.Getter;

public class MutableMain {
    public static void main(String[] args) {
        ImmutableObj obj1 = new ImmutableObj(10);
        System.out.println(obj1.getValue());
        ImmutableObj obj2 = obj1.add(50);
        System.out.println(obj2.getValue());
    }
}
@Getter
class ImmutableObj {
    private final int value;
    public ImmutableObj(int value) {
        this.value = value;
    }
    public ImmutableObj add(int value) {
        int result = this.value + value;
        return new ImmutableObj(result);
    }
}
