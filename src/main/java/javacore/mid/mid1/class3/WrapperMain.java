package javacore.mid.mid1.class3;

public class WrapperMain {
    public static void main(String[] args) {
        MyInteger myInteger = new MyInteger(10);
        System.out.println(myInteger.compareTo(20));

        MyInteger[] myIntegers = {new MyInteger(1), new MyInteger(-1), new MyInteger(0)};
        System.out.println(myInteger.findValue(myIntegers, 1));
        System.out.println(myInteger.findValue(myIntegers, 100));
    }
}
class MyInteger {
    private final int value;

    MyInteger(int value) {
        this.value = value;
    }

    public int compareTo(int value) {
        if(this.value > value) {
            return 1;
        }
        else if(this.value < value) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public MyInteger findValue(MyInteger[] myIntegers, int target) {
        for(MyInteger myInteger : myIntegers) {
            if(myInteger.value == target) {
                return myInteger;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
