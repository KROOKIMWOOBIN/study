package javacore.mid.mid2.collection.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapMain1 {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        System.out.println("keySet()");
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            Integer value = map.get(key);
            System.out.println("key: " + key + ", value: " + value);
        }

        System.out.println("values()");
        Collection<Integer> values = map.values();
        for (Integer value : values) {
            System.out.println("value: " + value);
        }

        System.out.println("entrySet()");
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println("entry = " + entry);
        }
    }
}
