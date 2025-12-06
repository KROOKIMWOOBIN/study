package javacore.mid.mid2.collection.set;

public class MyHashSetV1Main {
    public static void main(String[] args) {
        MyHashSetV1 set = new MyHashSetV1(10);
        set.add(1);
        set.add(2);
        set.add(5);
        set.add(8);
        set.add(14);
        set.add(99);
        set.add(9);
        System.out.println(set);

        int searchValue = 9;
        System.out.println("set.contains(" + searchValue + ") = " + set.contains(searchValue));

        System.out.println("set.remove(" + searchValue + ") = " + set.remove(searchValue));
        System.out.println(set);
    }
}
