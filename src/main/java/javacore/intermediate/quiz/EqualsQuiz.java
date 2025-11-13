package javacore.intermediate.quiz;

import java.util.Objects;

public class EqualsQuiz {
    public static void main(String[] args) {
        User1 user1 = new User1("1");
        User1 user2 = new User1("1");
        System.out.println("result : " + (user1 == user2)); // 출력 결과
        System.out.println("result : " + (user1.equals(user2))); // 출력 결과

        User2 user3 = new User2("1");
        User2 user4 = new User2("1");
        System.out.println("result : " + (user3 == user4)); // 출력 결과
        System.out.println("result : " + (user3.equals(user4))); // 출력 결과
    }
}
class User1 {
    String id;
    public User1(String id) {
        this.id = id;
    }
}
class User2 {
    String id;
    public User2(String id) {
        this.id = id;
    }
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        User2 user2 = (User2) object;
        return Objects.equals(id, user2.id);
    }
}
