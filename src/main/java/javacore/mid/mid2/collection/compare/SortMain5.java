package javacore.mid.mid2.collection.compare;

import java.util.Set;
import java.util.TreeSet;

public class SortMain5 {
    public static void main(String[] args) {
        MyUser user1 = new MyUser("a", 30);
        MyUser user2 = new MyUser("b", 20);
        MyUser user3 = new MyUser("c", 10);

        Set<MyUser> treeSet1 = new TreeSet<>();
        treeSet1.add(user1);
        treeSet1.add(user2);
        treeSet1.add(user3);
        System.out.println("Comparable 기본 정렬");
        System.out.println(treeSet1);

        Set<MyUser> treeSet2 = new TreeSet<>(new IdComparator());
        treeSet2.add(user1);
        treeSet2.add(user2);
        treeSet2.add(user3);
        System.out.println("Comparator 정렬");
        System.out.println(treeSet2);
    }
}
