> [← 홈](/index.md) · [Java](/java/java.md) · [람다 & 스트림](/java/lambda/lambda.md)

## 즉시 연산 VS 지연 연산

### 개념

| 구분 | 즉시 연산 (Eager Evaluation) | 지연 연산 (Lazy Evaluation) |
|------|----------------------------|-----------------------------|
| 실행 시점 | 연산 정의 즉시 실행 | 최종 연산 호출 시점까지 실행 안 함 |
| 처리 단위 | 전체 데이터를 단계별로 처리 | 요소 하나씩 파이프라인 전체 통과 |
| 중간 결과 | 매 단계마다 컬렉션 생성 | 중간 결과 저장 없음 |
| 메모리 | 높음 | 낮음 |
| 최적화 | 어려움 | Short-Circuiting 가능 |
| 대표 예시 | for-loop, 컬렉션 직접 조작 | Stream API |

---

### 왜 쓰는가?

즉시 연산은 불필요한 연산을 포함할 수 있다. 지연 연산은 **실제로 필요한 만큼만** 실행해 성능을 높인다.

```markdown
// 즉시 연산: filter 전체 → map 전체 (5번 + 3번 = 8번 실행)
List<Integer> filtered = new ArrayList<>();
for (Integer n : List.of(1, 2, 3, 4, 5)) {
    if (n > 2) filtered.add(n);
}
List<Integer> mapped = new ArrayList<>();
for (Integer n : filtered) {
    mapped.add(n * 2);
}
```

```markdown
// 지연 연산: 요소 하나씩 파이프라인 통과 (중간 컬렉션 없음)
List.of(1, 2, 3, 4, 5).stream()
    .filter(n -> n > 2)
    .map(n -> n * 2)
    .toList();

// 실행 흐름: 1(filter) → 2(filter) → 3(filter→map) → 4(filter→map) → 5(filter→map)
```

---

### Stream의 지연 연산

Stream에서 **중간 연산**은 지연 연산, **최종 연산**은 즉시 연산이다.

| 구분 | 종류 | 실행 시점 |
|------|------|-----------|
| 중간 연산 | `filter`, `map`, `flatMap`, `sorted`, `distinct`, `peek` 등 | 최종 연산 호출 전까지 실행 안 함 |
| 최종 연산 | `forEach`, `collect`, `toList`, `count`, `findFirst` 등 | 호출 즉시 파이프라인 실행 |

```markdown
Stream<Integer> stream = List.of(1, 2, 3).stream()
    .filter(n -> {
        System.out.println("filter: " + n);  // 최종 연산 전까지 출력 안 됨
        return n > 1;
    });

System.out.println("최종 연산 전");
stream.toList();  // 여기서 filter가 실행됨
System.out.println("최종 연산 후");

// 출력:
// 최종 연산 전
// filter: 1
// filter: 2
// filter: 3
// 최종 연산 후
```

---

### 단축 평가 (Short-Circuiting)

지연 연산의 핵심 최적화. 결과가 확정되는 순간 나머지 요소를 처리하지 않고 종료한다.

```markdown
// anyMatch: 조건 맞는 요소 발견 즉시 종료
boolean result = Stream.of(1, 2, 3, 4, 5)
    .filter(n -> {
        System.out.println("check: " + n);
        return n % 2 == 0;
    })
    .anyMatch(n -> true);
// check: 1 → check: 2 → 종료 (나머지 3, 4, 5는 처리 안 함)

// findFirst: 조건 맞는 첫 요소 찾으면 종료
Optional<Integer> first = Stream.of(1, 2, 3, 4, 5)
    .filter(n -> n > 3)
    .findFirst();  // 4 발견 → 5는 처리 안 함

// limit: 무한 스트림을 유한하게 만들 때 필수
List<Integer> evens = Stream.iterate(0, n -> n + 1)
    .filter(n -> n % 2 == 0)
    .limit(5)    // 짝수 5개 찾으면 무한 스트림 중단
    .toList();   // [0, 2, 4, 6, 8]
```

단축 평가 적용 연산: `anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `findAny`, `limit`

---

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 스트림 재사용 | 최종 연산 후 스트림은 소비됨 — 다시 사용하면 `IllegalStateException` | 필요하면 새 스트림 생성 |
| 사이드 이펙트 | 중간 연산에서 외부 상태 변경 시 실행 순서 예측 어려움 | 중간 연산은 순수 함수로 작성 |
| 디버깅 | 실행 시점이 분리되어 있어 흐름 파악이 어려움 | `peek()`으로 중간값 확인 |
| 무한 스트림 | `limit` 없이 최종 연산 호출 시 무한 루프 | 무한 스트림엔 반드시 `limit` 또는 단축 평가 연산 사용 |

```markdown
// peek으로 디버깅
List.of(1, 2, 3, 4, 5).stream()
    .filter(n -> n > 2)
    .peek(n -> System.out.println("filter 통과: " + n))
    .map(n -> n * 2)
    .peek(n -> System.out.println("map 결과: " + n))
    .toList();
```
