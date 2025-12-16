package javacore.mid.mid2.collection.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapTest1 {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        Collection<Integer> a = map.values();
    }
}
