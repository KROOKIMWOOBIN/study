package javacore.intermediate.test;

import java.sql.SQLOutput;
import java.util.Scanner;

public class EnumEx1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AuthGrade[] authGrades = AuthGrade.values();
        for(AuthGrade authGrade : authGrades) {
            authGrade.getInfo();
        }
        System.out.println("당신의 등급을 입력하세요 : ");
        String grade = sc.nextLine();
        AuthGrade authGrade = AuthGrade.valueOf(grade);
        authGrade.getInfo();
    }
}
enum AuthGrade {
    GUEST(1, "손님"),
    LOGIN(2, "로그인 회원"),
    ADMIN(3, "관리자");

    private final int level;
    private final String description;

    AuthGrade(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public void getInfo() {
        System.out.println("grade=" + this + ", level=" + level + ", 설명=" + description);
    }
}
