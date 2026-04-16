## 메서드 참조 (Method Reference)

### 왜 쓰는지

람다식으로 **이미 존재하는 메서드를 다시 작성**하는 것은 중복입니다:
- 같은 메서드를 여러 곳에서 재사용
- 람다로 표현하면 의도가 불명확
- 메서드 이름이 명시되면 가독성 향상

**메서드 참조**는 기존 메서드를 **이름으로 직접 전달**하므로, 람다보다 간결하고 명확합니다.

<div class="concept-box" markdown="1">

**핵심**: 메서드 참조는 **람다식을 더 간결하게 표현한 문법**입니다. `::` 연산자로 메서드를 함수형 인터페이스에 할당할 수 있습니다.

</div>

### 어떻게 쓰는지

#### 1. 정적 메서드 참조 (Static Method Reference)

```java
// ClassName::staticMethodName

// 예시 1: Math의 정적 메서드
BinaryOperator<Integer> maxFunc = Math::max;
int result = maxFunc.apply(10, 20);  // 20

// 람다 대비
BinaryOperator<Integer> maxLambda = (a, b) -> Math.max(a, b);

// 예시 2: 커스텀 정적 메서드
public class Calculator {
    public static int add(int a, int b) {
        return a + b;
    }
}

BinaryOperator<Integer> adder = Calculator::add;
System.out.println(adder.apply(5, 3));  // 8

// 예시 3: Integer.parseInt() 활용
Function<String, Integer> parser = Integer::parseInt;
int num = parser.apply("123");  // 123
```

#### 2. 특정 객체의 인스턴스 메서드 참조

```java
// instance::instanceMethodName

public class Printer {
    public void print(String message) {
        System.out.println(message);
    }
}

Printer printer = new Printer();
Consumer<String> printFunc = printer::print;
printFunc.accept("Hello");  // "Hello" 출력

// 람다 대비
Consumer<String> printLambda = msg -> printer.print(msg);

// 실제 사용 예: list.forEach()
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(printer::print);  // 각 이름 출력
```

#### 3. 임의의 객체의 인스턴스 메서드 참조

```java
// ClassName::instanceMethodName
// 첫 번째 파라미터가 "this" 역할

Function<String, Integer> lengthFunc = String::length;
int len = lengthFunc.apply("hello");  // 5

// 람다 대비
Function<String, Integer> lengthLambda = s -> s.length();

// BiFunction 예: 두 String의 비교
Comparator<String> comparator = String::compareTo;
int result = comparator.compare("apple", "banana");  // -1 (음수)

// 실제 사용 예: 정렬
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
names.sort(String::compareTo);  // 정렬
System.out.println(names);  // [Alice, Bob, Charlie]
```

#### 4. 생성자 참조 (Constructor Reference)

```java
// ClassName::new

// 예시 1: ArrayList 생성
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
ArrayList<String> list = listSupplier.get();
list.add("item");

// 람다 대비
Supplier<ArrayList<String>> listLambda = () -> new ArrayList<>();

// 예시 2: User 객체 생성
public class User {
    private String name;
    
    public User(String name) {
        this.name = name;
    }
}

Function<String, User> userFactory = User::new;
User user = userFactory.apply("Alice");

// 예시 3: Stream에서 객체 생성
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<User> users = names.stream()
    .map(User::new)  // User 생성자 참조
    .collect(Collectors.toList());
```

#### 5. 배열 생성자 참조

```java
// int[]::new

Function<Integer, int[]> arrayCreator = int[]::new;
int[] array = arrayCreator.apply(5);  // 크기 5인 int 배열 생성

// Stream에서 배열로 변환
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Integer[] result = numbers.stream()
    .toArray(Integer[]::new);  // 배열 생성자 참조
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **기존 메서드 재사용** | ✅ 메서드 참조 | 의도가 명확, 간결함 |
| **System.out::println** | ✅ 메서드 참조 | 람다보다 읽기 쉬움 |
| **로직 변환 필요** | ❌ 람다식 | 복잡한 표현은 람다가 나음 |
| **새로운 메서드 필요** | ❌ 메서드 정의 | 한 번만 쓰면 람다로 충분 |
| **여러 곳에서 재사용** | ✅ 메서드 참조 | 이름 있는 메서드로 명확 |

### 장점

| 장점 | 설명 |
|------|------|
| **가독성** | 메서드 이름이 명시되어 의도가 명확 |
| **간결성** | 람다보다 짧은 표현 |
| **재사용성** | 기존 메서드를 활용하므로 DRY 원칙 준수 |
| **보안** | 메서드 이름으로 의도를 드러내기 |
| **성능** | 람다보다 약간 빠를 수 있음 (컴파일 최적화) |

### 단점

| 단점 | 설명 |
|------|------|
| **문법 낯섦** | :: 연산자와 4가지 형태 이해 필요 |
| **제한적** | 파라미터 변환 불가능 (람다는 가능) |
| **타입 추론 실패** | 컨텍스트가 명확하지 않으면 컴파일 에러 |
| **디버깅 어려움** | 스택 트레이스에서 메서드 찾기 어려움 |

### 특징

#### 1. 네 가지 형태 정리

| 형태 | 문법 | 예시 |
|------|------|------|
| 정적 메서드 | `Class::staticMethod` | `Math::max` |
| 특정 객체의 메서드 | `instance::method` | `printer::print` |
| 임의 객체의 메서드 | `Class::method` | `String::length` |
| 생성자 | `Class::new` | `ArrayList::new` |

#### 2. 람다 vs 메서드 참조

```java
// 1️⃣ System.out.println
// 람다
Consumer<String> printLambda = x -> System.out.println(x);

