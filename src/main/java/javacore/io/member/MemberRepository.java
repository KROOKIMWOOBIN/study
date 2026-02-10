package javacore.io.member;

import java.util.List;

public interface MemberRepository {

    /**
     * @apiNote 회원 등록
     * @param member 등록할 회원
     */
    void add(Member member);

    /**
     * @apiNote 모든 회원 조회
     * @return members
     */
    List<Member> findAll();

}
