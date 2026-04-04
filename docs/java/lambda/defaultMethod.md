> [← 홈](/study/) · [Java](/study/java/java/) · [람다 & 스트림](/study/java/lambda/lambda/)

## Default Method

Java 8에서 인터페이스에 **구현부를 포함할 수 있게** 된 메서드. `default` 키워드를 붙인다.

```markdown
interface Greeting {
    default void hello() {
        System.out.println("Hello!");
    }
}
```

### 왜 쓰는가?

기존 인터페이스에 메서드를 추가하면 구현 클래스 전체가 컴파일 오류가 난다. Default Method는 **기존 구현 클래스를 깨지 않고** 인터페이스에 새 기능을 추가할 수 있게 해준다.

Java 8에서 `Collection`에 `stream()`, `forEach()` 등을 추가할 때 이 방식을 사용했다.

```markdown
// Collection 인터페이스 내부 (Java 8)
default Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
}
```

### 특징

| 항목 | 내용 |
|------|------|
| 선언 위치 | 인터페이스 |
| 구현 여부 | 구현부 포함 |
| 오버라이드 | 구현 클래스에서 선택적으로 가능 |
| 상속 | 구현 클래스가 자동으로 상속받음 |
| 접근 제어자 | 항상 `public` (생략 가능) |

### 오버라이드

구현 클래스에서 필요하면 재정의할 수 있다.

```markdown
interface Greeting {
    default void hello() {
        System.out.println("Hello!");
    }
}

class KoreanGreeting implements Greeting {
    @Override
    public void hello() {
        System.out.println("안녕하세요!");  // 재정의
    }
}
```

### 다이아몬드 문제

두 인터페이스가 동일한 시그니처의 default method를 가질 때 충돌이 발생한다. 구현 클래스에서 **반드시 명시적으로 해결**해야 한다.

```markdown
interface A {
    default void hello() { System.out.println("A"); }
}
interface B {
    default void hello() { System.out.println("B"); }
}

class C implements A, B {
    @Override
    public void hello() {
        A.super.hello(); // 어느 쪽을 쓸지 명시
    }
}
```

### 단점 / 주의할 점

| 상황 | 문제 |
|------|------|
| 두 인터페이스의 default method 충돌 | 컴파일 오류 — 구현 클래스에서 반드시 오버라이드 |
| 남용 | 인터페이스가 구현 클래스처럼 비대해져 책임이 불명확해짐 |
| 상태(필드) 없음 | default method는 인터페이스 필드를 가질 수 없어 상태 기반 로직 구현 불가 |
