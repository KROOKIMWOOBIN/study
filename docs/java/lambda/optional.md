> [← 홈](/study/) · [Java](/study/java/java/) · [람다 & 스트림](/study/java/lambda/lambda/)

## Optional

### Optional이란?

`Optional<T>`는 값이 있거나 없을 수 있는 컨테이너 객체다. Java 8에서 도입됐으며, 메서드 반환 타입으로 사용해 "값이 없을 수 있음"을 타입 수준에서 표현한다.

Stream 최종 연산 중 `findFirst()`, `findAny()`, `min()`, `max()`, `reduce()` 등이 `Optional`을 반환한다.

---

### 왜 쓰는가?

**null 직접 반환 vs Optional 반환**

| 구분 | null 직접 반환 | Optional 반환 |
|------|--------------|--------------|
| NPE 가능성 | 호출자가 null 체크를 잊으면 즉시 NPE | 컴파일러가 명시적 처리를 유도 |
| 의도 표현 | 반환값이 없을 수 있음을 타입으로 표현 못함 | `Optional<T>` 타입 자체가 "없을 수 있음"을 선언 |
| 체이닝 | 단계마다 null 체크 필요 | `map`, `flatMap`, `filter`로 안전하게 체이닝 |

```markdown
// Before: 다단계 null 체크
String city = null;
if (user != null && user.getAddress() != null) {
    city = user.getAddress().getCity();
}

// After: Optional 체이닝
String city = Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("미입력");
```

**언제 쓰는가**

| 적합 | 부적합 |
|------|--------|
| 메서드 반환 타입 (DB 조회, 컬렉션 검색) | 필드 타입 (직렬화 불가) |
| 값이 없을 수 있음을 명시적으로 표현할 때 | 메서드 파라미터 |
| Stream 최종 연산 결과 처리 | 컬렉션 원소 타입 |

---

### Optional 생성

| 메서드 | 설명 | null 허용 |
|--------|------|-----------|
| `Optional.of(value)` | 값이 반드시 존재할 때 | X — null이면 NPE |
| `Optional.ofNullable(value)` | 값이 있을 수도 없을 수도 있을 때 | O — null이면 empty 반환 |
| `Optional.empty()` | "결과 없음"을 명시적으로 반환할 때 | — |

```markdown
Optional<String> a = Optional.of("hello");           // 값이 확실히 있음
Optional<String> b = Optional.ofNullable(findUser()); // DB 조회 결과
Optional<String> c = Optional.empty();               // 빈 결과 명시 반환
```

---

### 값 존재 여부 확인

| 메서드 | 설명 | Java 버전 |
|--------|------|-----------|
| `isPresent()` | 값이 있으면 true | Java 8 |
| `isEmpty()` | 값이 없으면 true | Java 11 |

```markdown
Optional<String> opt = Optional.of("hello");

if (opt.isPresent()) { ... }  // 값이 있을 때
if (opt.isEmpty()) { ... }    // 값이 없을 때
```

> **주의:** `isPresent()` + `get()` 조합은 null 체크와 다를 바 없어 Optional의 장점을 살리지 못한다. `map`, `orElse`, `ifPresent` 계열을 사용하자.

---

### 값 추출

| 메서드 | 값 없을 때 | 언제 사용 |
|--------|-----------|-----------|
| `get()` | `NoSuchElementException` | 값이 반드시 있음을 확신할 때만 (지양) |
| `orElse(T)` | 인자로 준 기본값 반환 | 기본값이 상수·단순 값일 때 |
| `orElseGet(Supplier<T>)` | Supplier 실행 결과 반환 | 기본값 생성 비용이 클 때 |
| `orElseThrow()` | `NoSuchElementException` | 없으면 반드시 에러 (Java 10+) |
| `orElseThrow(Supplier)` | Supplier가 던지는 예외 | 커스텀 예외 지정 |

```markdown
opt.orElse("기본값");
opt.orElseGet(() -> expensiveDefault());
opt.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
```

#### orElse vs orElseGet

| 구분 | `orElse(T)` | `orElseGet(Supplier<T>)` |
|------|-------------|--------------------------|
| 인자 평가 시점 | **항상 즉시 평가** (값이 있어도 실행) | **값이 없을 때만 실행** (Lazy) |
| 적합한 경우 | `"N/A"`, `0` 등 상수 | `new Object()`, DB 쿼리, 파일 I/O |

