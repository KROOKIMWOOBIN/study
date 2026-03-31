## Stream Api

### 일괄 처리 방식 VS 파이프라인 방식
| 구분 | 일괄 처리 방식 (Batch) | 파이프라인 방식 (Stream) |
|------|----------------------|--------------------------|
| 처리 방식 | 전체 데이터를 한 번에 처리 | 데이터를 흐름처럼 단계별 처리 |
| 실행 시점 | 즉시 실행 (Eager) | 지연 실행 (Lazy) |
| 데이터 처리 단위 | 전체 컬렉션 단위 | 요소 단위 (Element-by-element) |
| 중간 결과 | 매 단계마다 컬렉션 생성 | 중간 결과 저장 없음 |
| 메모리 사용 | 높음 (중간 객체 다수) | 낮음 |
| 성능 | 비효율 가능 (불필요 연산) | 최적화 가능 (필요한 만큼만 실행) |
| 코드 스타일 | 명령형 (Imperative) | 선언형 (Declarative) |
| 병렬 처리 | 직접 구현 필요 | parallelStream()으로 간단히 처리 |
| 가독성 | 상대적으로 낮음 | 높음 |
| 대표 예시 | for-loop 기반 처리 | stream().filter().map() |

### 즉시 연산 VS 지연 연산
| 구분 | 즉시 연산 (Eager Evaluation) | 지연 연산 (Lazy Evaluation) |
|------|----------------------------|-----------------------------|
| 실행 시점 | 연산 정의 즉시 실행 | 최종 연산 시점까지 지연 |
| 처리 방식 | 전체 데이터를 먼저 처리 | 요소 단위로 순차 처리 |
| 연산 흐름 | 단계별로 전체 반복 | 한 요소씩 파이프라인 통과 |
| 중간 결과 | 매 단계마다 생성 | 생성되지 않음 |
| 메모리 사용 | 높음 | 낮음 |
| 성능 | 불필요한 연산 포함 가능 | 필요한 만큼만 수행 |
| 최적화 | 어려움 | Short-Circuiting 가능 |
| 코드 스타일 | 명령형 (Imperative) | 선언형 (Declarative) |
| 대표 예시 | for-loop, 컬렉션 조작 | Stream API (filter, map 등) |
| 실행 트리거 | 없음 (즉시 실행) | 최종 연산 (forEach, collect 등) |

#### 즉시 연산 예시 (Eager)
```markdown
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

List<Integer> filtered = new ArrayList<>();
for (Integer n : numbers) {
    System.out.println("filter: " + n);
    if (n > 2) filtered.add(n);
}

List<Integer> mapped = new ArrayList<>();
for (Integer n : filtered) {
    System.out.println("map: " + n);
    mapped.add(n * 2);
}

System.out.println(mapped);

// 실행 흐름: filter 전체 → map 전체
// filter: 1, 2, 3, 4, 5 → map: 3, 4, 5
```

#### 지연 연산 예시 (Lazy - Stream)
```markdown
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

numbers.stream()
       .filter(n -> {
           System.out.println("filter: " + n);
           return n > 2;
       })
       .map(n -> {
           System.out.println("map: " + n);
           return n * 2;
       })
       .forEach(System.out::println);

// 실행 흐름: 요소 하나씩 파이프라인 전체 통과
// filter:1 → filter:2 → filter:3 → map:3 → 6 → filter:4 → map:4 → 8 ...
```

---

### 단축 평가 (Short-Circuiting)

결과가 이미 확정되면 이후 요소를 처리하지 않고 즉시 종료한다.

```markdown
// anyMatch: true가 나오는 순간 나머지 요소 검사 안 함
boolean hasEven = Stream.of(1, 2, 3, 4, 5)
    .filter(n -> {
        System.out.println("check: " + n);
        return n % 2 == 0;
    })
    .anyMatch(n -> true);
// check: 1 → check: 2 → 종료 (2에서 짝수 발견)

// findFirst: 조건 맞는 첫 번째 요소 찾으면 즉시 종료
Optional<Integer> first = Stream.of(1, 2, 3, 4, 5)
    .filter(n -> n > 3)
    .findFirst();  // 4를 찾는 순간 5는 처리 안 함

// limit: n개 채우면 생성 중단 (무한 스트림과 조합)
List<Integer> result = Stream.iterate(0, n -> n + 1)
    .filter(n -> n % 2 == 0)
    .limit(5)           // 짝수 5개 찾으면 무한 스트림 중단
    .toList();          // [0, 2, 4, 6, 8]
```

