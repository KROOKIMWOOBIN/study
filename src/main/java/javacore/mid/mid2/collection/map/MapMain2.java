package javacore.mid.mid2.collection.map;

import java.util.HashMap;
import java.util.Map;

public class MapMain2 {
    public static void main(String[] args) {
        Map<String, Integer> studentMap = new HashMap<>();
        
        studentMap.put("studentA", 90); // 학생 성적 데이터 추가
        System.out.println(studentMap);

        studentMap.put("studentA", 100); // 같은 키에 값 저장 시 기존 값 변경
        System.out.println(studentMap);

        boolean containsKey = studentMap.containsKey("studentA");
        System.out.println("containsKey = " + containsKey);

        studentMap.remove("studentA"); // 특정 학생의 값 삭제
        System.out.println(studentMap);
    }
}
