> [← 홈](/README.md) · [Java](/docs/java/java.md)

# 람다, 스트림, 함수형 프로그래밍

함수형 프로그래밍 개념과 Java의 람다·스트림 API 정리.

---

| 주제 | 한 줄 설명 |
| --- | --- |
| [매개변수화](#매개변수화) | 값 매개변수화 vs 동작 매개변수화 |
| [람다식](#람다) | 익명 함수 문법, 축약 규칙 |
| [고차 함수](#고차-함수-higher-order-function) | 함수를 인자로 받거나 반환하는 함수 |
| [함수형 인터페이스](#람다와-타겟-타입) | Function, Consumer, Supplier, Predicate |
| [선언형 프로그래밍](#명령어-프로그래밍-vs-선언적-프로그래밍) | 명령형 vs 선언형 비교 |
| [메서드 참조](#메서드-참조) | `::` 연산자, 정적/인스턴스/생성자 참조 |
| [Stream API](#stream-api) | 파이프라인, 중간 연산, 최종 연산 |

---

## 매개변수화
- 프로그램의 유연성·재사용성을 높이기 위한 설계 기법이다.
- 핵심은 고정된 로직을 만들지 않고 외부에서 값을 전달받도록 만드는 것이다.

### 값 매개변수화
- 값을 바꿔서 동일한 로직을 재사용하는 방식
```markdown
int add(int a, int b) {
    return a + b;
}
add(1, 2);
```
### 동작 매개변수화
- 동작(로직)을 외부에서 전달하는 방식
```markdown
void execute(Runnable r) {
    r.run();
}
execute(() -> System.out.println("hello"));
```

## 함수 VS 메서드
| 구분 | 함수     | 메서드        |
| -- | ------ | ---------- |
| 소속 | 독립     | 클래스/객체     |
| 언어 | 함수형 언어 | Java 등 OOP |

### 함수
- 객체나 클래스에 속하지 않고 독립적으로 존재할 수 있다.
- 주로 절차지향 언어 또는 함수형 언어에서 사용되는 개념

### 메서드
- 객체(또는 클래스)에 속해 있는 함수
- 주로 객체지향 언어에서 사용되는 개념

## 람다
- 익명 함수를 지칭하는 일반적인 용어, 즉 개념이다.

### 람다식
- (매개변수) -> { 본문 } 형태로 람다를 구현하는 구체적인 문법을 지칭한다.

#### 람다식 축약 규칙
- 타입 생략 가능
  - int a, int b) -> a + b
  - (a, b) -> a + b
- 한 줄이면 {} 생략 가능
- 매개변수 1개면 괄호 생략 가능
  - x -> x * 2

## 고차 함수 (Higher-Order Function)
- 함수를 값처럼 다루는 함수를 뜻한다.
- Java`는 함수 자체를 직접 전달할 수 없기 때문에 -> 함수형 인터페이스를 사용

### 일반적으로 아래 케이스를 고차 함수라 뜻한다.

#### 함수를 인자로 받는 메서드
```markdown
void run(Runnable r) {
    r.run();
}
```

#### 함수를 반환하는 메서드
```markdown
Function<Integer, Integer> multiplier(int x) {
    return y -> x * y;
}
```

## 람다와 타겟 타입
- 람다가 할당될 함수형 인터페이스 타입
- 왜 필요한가? 인터페이스 구현체로 컴파일되기 때문
```markdown
[Java]에서 람다는 항상 타겟 타입이 필요하다.
예) Runnable r = () -> System.out.println("hello"); 
여기서 Runnable <- 이 타겟 타입
```

- 기본 함수형 인터페이스

| 인터페이스           | 형태          | 매개변수 | 반환값     | 용도        |
| --------------- | ----------- | ---- | ------- | --------- |
| `Function<T,R>` | T → R       | O    | O       | 입력을 받아 변환 |
| `Consumer<T>`   | T → void    | O    | X       | 값 소비      |
| `Supplier<T>`   | () → T      | X    | O       | 값 생성      |
| `Predicate<T>`  | T → boolean | O    | boolean | 조건 검사     |


- 특화 함수형 인터페이스 

| 인터페이스               | 실제 의미                 |
| ------------------- | --------------------- |
| `UnaryOperator<T>`  | Function<T,T>         |
| `BinaryOperator<T>` | BiFunction<T,T,T>     |
| `BiFunction<T,U,R>` | Function 확장           |
| `IntFunction<R>`    | Function primitive 특화 |
| `ToIntFunction<T>`  | Function primitive 반환 |

- 기타 함수형 인터페이스

| 인터페이스            | 형태             | 설명            |
| ---------------- | -------------- | ------------- |
| `Comparator<T>`  | (T,T) → int    | 객체 비교 (정렬 기준) |
| `Runnable`       | () → void      | 스레드 실행        |
| `Callable<V>`    | () → V         | 결과 반환 스레드     |
| `ActionListener` | (Event) → void | GUI 이벤트 처리    |

## 명령어 프로그래밍 VS 선언적 프로그래밍

### 명령형 (Imperative)
- 어떻게(how) 할지를 직접 기술
- 상태 변화, 반복문, 인덱스 중심
```markdown
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.length() > 3) {
        result.add(s.toUpperCase());
    }
}
```

#### 특징
1. 제어 흐름 직접 관리
2. 가변 상태 많음
3. 코드 길고 버그 가능성↑

### 선언형 (Declarative)
- 무엇(what) 을 원하는지만 표현
- 내부 동작은 라이브러리가 처리
```markdown
list.stream()
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase)
    .toList();
```

#### 특징
1. 로직 의도가 명확
2. 내부 구현 숨김
3. 병렬 처리 최적화 쉬움

## Filter, Map
| 구분     | 역할            |
| ------ | ------------- |
| filter | 걸러냄 (boolean) |
| map    | 변환 (T → R)    |

### Filter
- 조건 기반 필터링
- Predicate<T> 사용
```markdown
.filter(s -> s.length() > 3)
```

### Map
- 데이터 변환
- Function<T, R> 사용
```markdown
.map(String::toUpperCase)
```

## 스트림
- 데이터 흐름 파이프라인
- 컬렉션을 함수형 스타일로 처리
```markdown
list.stream()
[데이터] → 중간연산 → 최종연산
```
| 단계               | 설명    |
| ---------------- | ----- |
| stream()         | 시작    |
| filter/map       | 중간 연산 |
| toList()/collect | 최종 연산 |

## Static Factory Method
- 생성자를 대신하는 정적 메서드

### 왜 사용하는가?
- 이름으로 의미 표현 가능
```markdown
public class Grade {

    private final String name;

    private Grade(String name) {
        this.name = name;
    }

    public static Grade gold() {
        return new Grade("GOLD");
    }
    
    public static Grade silver() {
        return new Grade("SILVER");
    }

}
```

## 내부 반복 VS 외부 반복
| 구분    | 외부 반복 | 내부 반복 |
| ----- | ----- | ----- |
| 제어    | 개발자   | 라이브러리 |
| 병렬 처리 | 어려움   | 쉬움    |
| 코드    | 장황    | 간결    |

### 내부 반복
- 개발자가 직접 반복 제어
```markdown
list.stream().forEach(...)
```

### 외부 반복
- 라이브러리가 반복 수행
```markdown
for (String s : list)
```

## 메서드 참조
- 람다식에서 이미 존재하는 메서드를 그대로 전달
- :: 연산자를 사용
- 함수형 인터페이스와 결합됨
```markdown
list.forEach(System.out::println);
// (x) -> System.out.println(x)
```

### 정적 메서드 참조 (Static Method Reference)

#### 개념
- 클래스에 정의된 static 메서드를 직접 참조
- 인스턴스 생성 없이 호출

#### 문법
```markdown
ClassName::staticMethod
```

#### 예제 
```markdown
public class Main {
    public static void main(String[] args) {
        BinaryOperator<Integer> max = Math::max;

        int result = max.apply(10, 20);
        System.out.println(result); // 20
    }
}
```

### 인스턴스 메서드 참조 (특정 객체)

#### 개념
- 이미 생성된 객체의 메서드를 참조
- 고정된 객체 기준으로 동작

#### 문법
```markdown
instance::method
```

#### 예제
```markdown
public class Main {
    public static void main(String[] args) {
        Printer printer = new Printer();

        Consumer<String> consumer = printer::print;
        consumer.accept("Hello");
    }
}

class Printer {
    public void print(String message) {
        System.out.println(message);
    }
}
```

### 인스턴스 메서드 참조 (임의 객체)

#### 개념
- 특정 객체가 아니라 파라미터로 전달된 객체를 기준으로 호출
- 첫 번째 인자가 "this" 역할을 함

#### 문법
```markdown
ClassName::method
```

#### 예시
```markdown
public class Main {
    public static void main(String[] args) {
        Function<String, Integer> lengthFunc = String::length;
        int len = lengthFunc.apply("hello");
        System.out.println(len); // 5
    }
}
```

### 생성자 참조 (Constructor Reference)

#### 개념
- 생성자를 함수처럼 참조
- 객체 생성 로직을 람다로 대체

#### 문법
```markdown
ClassName::new
```

#### 예시
```markdown
public class Main {
    public static void main(String[] args) {
        Supplier<ArrayList<String>> supplier = ArrayList::new;
        ArrayList<String> list = supplier.get();
        list.add("A");
        System.out.println(list);
    }
}
```

### 특징
- 람다의 축약형
```markdown
(x) -> x.toLowerCase()
String::toLowerCase
```
- 함수형 인터페이스 필수
```markdown
Function<String, Integer> f = String::length;
```
- 타입 추론 의존
  - 컴파일러가 문맥으로 매칭
```markdown
Function<String, Integer> f = String::length;
```
- 가독성 중심 기능
  - 로직이 아닌 “전달”에 초점

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

### 메서드 종류
| 메서드           | 반환 타입       | 설명                | 종료 조건        |
| ------------- | ----------- | ----------------- | ------------ |
| `anyMatch()`  | boolean     | 하나라도 조건 만족        | true 나오면 종료  |
| `allMatch()`  | boolean     | 모두 조건 만족          | false 나오면 종료 |
| `noneMatch()` | boolean     | 모두 조건 불만족         | true 나오면 종료  |
| `findFirst()` | Optional<T> | 첫 번째 요소 반환        | 하나 찾으면 종료    |
| `findAny()`   | Optional<T> | 아무 요소 반환 (병렬 최적화) | 하나 찾으면 종료    |
| `limit()`     | Stream<T>   | n개까지만 처리          | n개 처리 시 종료   |