단축 평가가 적용되는 연산: `anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `findAny`, `limit`

---

### 스트림 생성 단계
| 분류       | 메서드                           | 설명                | 특징        | 예시                       |
| -------- | ----------------------------- | ----------------- | --------- | ------------------------ |
| 컬렉션 기반   | `collection.stream()`         | 컬렉션 → 스트림         | 가장 기본     | `list.stream()`          |
| 배열 기반    | `Arrays.stream()`             | 배열 → 스트림          | 범위 지정 가능  | `Arrays.stream(arr)`     |
| 값 직접 생성  | `Stream.of()`                 | 값 나열 → 스트림        | 간단한 데이터   | `Stream.of(1,2,3)`       |
| 빈 스트림    | `Stream.empty()`              | 빈 스트림 생성          | 안전 처리     | `Stream.empty()`         |
| 무한 스트림   | `Stream.iterate()`            | 이전 값 기반 생성        | 상태 있음     | `iterate(0, n->n+1)`     |
| 무한 스트림   | `Stream.generate()`           | Supplier 기반 생성    | 상태 없음     | `generate(Math::random)` |
| 범위 스트림   | `IntStream.range()`           | 정수 범위             | 성능 최적화    | `range(1,10)`            |
| 범위 스트림   | `IntStream.rangeClosed()`     | 포함 범위             | 끝값 포함     | `rangeClosed(1,10)`      |
| 파일       | `Files.lines()`               | 파일 → 스트림          | lazy read | 파일 처리                    |
| 랜덤       | `Random.ints()`               | 랜덤 스트림            | 무한 가능     | 난수 처리                    |
| 빌더       | `Stream.builder()`            | 수동 추가             | 유연성 높음    | `builder.add()`          |
| Optional | `Optional.stream()`           | Optional → Stream | Java 9+   | null-safe                |
| 병렬 스트림   | `collection.parallelStream()` | 병렬 처리             | 멀티코어 활용   | 병렬 연산                    |

---

### 중간 연산

| 메서드                      | 설명                    |
| ------------------------ | --------------------- |
| `filter(Predicate)`      | 조건에 맞는 요소만 추출         |
| `map(Function)`          | 요소 변환                 |
| `flatMap(Function)`      | 1:N 구조 평탄화            |
| `mapToInt/Long/Double`   | 기본형 특화 스트림으로 변환       |
| `distinct()`             | 중복 제거                 |
| `sorted()`               | 정렬                    |
| `limit(n)`               | 앞에서 n개                |
| `skip(n)`                | n개 건너뜀                |
| `peek(Consumer)`         | 중간 확인 (디버깅용)          |

#### flatMap — 중첩 구조 평탄화

`map`은 요소 → 요소 (1:1), `flatMap`은 요소 → 스트림 (1:N) 변환 후 하나의 스트림으로 합친다.

```markdown
// map: List<List<Integer>> → Stream<List<Integer>> (여전히 중첩)
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));
nested.stream()
    .map(List::stream)          // Stream<Stream<Integer>> — 원하는 게 아님
    .forEach(System.out::println);

// flatMap: List<List<Integer>> → Stream<Integer> (평탄화)
nested.stream()
    .flatMap(List::stream)      // Stream<Integer>
    .forEach(System.out::println); // 1 2 3 4 5

// 실전 예시: 주문 목록에서 모든 상품명 추출
List<Order> orders = ...;
List<String> allItems = orders.stream()
    .flatMap(order -> order.getItems().stream())  // Order → Stream<Item>
    .map(Item::getName)
    .toList();
