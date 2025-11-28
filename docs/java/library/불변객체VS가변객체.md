## 불변객체 VS 가변객체
### String
#### 설명 
1. 불변객체
2. new 키워드를 사용하지 않은 중복된 문자열은 문자열 풀을 사용한다.
3. 문자열 풀은 힙 메모리 영역 안에 존재하며 문자열 풀에 있는 내용이 같으면 동일성 비교가 가능하다.
4. 문자열을 더할 때 새로운 인스턴스를 만들어야 한다.
5. 해시 알로기름을 사용하여 문자열 풀에 문자열을 빠르게 찾아간다.
### 예시
```java
public class Main {
    public static void main(String[] args){
        String a = "TEST"; 
        String b = "TEST"; // a랑 동일한 문자열로 힙 영역에 같은 주소를 참조한다.
        String c = a + b; // 불변은 새로운 인스턴스를 만들어 반환한다. 예시) new StringBuilder().append(a).append(b).toString();
    }
}
```
### StringBuilder
#### 설명 
1. 가변 객체
2. 메서드 체이닝 기법 사용
3. 문자열을 더할 때 새로운 인스턴스를 만들기 싫어 위 클래스를 사용한다.
4. 사이드 이펙트를 방지하기 위해 마지막에 toString()을 사용해 String(불변) 인스턴스에 값을 담아준다.
### 예시
```java
public class Main {
    public static void main(String[] args){
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append("World");
        String result = sb.toString();
        System.out.println(result);
    }
}
```