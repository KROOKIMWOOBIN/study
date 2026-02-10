package javacore.io.member;

import javacore.io.member.impl.FileMemberRepository;
import javacore.io.member.impl.MemoryMemberRepository;

import java.util.List;
import java.util.Scanner;

public class MemberConsoleMain {

    // private static final MemberRepository repository = new MemoryMemberRepository();
    private static final MemberRepository repository = new FileMemberRepository();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("=== 1. 회원등록 | 2. 회원목록조회 | 3. 종료 ===");
            System.out.print("선택 : ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    registerMember();
                    break;
                case 2:
                    displayMembers();
                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 입력해주세요");
            }
        }
    }

    private static void registerMember() {
        System.out.print("ID 입력 : ");
        String id = sc.nextLine();

        System.out.print("Name 입력 : ");
        String name = sc.nextLine();

        System.out.print("Age 입력 : ");
        int age = sc.nextInt();
        sc.nextLine();

        repository.add(new Member(id, name, age));
        System.out.println("회원이 성공적으로 등록되었습니다.");
    }

    private static void displayMembers() {
        List<Member> memberList = repository.findAll();
        if (memberList.isEmpty()) {
            System.out.println("조회할 회원이 없습니다.");
            return;
        }
        for (int i = 0; i < memberList.size(); i++) {
            System.out.println((i+1) + ") " + memberList.get(i));
        }
    }

}
