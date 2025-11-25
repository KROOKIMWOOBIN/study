package javacore.mid.mid1.class2;

import lombok.Getter;
import lombok.Setter;

public class ImmutableMain {
    public static void main(String[] args) {
        Address address = new Address("서울");
        Member memberA = new Member("철수", address);
        Member memberB = new Member("짱구", address);
        System.out.println("---서울---");
        System.out.println(memberA);
        System.out.println(memberB);
        memberB.setAddress(new Address("부산"));
        System.out.println("---부산---");
        System.out.println(memberA);
        System.out.println(memberB);
    }
}
@Getter
class Address {
    private final String value;
    public Address(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return "Address{" +
                "value='" + value + '\'' +
                '}';
    }
}
@Getter
@Setter
class Member {
    private String name;
    private Address address;
    public Member(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
