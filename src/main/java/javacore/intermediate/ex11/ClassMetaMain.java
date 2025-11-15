package javacore.intermediate.ex11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassMetaMain {
    public static void main(String[] args) throws Exception {
        // Class 조회
        Class class1 = String.class; // 1. 클래스에서 조회
        Class class2 = new String().getClass(); // 2. 인스턴에서 조회
        Class class3 = Class.forName("java.lang.String"); // 3. 문자열로 조회

        // 모든 필드 출력
        Field[] fields = class1.getDeclaredFields();
        for(Field field : fields) {
            System.out.println("field : " + field);
        }

        // 모든 메서드 출력
        Method[] methods = class1.getDeclaredMethods();
        for(Method method : methods) {
            System.out.println("method : " + method);
        }

        // 상위 클래스 정보 출력
        System.out.println("SuperClass : " + class1.getSuperclass().getName());

        // 인터페이스 정보 출력
        Class[] interfaces = class1.getInterfaces();
        for(Class i : interfaces) {
            System.out.println("Interface : " + i);
        }
    }
}