// 메서드 참조
Consumer<String> printRef = System.out::println;

// 2️⃣ 정렬
// 람다
names.sort((a, b) -> a.compareTo(b));

// 메서드 참조
names.sort(String::compareTo);

// 3️⃣ 필터링
// 람다
list.stream().filter(s -> s.startsWith("A"));

// 메서드 참조
list.stream().filter(new StartsWithAPredicate()::test);
// 또는 람다만 가능
```

#### 3. 메서드 참조의 타입 추론

```java
// 컨텍스트로 타입 결정
Function<String, Integer> f1 = String::length;      // String → Integer
BiFunction<String, String, Integer> f2 = String::compareTo;  // String, String → Integer

// 같은 메서드도 문맥에 따라 다른 타입으로 해석
```

#### 4. 생성자 참조의 활용

```java
// 다양한 형태의 생성자 참조
Supplier<User> noArgs = User::new;         // () → User
Function<String, User> oneArg = User::new;  // String → User
BiFunction<String, Integer, User> twoArgs = User::new;  // String, Integer → User

// Stream에서 자주 사용
List<String> names = Arrays.asList("Alice", "Bob");
List<User> users = names.stream()
    .map(User::new)
    .collect(Collectors.toList());
```

#### 5. 메서드 참조 체이닝

```java
// 여러 메서드 참조 결합
List<String> names = Arrays.asList("alice", "bob", "charlie");

names.stream()
    .map(String::toUpperCase)       // String::toUpperCase
    .filter(s -> s.length() > 3)    // 필터
    .forEach(System.out::println);  // System.out::println
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 존재하지 않는 메서드 참조**

```java
// ❌ 컴파일 에러: notExists 메서드가 없음
Runnable r = System.out::notExists;
```

**✅ 올바른 방식:**
- 존재하는 public 메서드만 참조
- IDE의 자동완성 활용

</div>

<div class="warning-box" markdown="1">

**⚠️ 메서드 참조로 표현 불가능한 경우**

```java
// ❌ 메서드 참조로 불가능: 파라미터 변환 필요
Function<String, Integer> parser = Integer::parseInt;
int result = parser.apply("123");  // OK

// 하지만 Radix(진법)를 지정하려면?
// Integer.parseInt(String, int) 형태
// 람다로만 가능
Function<String, Integer> hexParser = s -> Integer.parseInt(s, 16);
// 메서드 참조로는 불가능 (파라미터가 고정될 수 없음)
```

**✅ 복잡한 경우 람다 사용:**
```java
Function<String, Integer> hexParser = s -> Integer.parseInt(s, 16);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 람다가 더 명확할 수 있음**

```java
// 메서드 참조 (짧지만 의도 불명확)
list.stream()
    .filter(String::isEmpty)
    .forEach(System.out::println);

// 람다 (더 의도가 명확)
list.stream()
    .filter(s -> s.isEmpty())
    .forEach(s -> System.out.println(s));
```

</div>

<div class="tip-box" markdown="1">

**💡 메서드 참조 활용 팁**

- `System.out::println` — 간단한 출력
- `String::toUpperCase` — 변환
- `String::compareTo` — 비교/정렬
- `ArrayList::new` — 컬렉션 생성
- `Integer::parseInt` — 파싱
- `Collections::sort` — 유틸리티 메서드

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **목적** | 기존 메서드를 람다로 표현 |
| **문법** | `Class/instance::methodName` |
| **형태** | 정적, 특정 객체, 임의 객체, 생성자 |
| **장점** | 간결, 명확, 가독성 |
| **주의** | 존재하는 메서드만 가능, 복잡한 변환은 람다 |

---

**관련 파일:**
- [람다 기초](start.md) — 함수형 인터페이스 이해
- [함수형 인터페이스](function.md) — 메서드 참조 대상
- [Stream API](streamApi.md) — 메서드 참조 활용 사례
