package javacore.mid.mid2.collection.map.test;

import java.util.HashMap;
import java.util.Map;

public class ArrayToMapTest {
    public static void main(String[] args) {
        String[][] productArr = {{"JAVA", "10000"}, {"Spring", "20000"}, {"JPA", "30000"}};
        Map<String, Integer> productMap = new HashMap<>();
        for (String[] strings : productArr) {
            productMap.put(strings[0], Integer.valueOf(strings[1]));
        }
        productMap.forEach((key, value) -> {
            System.out.println("제품: " + key + ", 가격: " + value);
        });
    }
}
