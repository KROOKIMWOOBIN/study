## 불변객체 (Immutable Object)
- 객체 생성 이후 외부에서 관찰 가능한 상태가 절대 변하지 않는 객체
- 상태 변경이 필요하면 기존 객체를 수정하지 않고 새로운 객체를 생성한다.

### 특징
- 상태 변경 메서드(setter 등)가 없다.
- 내부 상태를 변경할 수 있는 참조를 외부에 노출하지 않는다.
- 동시성 환경에서 스레드 안전(Thread-Safe) 하다.
- 공유해도 안전하다.

### String (대표적인 불변객체)
- 문자열 상수 풀(String Constant Pool) 사용
  - 문자열 리터럴은 힙 영역 내부의 문자열 상수 풀에 저장된다.
  - 같은 리터럴은 하나의 객체만 생성되어 공유된다.
- new 키워드 사용 시
  - 문자열 상수 풀과 무관하게 항상 새로운 객체를 생성한다.
- 동일성 비교(==)
  - 상수 풀을 공유하는 경우 == 비교가 true가 될 수 있으나 구현 의존적이므로 equals 사용이 원칙이다. 수정
- 문자열 연산
  - 문자열을 더하면 기존 객체를 수정하지 않고 새로운 String 객체가 생성된다.
  - 단, 컴파일 타임 상수 결합은 예외다.
- 중복 관리 방식
  - 문자열 상수 풀은 해시 기반 자료구조로 중복을 관리한다.

#### 예시
```java
public class Main {
   public static void main(String[] args) {
      String a = "TEST";
      String b = "TEST"; // 같은 상수 풀 객체 참조
      
      String c = a + b; // 런타임 결합 → 새로운 객체 생성
      String d = "TEST" + "TEST"; // 컴파일 타임 결합 → 상수 풀 객체
   }
}
```
- a == b → true 가능 (같은 상수 풀)
- a + b → 런타임 시 StringBuilder 사용 후 새로운 String 생성

## 가변객체 (Mutable Object)
- 객체 생성 이후에도 내부 상태를 변경할 수 있는 객체

### 특징
- 상태 변경 메서드를 가진다.
- 하나의 객체를 계속 재사용한다.
- 성능상 이점이 있으나 공유 시 주의 필요

### StringBuilder (대표적인 가변객체)
- *String*은 불변 → 반복적인 문자열 결합 시 객체가 계속 생성됨
- *StringBuilder*는 내부 버퍼를 변경 → 성능 우수

#### 특징
- 내부 char 배열을 직접 수정
- 메서드 체이닝 지원
- 스레드 안전하지 않음 (단일 스레드 환경 권장)

#### 예시
```java
public class Main {
   public static void main(String[] args) {
      StringBuilder sb = new StringBuilder();
      sb.append("Hello ").append("World");
      String result = sb.toString();
      System.out.println(result);
   }
}
```

## 메서드 체이닝 (Method Chaining)
- 메서드가 자기 자신의 참조(this)를 반환하여 연속적인 메서드 호출이 가능하게 하는 패턴

### 목적
- 가독성 향상
- 객체 설정 코드 간결화

### 예시 코드
```java
public class MethodChainingMain {
   public static void main(String[] args) {
       Address address = new Address();
       System.out.println(address.add(50).add(40));
   }
}
   
class Address {
   private int value;
   
   public Address add(int value) {
       this.value += value;
       return this; // 자기 자신 반환
   }
}
```