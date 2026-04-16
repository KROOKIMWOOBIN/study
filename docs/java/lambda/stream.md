## Stream (스트림)

### 왜 쓰는지

기존 반복문은 **개발자가 직접 반복을 제어**해야 하므로:
- 코드가 길고 복잡함
- 병렬 처리가 어려움
- 요소 처리 순서 관리가 번거로움

<div class="concept-box" markdown="1">

**핵심**: Stream은 컬렉션의 요소를 **함수형 스타일로 처리**하는 라이브러리 제공 파이프라인입니다.

</div>

### 어떻게 쓰는지

#### 기본 파이프라인

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Stream 파이프라인: 소스 → 중간연산 → 최종연산
names.stream()              // 1. 소스 (Stream 생성)
    .filter(name -> name.length() > 3)  // 2. 중간연산
    .map(String::toUpperCase)           // 3. 중간연산
    .forEach(System.out::println);      // 4. 최종연산
```

#### 구성 요소

| 구성 | 역할 | 예시 | 반환 |
|------|------|------|------|
| **소스** | Stream 생성 | `list.stream()`, `Stream.of()` | Stream |
| **중간연산** | 요소 변환/필터링 (lazy) | `filter()`, `map()`, `distinct()` | Stream |
| **최종연산** | 결과 도출 (eager) | `forEach()`, `collect()`, `reduce()` | 단일값/컬렉션 |

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **컬렉션 필터링 & 변환** | ✅ Stream | 함수형 스타일로 간결함 |
| **대용량 데이터 처리** | ✅ Stream | lazy evaluation으로 불필요한 연산 생략 |
| **병렬 처리 필요** | ✅ `parallelStream()` | 병렬화가 자동으로 처리됨 |
| **간단한 반복** | ❌ for-each | for-each가 더 읽기 쉬움 |
| **중간 결과 필요** | ❌ 전통 반복문 | 반복문에서는 중간 변수 할당 가능 |

### 장점

| 장점 | 설명 |
|------|------|
| **간결한 코드** | 함수형 스타일로 선언적 작성 |
| **게으른 평가 (Lazy Evaluation)** | 중간연산이 실행되지 않고, 최종연산 시점에만 수행 |
| **병렬 처리 용이** | `parallelStream()`으로 멀티스레드 자동 처리 |
| **함수 조합** | 메서드 체이닝으로 복잡한 로직을 단계별로 표현 |
| **불변성** | 원본 컬렉션을 변경하지 않음 |

### 단점

| 단점 | 설명 |
|------|------|
| **성능 오버헤드** | 함수형 객체 생성, 메서드 호출 비용 발생 |
| **단순한 루프보다 느림** | 큰 데이터셋에서 for-each가 더 빠를 수 있음 |
| **스택 트레이스 복잡** | 디버깅 시 콜 스택이 깊어져 읽기 어려움 |
| **중간 결과 접근 불가** | 각 단계 중간값을 쉽게 볼 수 없음 |
| **일회용** | Stream은 한 번 소비되면 재사용 불가 |

### 특징

#### 1. 내부 반복 vs 외부 반복

```java
// 외부 반복: 개발자가 직접 제어
for (String name : list) {
    System.out.println(name.toUpperCase());
}

// 내부 반복: 라이브러리가 제어
list.stream()
    .forEach(name -> System.out.println(name.toUpperCase()));
```

| 특징 | 외부 반복 | 내부 반복 (Stream) |
|------|---------|-------------------|
| 제어자 | 개발자 | 라이브러리 |
| 병렬화 | 수동 (복잡함) | 자동 |
| 가독성 | 절차형 | 선언형 |
| 성능 | 단순 루프 빠름 | 오버헤드 있음 |

#### 2. Lazy Evaluation (게으른 평가)

```java
list.stream()
    .filter(x -> {
        System.out.println("filter: " + x);  // 아직 실행 안됨
        return x > 3;
    })
    .map(x -> {
        System.out.println("map: " + x);  // 아직 실행 안됨
        return x * 2;
    })
    // forEach 호출 시점에야 위 두 연산이 실행됨
    .forEach(System.out::println);
```

**결과:**
```text
filter: 1
filter: 2
filter: 3
filter: 4
filter: 5
map: 4
```

최종연산(`forEach`) 호출 시점에 중간연산들이 한 번에 실행됨 → **불필요한 요소 처리 생략 가능**

#### 3. 중간연산 vs 최종연산

| 구분 | 중간연산 | 최종연산 |
|------|---------|---------|
| 반환 | Stream | 단일값/void |
| 실행 시점 | 최종연산까지 지연 | 호출 시 즉시 실행 |
| 체이닝 | 가능 | 불가능 (종료) |
| 예시 | `filter`, `map`, `distinct` | `forEach`, `collect`, `reduce` |

### 주의할 점

<div class="danger-box" markdown="1">

**❌ Stream 재사용 불가 (일회용)**
```java
Stream<Integer> stream = list.stream();
stream.forEach(System.out::println);
stream.forEach(System.out::println);  // ❌ IllegalStateException
```

**✅ 올바른 방식:**
```java
list.stream().forEach(System.out::println);
list.stream().forEach(System.out::println);  // 새로운 Stream 생성
```

</div>

<div class="danger-box" markdown="1">

**❌ 중간 결과에 접근 불가**
```java
list.stream()
    .filter(x -> x > 3)  // 여기까지의 결과를 보고 싶은 경우?
    .forEach(System.out::println);
```

**✅ 필요하면 별도 변수에 저장:**
```java
List<Integer> filtered = list.stream()
    .filter(x -> x > 3)
    .collect(Collectors.toList());
System.out.println(filtered);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 성능**: 대규모 단순 연산(밀리초 단위)에서는 전통 for-each가 더 빠를 수 있음

```java
// 매우 많은 요소, 단순 처리 → for-each가 더 빠름
for (int i = 0; i < 10_000_000; i++) { ... }

// 복잡한 변환/필터링 → Stream이 가독성 우수
list.stream()
    .filter(...)
    .map(...)
    .filter(...)
    .collect(Collectors.groupingBy(...));
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 병렬 스트림 주의** (`parallelStream()`)
- 데이터가 작으면 병렬 오버헤드가 더 클 수 있음
- 상태 공유 변수 사용 시 동기화 필요
- I/O 대기가 없는 CPU 집약적 작업에 적합

</div>

### 정리

<div class="concept-box" markdown="1">

**Stream 사용 기준:**
- ✅ 컬렉션 필터링/변환/집계
- ✅ 복잡한 데이터 처리 로직
- ✅ 병렬 처리 필요 시
- ❌ 성능이 매우 중요한 단순 루프
- ❌ 중간 결과에 자주 접근해야 하는 경우

</div>

구체적인 API 사용법은 [streamApi.md](streamApi.md)를 참고하세요.
