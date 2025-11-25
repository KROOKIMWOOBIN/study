package javacore.mid.mid1.quiz;

public class StringQuiz {
    public static void main(String[] args) {
        String a = new String("TEST");
        String b = new String("TEST");
        System.out.println("a == b : " + (a == b)); // 1번 출력 결과는?
        System.out.println("a equals b : " + a.equals(b)); // 2번 출력 결과는?

        String c = "TEST";
        String d = "TEST";
        System.out.println("c == d : " + (c == d)); // 3번 출력 결과는?
        System.out.println("c equals d : " + c.equals(d)); // 4번 출력 결과는?
    }
}
