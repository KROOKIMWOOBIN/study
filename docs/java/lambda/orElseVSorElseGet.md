> [← 홈](/study/) · [Java](/study/java/java/) · [람다 & 스트림](/study/java/lambda/lambda/)

## orElse() VS orElseGet()

### 차이점 한 줄 요약

`orElse(T)`는 **항상** 인자를 평가하고, `orElseGet(Supplier<T>)`는 값이 **없을 때만** 평가한다.

---

### 왜 구분해야 하는가?

겉보기엔 동일해 보이지만 **실행 시점**이 다르다. 인자로 비용이 큰 연산을 넘기면 성능 차이가 발생한다.

```markdown
Optional<String> opt = Optional.of("존재하는 값");

opt.orElse(heavyOperation());          // heavyOperation() 항상 실행됨
opt.orElseGet(() -> heavyOperation()); // 값이 있으므로 실행 안 됨
```

값이 있음에도 `orElse`는 `heavyOperation()`을 호출한다.

---

### 비교

| 구분 | `orElse(T)` | `orElseGet(Supplier<T>)` |
|------|-------------|--------------------------|
| 인자 타입 | 값 직접 전달 | Supplier (람다) 전달 |
| 평가 시점 | **항상** 즉시 평가 (Eager) | 값이 없을 때만 평가 (Lazy) |
| 값이 있을 때 | 인자 평가 후 무시 | 인자 평가 자체를 안 함 |
| 값이 없을 때 | 인자 반환 | Supplier 실행 결과 반환 |
| 적합한 인자 | 상수, 이미 생성된 객체 | 메서드 호출, 객체 생성, DB 조회 |

---

### 언제 orElse를 쓰는가

인자가 **상수이거나 이미 생성된 객체**일 때. 어차피 평가 비용이 없으므로 차이가 없다.

```markdown
opt.orElse("기본값");           // 문자열 리터럴 — 비용 없음
opt.orElse(0);                  // 기본 정수 — 비용 없음
opt.orElse(Collections.emptyList()); // 이미 존재하는 싱글톤 — 비용 없음
```

---

### 언제 orElseGet을 쓰는가

인자가 **새 객체 생성, 메서드 호출, DB 조회** 등 비용이 있는 연산일 때.

```markdown
// 새 객체 생성
opt.orElseGet(() -> new User("guest"));

// 다른 메서드 호출
opt.orElseGet(() -> userRepository.findDefault());

// 복잡한 연산
opt.orElseGet(() -> {
    String name = config.getDefaultName();
    return name.toUpperCase();
});
```

---

### 실수하기 쉬운 패턴

```markdown
// 문제: 값이 있어도 새 객체가 매번 생성됨
Optional<User> opt = Optional.of(existingUser);
User result = opt.orElse(new User("guest")); // new User()가 항상 실행됨

// 해결
User result = opt.orElseGet(() -> new User("guest")); // 값 없을 때만 생성
```

```markdown
// 문제: 값이 있어도 DB를 매번 조회함
User result = opt.orElse(userRepository.findDefault()); // DB 쿼리 항상 실행

// 해결
User result = opt.orElseGet(() -> userRepository.findDefault()); // 값 없을 때만 조회
```

---

### 정리

- 인자가 **상수·리터럴** → `orElse`
- 인자가 **연산·생성·IO** → `orElseGet`
- 확신이 없으면 `orElseGet`이 안전하다
