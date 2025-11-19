package javacore.intermediate.class2;

public class StringMain {
    public static void main(String[] args) {
        String a = new String("TEST"); // 객체 생성 생략 가능
        String b = new String("TEST");
        System.out.println(a.concat(b));
        System.out.println(a + b); // 객체는 연산이 허용되지 않지만 String 허용함
        System.out.println("a == b : " + (a == b));
        System.out.println("a equals b : " + a.equals(b)); // equals 따로 오버라이딩 안해도 비교가 가능하다.

        String c = "TEST";
        String d = "TEST";
        System.out.println("c == d : " + (c == d)); // 문자열 풀로 인해 TRUE 나옴
        System.out.println("c equals d : " + c.equals(d));

        String sumString = "";
        StringBuilder sb = new StringBuilder();
        sb.append("A");
        sb.append("B");
        sb.append("C");
        sb.append("D");
        sumString = sb.toString();
        System.out.println(sumString);
    }
}