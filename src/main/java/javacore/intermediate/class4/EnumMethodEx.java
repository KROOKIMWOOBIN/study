package javacore.intermediate.class4;

import java.util.Arrays;

public class EnumMethodEx {
    public static void main(String[] args) {
        Grade[] values = Grade.values(); // 모든 Enum 반환
        System.out.println("values : " + Arrays.toString(values)); // 모든 상수 출력
        for(Grade value : values) { // 상수 순서대로 반환
            System.out.println("name=" + value.name() + ", ordinal=" + value.ordinal());
        }

        String input = "GOLD";
        Grade gold = Grade.valueOf(input);
        System.out.println("Gold : " + gold);
    }
}
