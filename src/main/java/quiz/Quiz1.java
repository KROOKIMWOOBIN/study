package quiz;

// 기본형 vs 참조형 퀴즈
public class Quiz1 {

    public static void main(String[] args) {
        // before number과 afoter number에 출력 결과는?
        int a = 10;
        changeNumber(a);
        System.out.println("before number : " + a);

        // before member과 after member에 출력 결과는?
        Member member = new Member();
        changeAge(member);
        System.out.println("before member age : " + member.age);
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
