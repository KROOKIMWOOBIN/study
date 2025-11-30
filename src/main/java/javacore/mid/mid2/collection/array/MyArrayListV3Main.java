package javacore.mid.mid2.collection.array;

public class MyArrayListV3Main {
    public static void main(String[] args) {
        MyArrayListV3 list = new MyArrayListV3();
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println(list);

        System.out.println("addList");
        list.add(3, "addList"); // O(1)
        System.out.println(list);

        System.out.println("addFirst");
        list.add(0, "addFirst"); // O(n)
        System.out.println(list);

        System.out.println("remove");
        Object remove = list.remove(4);
        System.out.println("remove(4) = " + remove);
        System.out.println(list);
    }
}
