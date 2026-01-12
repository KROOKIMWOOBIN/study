## 불변객체 VS 가변객체

### String (불변객체)

#### 설명
1. new 키워드를 사용하지 않은 중복된 문자열은 문자열 상수 풀을 사용한다.
   - 다만, **new**키워드로 생성하면 문자열 상수 풀과 상관없이 항상 새로운 객체가 생성됩니다.
2. 문자열 상수 풀은 힙 메모리 영역 안에 존재하며 문자열 상수 풀에 있는 내용이 같으면 동일성 비교가 가능하다. (권장하지 않는다.)
3. 문자열을 더할 때 새로운 인스턴스가 만들어진다.
   - 단, 컴파일 타임 상수 결합은 미리 합쳐져서 새로운 객체가 생성되지 않을 수도 있다.
4. 문자열 상수 풀은 해시 기반 자료구조를 사용하여 중복을 관리한다.

#### 예시
```java
public class Main {
    public static void main(String[] args){
        String a = "TEST"; 
        String b = "TEST"; // a와 b는 같은 상수 풀 객체를 참조
        String c = a + b;  // a와 b는 변수이므로, 런타임에서 새로운 String 객체 생성
        String d = "TEST" + "TEST"; // 리터럴끼리 더하면 컴파일 단계에서 합쳐져 상수 풀의 객체를 참조
    }
}
```

### StringBuilder (가변객체)

#### 설명
1. 메서드 체이닝 기법을 사용하여 런타임에서 동적으로 붙이는 연산은 스트링 빌더가 더 빠르다.

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

## 메서드 체이닝

### 설명
1. 메서드에서 자기 참조값을 반환하면 연속하여 메서드를 호출할 수 있다.

### 예시
```java
public class MethodChainingMain {
    public static void main(String[] args){
        Address address = new Address();
        // 자신의 인스턴스를 반환하여 메서드를 연속적으로 사용이 가능하다.
        System.out.println(address.add(50).add(40));
    }
}
class Address {
    private int value;
    public Address add(int value) {
        this.value += value;
        return this;
    }
}
```