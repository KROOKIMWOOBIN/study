package javacore.mid.mid2.collection.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OfMain {
    public static void main(String[] args) {
        // 편리한 불변 컬렉션 생성
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        Set<Integer> set = Set.of(1, 2, 3, 4, 5);
        Map<Integer, String> map = Map.of(1, "A", 2, "B");

        System.out.println("list = " + list);
        System.out.println("set = " + set);
        System.out.println("map = " + map);

        System.out.println("List Class: " + list.getClass());
        System.out.println("Set Class: " + set.getClass());
        System.out.println("Map Class: " + map.getClass());
    }
}
