package javacore.mid.mid1.class3;

public class WrapperVsPrimitive {
    public static void main(String[] args) {
        int iterations = 1_000_000_000;
        long start, end;
        long sumPrimitive = 0L;
        start = System.currentTimeMillis();
        for(int i = 0; i < iterations; i++) {
            sumPrimitive += i;
        }
        end = System.currentTimeMillis();
        System.out.println(sumPrimitive);
        System.out.println("기본 자료형 Long 실행 시간 : " + (end - start) + "ms");

        Long sumWrapper = 0L;
        start = System.currentTimeMillis();
        for(int i = 0; i < iterations; i++) {
            sumWrapper += i;
        }
        end = System.currentTimeMillis();
        System.out.println(sumWrapper);
        System.out.println("래퍼 클래스 Long 실행 시간 : " + (end - start) + "ms");

    }
}
