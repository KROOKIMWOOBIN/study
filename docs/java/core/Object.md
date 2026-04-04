> [← 홈](/study/) · [Java](/study/java/java/) · [중급 1편](/study/java/core/core/)

## Object (java.lang.Object)
- 자바의 모든 클래스가 암묵적으로 상속받는 최상위 클래스
- 명시적으로 상속을 선언하지 않아도 자동으로 `Object`를 상속

### 왜 사용하는가?
- 모든 클래스의 공통 기능(동등 비교, 해시, 문자열 표현 등)을 제공
- 다형성의 최상위 타입으로 모든 객체를 `Object` 타입으로 다룰 수 있음
- 프레임워크, 컬렉션 등이 타입에 무관하게 객체를 처리할 수 있는 기반

### 특이점
- `import` 없이 사용 가능 (`java.lang` 패키지)
- 모든 배열도 `Object` 타입으로 취급 가능
- 자바에서 단일 루트 계층 구조의 핵심

### 주요 메서드
| 메서드 | 설명 | 재정의 여부 |
| --- | --- | --- |
| `equals(Object o)` | 동등성 비교 (기본: 동일성 `==`) | 재정의 권장 |
| `hashCode()` | 해시 코드 반환 (기본: 주소 기반) | `equals`와 함께 재정의 |
| `toString()` | 문자열 표현 (기본: `클래스명@해시`) | 재정의 권장 |
| `getClass()` | 런타임 클래스 정보 반환 | 재정의 불가 (final) |
| `clone()` | 객체 복사 | 필요 시 재정의 |
| `wait()` / `notify()` | 스레드 동기화 | 재정의 불가 |

### equals와 hashCode 재정의 규칙
- `equals()`가 true인 두 객체는 반드시 같은 `hashCode()`를 가져야 한다
- `hashCode()`가 같아도 `equals()`는 false일 수 있음 (해시 충돌)

```java
class Member {
    String id;
    String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member m)) return false;
        return Objects.equals(id, m.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name=" + name + "}";
    }
}
```

### 모든 타입을 담는 최상위 타입으로 활용
```java
Object[] objects = {new Dog(), new Cat(), "문자열", 42};
for (Object obj : objects) {
    System.out.println(obj); // toString() 호출
}
```

### 어떨 때 많이 쓰는가?
- `equals()` / `hashCode()` 재정의: 값 기반 동등 비교가 필요한 엔티티, VO 클래스
- `toString()` 재정의: 디버깅, 로깅 시 객체 상태를 문자열로 표현
- `Object` 타입 활용: 제네릭 이전 코드, 리플렉션, 범용 유틸리티
