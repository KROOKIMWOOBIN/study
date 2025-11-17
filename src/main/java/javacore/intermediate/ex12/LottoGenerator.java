package javacore.intermediate.ex12;

import java.util.Random;

public class LottoGenerator {
    public static void main(String[] args) {
        Lotto lotto = new Lotto();
        for(int i = 0; i < 10; i++) {
            lotto.printRound();
            lotto.generate();
            lotto.printLotto();
        }
    }
}
class Lotto {

    private final int[] lottoNumbers = new int[6];
    private int round = 1;

    public void generate() {
        Random random = new Random();
        int count = 0;
        while(count < 6) {
            int number = random.nextInt(45) + 1;
            if(isUnique(number)) {
                lottoNumbers[count] = number;
                count++;
            }
        }
        round++;
    }

    private boolean isUnique(int number) {
        for(int n : lottoNumbers) {
            if(n == number) {
                return false;
            }
        }
        return true;
    }

    public void printLotto() {
        System.out.print("로또 번호:");
        for(int i = 0; i < lottoNumbers.length; i++) {
            System.out.printf(" %2d", lottoNumbers[i]);
        }
        System.out.println("\n");
    }

    public void printRound() {
        System.out.println(round + "회차 로또");
    }

}
