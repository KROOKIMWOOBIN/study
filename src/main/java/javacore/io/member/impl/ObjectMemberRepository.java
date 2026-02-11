package javacore.io.member.impl;

import javacore.io.member.Member;
import javacore.io.member.MemberRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectMemberRepository implements MemberRepository {

    private static final String FILE_PATH = "temp/members-obj.dat";

    @Override
    public void add(Member member) {
        List<Member> members = findAll();
        members.add(member);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(members);
        } catch (IOException e) {
            throw new RuntimeException("파일 쓰기 에러 발생");
        }
    }

    @Override
    public List<Member> findAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object findObject = ois.readObject();
            return (List<Member>)findObject;
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 읽기 에러 발생");
        }
    }

}