```markdown
Optional<String> opt = Optional.of("있음");

opt.orElse(expensiveMethod());          // expensiveMethod()가 호출됨!
opt.orElseGet(() -> expensiveMethod()); // expensiveMethod() 호출 안 됨
```

---

### 값 변환

| 메서드 | 인자 | 설명 | 반환 |
|--------|------|------|------|
| `map(Function<T,U>)` | `T → U` | 값 변환 (값 없으면 empty 유지) | `Optional<U>` |
| `flatMap(Function<T, Optional<U>>)` | `T → Optional<U>` | 중첩 Optional 방지 | `Optional<U>` |
| `filter(Predicate<T>)` | `T → boolean` | 조건 불충족이면 empty | `Optional<T>` |

**map — null 체크 없이 안전한 체이닝**

```markdown
String city = Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("미입력");
```

**flatMap — 반환 타입이 이미 Optional인 메서드와 연결**

`map`을 쓰면 `Optional<Optional<String>>`이 중첩되므로 `flatMap`을 사용한다.

```markdown
// getEmail()이 Optional<String>을 반환하는 경우
Optional<String> email = Optional.ofNullable(user)
    .flatMap(User::getEmail);  // map이면 Optional<Optional<String>>이 됨
```

**filter — 조건 불일치 시 empty**

```markdown
Optional.of(age)
    .filter(a -> a >= 19)
    .orElseThrow(() -> new IllegalArgumentException("미성년자"));
```

---

### 소비 (값 사용)

| 메서드 | 설명 | Java 버전 |
|--------|------|-----------|
| `ifPresent(Consumer<T>)` | 값이 있으면 Consumer 실행 | Java 8 |
| `ifPresentOrElse(Consumer, Runnable)` | 있으면 Consumer, 없으면 Runnable 실행 | Java 9 |

```markdown
// 값이 있을 때만 처리
opt.ifPresent(user -> log.info("로그인: {}", user.getName()));

// 있을 때 / 없을 때 분기
opt.ifPresentOrElse(
    user -> log.info("로그인: {}", user.getName()),
    () -> log.warn("사용자 없음")
);
```

> **주의:** `ifPresent` 내부에서 외부 변수에 값을 대입하려는 패턴(`ifPresent(v -> result = v)`)은 effectively final 제약으로 컴파일 오류다. 이 경우 `orElse` 계열을 사용한다.

---

### Stream과의 연계

| 상황 | 코드 패턴 |
|------|-----------|
| Stream 최종 연산 결과 처리 | `findFirst()`, `findAny()`, `min()`, `max()` → Optional |
| `Optional<T>` → `Stream<T>` | `optional.stream()` (Java 9+) — 0개 또는 1개 요소 스트림 |
| `List<Optional<T>>`에서 값만 추출 | `flatMap(Optional::stream)` |

```markdown
// findFirst 결과를 Optional로 처리
String name = users.stream()
    .filter(u -> u.getId() == targetId)
    .findFirst()
    .map(User::getName)
    .orElse("미등록 사용자");

// Optional::stream — 값 있는 것만 추출
List<Optional<String>> optionals = List.of(Optional.of("A"), Optional.empty(), Optional.of("B"));
List<String> present = optionals.stream()
    .flatMap(Optional::stream)  // empty는 제거, 값 있는 것만
    .toList();  // ["A", "B"]
```

---

### 단점 / 주의할 점

| 안티패턴 | 문제 | 대안 |
|----------|------|------|
| `Optional`을 필드 타입으로 사용 | `Serializable` 미구현 — 직렬화 불가 | 반환 타입으로만 사용 |
| `Optional`을 메서드 파라미터로 사용 | 호출부 불편, 코드 복잡도 증가 | 오버로딩 또는 null 허용 파라미터 |
| `isPresent()` + `get()` 조합 | null 체크와 동일, Optional 도입 효과 없음 | `map`, `orElse`, `ifPresent` 사용 |
| `orElse(expensiveOp())` | 값이 있어도 항상 실행됨 | `orElseGet(() -> expensiveOp())` |
| `get()` 남용 | 값 없으면 `NoSuchElementException` | `orElseThrow()` 또는 `isPresent()` 선검사 |
| `List<Optional<T>>` | 불필요하게 복잡 | 빈 컬렉션 반환으로 대체 |
| `Optional.of(null)` | 즉시 NPE | null 가능성이 있으면 `ofNullable` 사용 |
| `Optional`을 반환하는 메서드가 null 반환 | Optional의 의미가 사라짐 | 반드시 `Optional.empty()` 반환 |