```

---

### 최종 연산

| 메서드                  | 반환 타입        | 설명            |
| -------------------- | ------------ | ------------- |
| `forEach(Consumer)`  | void         | 요소 반복         |
| `collect(Collector)` | R            | 컬렉션/결과로 수집    |
| `toList()`           | List<T>      | 리스트로 수집 (Java 16+) |
| `toArray()`          | Object[]     | 배열 변환         |
| `reduce()`           | Optional / T | 누적 연산         |
| `count()`            | long         | 개수            |
| `min(Comparator)`    | Optional<T>  | 최솟값           |
| `max(Comparator)`    | Optional<T>  | 최댓값           |
| `sum()` / `average()`| int / OptionalDouble | 특화 스트림 전용 |
| `findFirst()`        | Optional<T>  | 첫 요소 (순서 보장)  |
| `findAny()`          | Optional<T>  | 아무 요소 (병렬 최적) |
| `anyMatch()`         | boolean      | 하나라도 만족       |
| `allMatch()`         | boolean      | 모두 만족         |
| `noneMatch()`        | boolean      | 모두 불만족        |

> `findFirst` vs `findAny`: 순차 스트림에서는 동일하지만, 병렬 스트림에서는 `findAny`가 더 빠르다 (순서 보장 불필요).

#### reduce — 누적 연산

```markdown
// identity(초기값) + accumulator
int sum = Stream.of(1, 2, 3, 4, 5)
    .reduce(0, Integer::sum);  // 0 + 1 + 2 + 3 + 4 + 5 = 15

// identity 없으면 Optional 반환 (빈 스트림 대비)
Optional<Integer> product = Stream.of(1, 2, 3, 4)
    .reduce((a, b) -> a * b);  // 24

// 동작 원리: (((1+2)+3)+4)+5
// accumulator(accumulator(accumulator(identity, e1), e2), e3) ...
```

---

### 특화 연산 (Primitive Streams)

`Stream<Integer>` 대신 `IntStream`, `LongStream`, `DoubleStream`을 사용하면 **박싱/언박싱 비용이 없어** 성능이 좋다.

| 특화 스트림      | 생성 방법                        | 전용 최종 연산                          |
| ----------- | ---------------------------- | ---------------------------------- |
| `IntStream` | `mapToInt()`, `IntStream.range()` | `sum()`, `average()`, `min()`, `max()`, `summaryStatistics()` |
| `LongStream` | `mapToLong()`, `LongStream.range()` | 동일 |
| `DoubleStream` | `mapToDouble()` | 동일 |

```markdown
// Stream<Integer> → IntStream 변환 (언박싱)
int total = List.of(1, 2, 3, 4, 5).stream()
    .mapToInt(Integer::intValue)  // IntStream
    .sum();                       // 15

// 범위 생성
IntStream.range(1, 6)         // 1, 2, 3, 4, 5 (끝 미포함)
IntStream.rangeClosed(1, 5)  // 1, 2, 3, 4, 5 (끝 포함)

// 통계 한 번에
IntSummaryStatistics stats = IntStream.of(1, 2, 3, 4, 5)
    .summaryStatistics();
stats.getSum();     // 15
stats.getAverage(); // 3.0
stats.getMin();     // 1
stats.getMax();     // 5
stats.getCount();   // 5

// IntStream → Stream<T> 역변환 (박싱)
Stream<Integer> boxed = IntStream.range(1, 6).boxed();
Stream<String>  obj   = IntStream.range(1, 6).mapToObj(n -> "item" + n);
```

---

### Collectors — collect()의 핵심

#### 왜 쓰는가?

Stream 중간 연산 결과를 실제 자료구조나 단일 값으로 변환할 때 사용한다.
단순한 `toList()`만으로는 부족한 **그룹화, 파티셔닝, 집계, 문자열 합치기** 등 복잡한 변환을 선언적으로 표현할 수 있다.

```markdown
// Collector 없이 부서별 그룹화 — 명령형
Map<String, List<Employee>> result = new HashMap<>();
for (Employee e : employees) {
    result.computeIfAbsent(e.getDepartment(), k -> new ArrayList<>()).add(e);
}

// Collector 사용 — 선언형
Map<String, List<Employee>> result = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));
```

#### 특징

- `Collector`는 **전략 객체** — `collect()`에 "어떻게 수집할지"를 위임한다
- `Collectors`는 자주 쓰는 구현체를 제공하는 **팩토리 클래스**
- downstream collector 조합으로 **중첩 집계** 표현 가능
- `Collector.of()`로 직접 커스텀 Collector 구현도 가능

---

#### 기본 수집

| 방법 | 결과 | 특징 |
|---|---|---|
| `Collectors.toList()` | 가변 List | 추가/삭제 가능 |
| `stream.toList()` | 불변 List | Java 16+, 수정 시 UnsupportedOperationException |
| `Collectors.toUnmodifiableList()` | 불변 List | Java 10+ |
| `Collectors.toSet()` | 가변 Set | 순서 미보장, 중복 제거 |

```markdown
List<String> mutable   = stream.collect(Collectors.toList());  // 가변
List<String> immutable = stream.toList();                      // 불변 (Java 16+)
Set<String>  set       = stream.collect(Collectors.toSet());
```

> **주의:** `toList()`(불변)와 `Collectors.toList()`(가변)는 다르다. 반환 결과에 `.add()`를 호출할 계획이면 반드시 `Collectors.toList()`를 사용할 것.

---

#### toMap — 스트림을 Map으로 변환

**언제 쓰는가:** 요소를 키-값 쌍으로 변환해 Map이 필요할 때

```markdown
// key: 이름, value: 나이
Map<String, Integer> map = people.stream()
    .collect(Collectors.toMap(
        Person::getName,   // keyMapper
        Person::getAge     // valueMapper
    ));

