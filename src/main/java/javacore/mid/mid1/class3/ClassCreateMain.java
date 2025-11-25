package javacore.mid.mid1.class3;

public class ClassCreateMain {
    public static void main(String[] args) throws Exception {
        Class helloClass = Class.forName("javacore.mid.mid1.class3.Hello");
        Hello hello = (Hello) helloClass.getDeclaredConstructor().newInstance(); // Hello에 생성자 선택 후 인스턴스 생성
        hello.hello();
    }
}
class Hello {
    public void hello() {
        System.out.println("Hello");
    }
}
