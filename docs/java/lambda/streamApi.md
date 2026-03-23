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

#### 📌 즉시 연산 예시 (Eager)
```markdown
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

List<Integer> filtered = new ArrayList<>();
for (Integer n : numbers) {
    System.out.println("filter: " + n);
    if (n > 2) {
        filtered.add(n);
    }
}

List<Integer> mapped = new ArrayList<>();
for (Integer n : filtered) {
    System.out.println("map: " + n);
    mapped.add(n * 2);
}

System.out.println(mapped);

// 실행 흐름
filter: 1
filter: 2
filter: 3
filter: 4
filter: 5
map: 3
map: 4
map: 5
-> filter 전체 → map 전체
```
#### 📌 지연 연산 예시 (Lazy - Stream)
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

// 실행 흐름
filter: 1
filter: 2
filter: 3
map: 3
6
filter: 4
map: 4
8
filter: 5
map: 5
10
-> 요소 하나씩
👉 filter → map → 소비 순으로 즉시 흘러감
```

### 단축 평가란?
```markdown
단축 평가는 연산 또는 스트림 처리 중
결과가 이미 확정되면 이후 연산을 수행하지 않고 즉시 종료하는 방식이다.
```

### 스트림 생성 단계
| 분류       | 메서드                           | 설명                | 특징        | 예시                       |
| -------- | ----------------------------- | ----------------- | --------- | ------------------------ |
| 컬렉션 기반   | `collection.stream()`         | 컬렉션 → 스트림         | 가장 기본     | `list.stream()`          |
| 배열 기반    | `Arrays.stream()`             | 배열 → 스트림          | 범위 지정 가능  | `Arrays.stream(arr)`     |
| 값 직접 생성  | `Stream.of()`                 | 값 나열 → 스트림        | 간단한 데이터   | `Stream.of(1,2,3)`       |
| 빈 스트림    | `Stream.empty()`              | 빈 스트림 생성          | 안전 처리     | `Stream.empty()`         |
| 무한 스트림   | `Stream.iterate()`            | 이전 값 기반 생성        | 상태 있음     | `iterate(0, n→n+1)`      |
| 무한 스트림   | `Stream.generate()`           | Supplier 기반 생성    | 상태 없음     | `generate(Math::random)` |
| 범위 스트림   | `IntStream.range()`           | 정수 범위             | 성능 최적화    | `range(1,10)`            |
| 범위 스트림   | `IntStream.rangeClosed()`     | 포함 범위             | 끝값 포함     | `rangeClosed(1,10)`      |
| 파일       | `Files.lines()`               | 파일 → 스트림          | lazy read | 파일 처리                    |
| 랜덤       | `Random.ints()`               | 랜덤 스트림            | 무한 가능     | 난수 처리                    |
| 빌더       | `Stream.builder()`            | 수동 추가             | 유연성 높음    | builder.add()            |
| Optional | `Optional.stream()`           | Optional → Stream | Java 9+   | null-safe                |
| 병렬 스트림   | `collection.parallelStream()` | 병렬 처리             | 멀티코어 활용   | 병렬 연산                    |

### 중간 연산
| 메서드                 | 설명            |
| ------------------- | ------------- |
| `filter(Predicate)` | 조건에 맞는 요소만 추출 |
| `map(Function)`     | 요소 변환         |
| `flatMap(Function)` | 1:N 구조 평탄화    |
| `distinct()`        | 중복 제거         |
| `sorted()`          | 정렬            |
| `limit(n)`          | 앞에서 n개        |
| `skip(n)`           | n개 건너뜀        |
| `peek(Consumer)`    | 중간 확인 (디버깅용)  |

### 최종 연산
| 메서드                  | 설명      |
| -------------------- | ------- |
| `forEach(Consumer)`  | 요소 반복   |
| `collect(Collector)` | 컬렉션 변환  |
| `toArray()`          | 배열 변환   |
| `reduce()`           | 누적 연산   |
| `count()`            | 개수      |
| `findFirst()`        | 첫 요소    |
| `findAny()`          | 아무 요소   |
| `anyMatch()`         | 하나라도 만족 |
| `allMatch()`         | 모두 만족   |
| `noneMatch()`        | 모두 불만족  |