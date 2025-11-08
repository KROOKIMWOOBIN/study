package study.quiz;

public class Quiz1 {

    public static void main(String[] args) {
        Member member = new Member();
        changeAge(member);
        System.out.println("before member age : " + member.age);

        int a = 10;
        changeNumber(a);
        System.out.println("before number : " + a);
    }

    private static void changeNumber(int number) {
        number = 100;
        System.out.println("after number : " + number);
    }

    private static void changeAge(Member member) {
        member.age = 20;
        System.out.println("after member age : " + member.age);
    }

}

class Member {
    int age = 10;
}