// 중복 키 충돌 처리 (mergeFunction) + Map 구현체 지정
Map<String, Integer> map = people.stream()
    .collect(Collectors.toMap(
        Person::getName,
        Person::getAge,
        (existing, incoming) -> existing,  // 충돌 시 기존 값 유지
        LinkedHashMap::new                 // 삽입 순서 유지
    ));
```

> **주의:** `mergeFunction`을 생략하면 키 중복 시 `IllegalStateException` 발생. 실무에서는 항상 세 번째 인자를 명시하는 것이 안전하다.

---

#### groupingBy — 그룹화

**언제 쓰는가:** 특정 기준으로 요소를 분류해 `Map<K, List<V>>` 구조가 필요할 때

```markdown
// 부서별 직원 목록: Map<String, List<Employee>>
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// downstream collector 조합: 그룹별 인원 수
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.counting()          // downstream — 각 그룹에 적용할 추가 집계
    ));

// 그룹별 이름만 추출
Map<String, List<String>> namesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));
```

> **주의:** `groupingBy` 결과 Map은 순서를 보장하지 않는다. 삽입 순서가 필요하면 `groupingBy(classifier, LinkedHashMap::new, downstream)` 형태로 Map 구현체를 지정한다.

---

#### partitioningBy — 조건으로 두 그룹 분리

**언제 쓰는가:** `true` / `false` 두 그룹으로만 나눌 때. `groupingBy`보다 의도가 명확하다.

```markdown
// 짝수 / 홀수 분리: Map<Boolean, List<Integer>>
Map<Boolean, List<Integer>> partition = Stream.of(1, 2, 3, 4, 5)
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// {false=[1, 3, 5], true=[2, 4]}

// downstream 조합: 합격/불합격 인원 수
Map<Boolean, Long> passCount = students.stream()
    .collect(Collectors.partitioningBy(
        s -> s.getScore() >= 60,
        Collectors.counting()
    ));
```

---

#### joining — 문자열 합치기

**언제 쓰는가:** 스트림 요소들을 하나의 문자열로 이어붙일 때. `StringBuilder` 반복보다 간결하다.

```markdown
String result = Stream.of("Java", "Stream", "API")
    .collect(Collectors.joining(", ", "[", "]"));
// "[Java, Stream, API]"
//                  ↑ delimiter  ↑ prefix  ↑ suffix

// 단순 연결
String csv = Stream.of("a", "b", "c")
    .collect(Collectors.joining(","));  // "a,b,c"
```

---

#### 통계 Collectors

**언제 쓰는가:** 그룹별 합계, 평균, 최댓값 등 집계가 필요할 때. `IntStream.summaryStatistics()`의 Collector 버전.

```markdown
// 나이 통계 한 번에
IntSummaryStatistics stats = people.stream()
    .collect(Collectors.summarizingInt(Person::getAge));
// stats.getSum(), getAverage(), getMin(), getMax(), getCount()

// 개수
long count = people.stream().collect(Collectors.counting());

// 최댓값 (Optional 반환 — 빈 스트림 대비)
Optional<Person> oldest = people.stream()
    .collect(Collectors.maxBy(Comparator.comparing(Person::getAge)));
```

---

#### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|---|---|---|
| `toMap` 키 중복 | `IllegalStateException` | mergeFunction 명시 |
| `groupingBy` 순서 | 결과 Map 순서 미보장 | `LinkedHashMap::new` 지정 |
| `toList()` vs `Collectors.toList()` | 불변/가변 혼동 | 수정 필요 시 `Collectors.toList()` 사용 |
| downstream 중첩 | 가독성 저하 | 2단계 이상이면 메서드 분리 고려 |

