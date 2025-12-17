package javacore.mid.mid2.collection.map.test;

import java.util.HashMap;
import java.util.Map;

public class CommonKeyValueSum {
    public static void main(String[] args) {
        Map<String, Integer> map1 = Map.of("A", 1, "B", 2, "C", 3);
        Map<String, Integer> map2 = Map.of("B", 4, "C", 5, "D", 6);

        Map<String, Integer> result = new HashMap<>();
        map1.forEach((key, value) -> {
            if (map2.containsKey(key)) {
                result.put(key, (value + map2.get(key)));
            }
        });
        System.out.println(result);
    }
}
