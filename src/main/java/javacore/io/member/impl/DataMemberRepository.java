package javacore.io.member.impl;

import javacore.io.member.Member;
import javacore.io.member.MemberRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataMemberRepository implements MemberRepository {

    private static final String FILE_PATH = "temp/members-data.dat";

    @Override
    public void add(Member member) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(FILE_PATH, true))) {
            dos.writeUTF(member.getId());
            dos.writeUTF(member.getName());
            dos.writeInt(member.getAge());
        } catch (IOException e) {
            throw new RuntimeException("파일 쓰기 에러 발생");
        }
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(FILE_PATH))) {
            while (dis.available() > 0) {
                members.add(new Member(dis.readUTF(), dis.readUTF(), dis.readInt()));
            }
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 에러 발생");
        }
        return members;
    }

}
