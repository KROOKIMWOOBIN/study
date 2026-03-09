package javacore.lambda.quiz;

import java.util.Arrays;

public class Ex03Main {

    public static void main(String[] args) {
        measure(() -> {
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
            }
            System.out.println("[1부터 100까지 합] 결과 : " + sum);
        });
        measure(() -> {
            int[] arr = {4, 3, 2, 1};
            System.out.println("원본 배열 : " + Arrays.toString(arr));
            Arrays.sort(arr);
            System.out.println("배열 정렬 : " + Arrays.toString(arr));
        });
    }

    private static void measure(Procedure procedure) {
        long start = System.nanoTime();
        procedure.run();
        long end = System.nanoTime();
        System.out.println("실행 시간 : " + (end - start) + "ns");
    }

    interface Procedure {
        void run();
    }

}
