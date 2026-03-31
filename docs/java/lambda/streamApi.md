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

`collect(Collectors.xxx())`로 다양한 결과물을 만든다.

#### 기본 수집

```markdown
List<String> names = stream.collect(Collectors.toList());    // 변경 가능한 List
List<String> names = stream.toList();                        // 불변 List (Java 16+)
Set<String>  names = stream.collect(Collectors.toSet());
```

#### toMap

```markdown
// key: 이름, value: 나이
Map<String, Integer> map = people.stream()
    .collect(Collectors.toMap(
        Person::getName,   // keyMapper
        Person::getAge     // valueMapper
    ));

// 중복 키 충돌 처리 (mergeFunction)
Map<String, Integer> map = people.stream()
    .collect(Collectors.toMap(
        Person::getName,
        Person::getAge,
        (existing, incoming) -> existing,  // 충돌 시 기존 값 유지
        
    ));
```

#### groupingBy — 그룹화

```markdown
// 나이대별 그룹화: Map<Integer, List<Person>>
Map<Integer, List<Person>> byAge = people.stream()
    .collect(Collectors.groupingBy(Person::getAge));

// downstream collector 조합: 그룹별 인원 수
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.counting()          // downstream
    ));

// 그룹별 이름 목록
Map<String, List<String>> namesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));
```

#### partitioningBy — 조건으로 두 그룹 분리

```markdown
// true / false 두 그룹으로 분리: Map<Boolean, List<T>>
Map<Boolean, List<Integer>> partition = Stream.of(1, 2, 3, 4, 5)
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// {false=[1, 3, 5], true=[2, 4]}
```

#### joining — 문자열 합치기

```markdown
String result = Stream.of("Java", "Stream", "API")
    .collect(Collectors.joining(", ", "[", "]"));
// "[Java, Stream, API]"
//                  ↑ delimiter  ↑ prefix  ↑ suffix
```

#### 통계 Collectors

```markdown
IntSummaryStatistics stats = people.stream()
    .collect(Collectors.summarizingInt(Person::getAge));

long count   = people.stream().collect(Collectors.counting());
Optional<Person> oldest = people.stream()
    .collect(Collectors.maxBy(Comparator.comparing(Person::getAge)));
```

