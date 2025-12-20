package javacore.mid.mid2.collection.compare;

import java.util.ArrayList;
import java.util.List;

public class SortMain4 {
    public static void main(String[] args) {
        MyUser user1 = new MyUser("a", 30);
        MyUser user2 = new MyUser("b", 20);
        MyUser user3 = new MyUser("c", 10);

        List<MyUser> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        System.out.println("기본 데이터");
        System.out.println(userList);

        System.out.println("Comparable 나이 정렬");
        userList.sort(null);
        // Collections.sort(userList);
        System.out.println(userList);

        System.out.println("Comparator ID 정렬");
        userList.sort(new IdComparator());
        // Collections.sort(userList, new IdComparator());
        System.out.println(userList);
    }
}
