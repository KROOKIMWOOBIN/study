> [← 홈](/README.md) · [Java](/docs/java/java.md) · [중급 1편](/docs/java/core/core.md)

## 불변객체 (Immutable Object)
- 객체 생성 이후 외부에서 관찰 가능한 상태가 절대 변하지 않는 객체
- 상태 변경이 필요하면 기존 객체를 수정하지 않고 새로운 객체를 생성한다

### 왜 사용하는가?
- 멀티스레드 환경에서 동기화 없이 안전하게 공유 가능
- 예측 가능한 상태로 버그 방지
- 함수형 프로그래밍 패턴에 적합

### 장점
| 항목 | 설명 |
| --- | --- |
| 스레드 안전 | 상태 변경 불가 → 동기화 불필요 |
| 예측 가능 | 외부에서 상태 변경 불가 → 버그 감소 |
| 캐싱/공유 | 안전하게 여러 곳에서 참조 가능 |
| 방어적 복사 불필요 | 전달해도 변경 걱정 없음 |

### 단점
| 항목 | 설명 |
| --- | --- |
| 객체 생성 비용 | 상태 변경 시마다 새 객체 생성 |
| 메모리 사용 | GC 대상 객체 증가 |

### 특징
- 상태 변경 메서드(setter)가 없다
- 내부 상태를 변경할 수 있는 참조를 외부에 노출하지 않는다
- 클래스를 `final`로 선언하고, 필드를 `final`로 선언하는 것이 원칙

### 예시 코드
```java
public class Main {
    public static void main(String[] args) {
        Money money = new Money(1000);
        System.out.println(money.add(500).subtract(200).getAmount()); // 1300
    }
}

final class Money {
    private final int amount;

    public Money(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Money add(int value) {
        return new Money(this.amount + value); // 새 객체 반환
    }

    public Money subtract(int value) {
        return new Money(this.amount - value); // 새 객체 반환
    }
}
```

### String (대표적인 불변객체)
- 문자열 상수 풀(String Constant Pool) 사용
  - 문자열 리터럴은 힙 영역 내부의 문자열 상수 풀에 저장
  - 같은 리터럴은 하나의 객체만 생성되어 공유
- `new` 키워드 사용 시 상수 풀과 무관하게 항상 새로운 객체 생성
- 동일성 비교(`==`): 상수 풀 공유 시 true 가능, 하지만 `equals()` 사용이 원칙
- 문자열 연산: 더하면 새로운 `String` 객체가 생성됨 (컴파일 타임 상수 결합 제외)

```java
String a = "TEST";
String b = "TEST"; // 같은 상수 풀 객체

String c = a + b;           // 런타임 결합 → 새 객체 생성
String d = "TEST" + "TEST"; // 컴파일 타임 결합 → 상수 풀 객체

// a == b → true (같은 상수 풀)
// a + b → 런타임 시 StringBuilder 사용 후 새로운 String 생성
```

---

## 가변객체 (Mutable Object)
- 객체 생성 이후에도 내부 상태를 변경할 수 있는 객체

### 왜 사용하는가?
- 반복적인 상태 변경이 필요할 때 매번 객체를 생성하는 비용을 줄이기 위해
- 성능이 중요하고 단일 스레드 환경에서 사용할 때

### 장점
| 항목 | 설명 |
| --- | --- |
| 성능 | 객체 재사용 → GC 부담 감소 |
| 직관적 | 상태 변경이 자연스러움 |

### 단점
| 항목 | 설명 |
| --- | --- |
| 스레드 위험 | 공유 시 동기화 필요 |
| 예측 어려움 | 외부에서 상태 변경 가능 |

### 특징
- 상태 변경 메서드를 가진다
- 하나의 객체를 계속 재사용
- 성능상 이점이 있으나 공유 시 주의 필요

### StringBuilder (대표적인 가변객체)
- `String`은 불변 → 반복적인 문자열 결합 시 객체가 계속 생성됨
- `StringBuilder`는 내부 버퍼를 직접 변경 → 성능 우수
- 스레드 안전하지 않음 (단일 스레드 환경 권장)
- 스레드 안전 버전: `StringBuffer`

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello ").append("World");
String result = sb.toString();
System.out.println(result); // Hello World
```

---

## 메서드 체이닝 (Method Chaining)
- 메서드가 자기 자신의 참조(`this`)를 반환하여 연속 호출이 가능한 패턴

### 목적
- 가독성 향상
- 객체 설정 코드 간결화

### 예시 코드
```java
StringBuilder sb = new StringBuilder();
sb.append("A").append("B").append("C"); // 체이닝

// 직접 구현
class Builder {
    private int value;

    public Builder set(int v) {
        this.value = v;
        return this; // 자기 자신 반환
    }
}
```

### 어떨 때 많이 쓰는가?
| 상황 | 선택 |
| --- | --- |
| 멀티스레드 공유 객체 | 불변 객체 |
| 단일 스레드, 반복 변경 | 가변 객체 (StringBuilder 등) |
| 반복적인 문자열 결합 | `StringBuilder` |
| 스레드 안전한 문자열 결합 | `StringBuffer` |
| 값 객체 (VO), 금액 등 | 불변 객체 |
