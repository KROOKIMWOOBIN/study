## 람다 기초

### 왜 쓰는지

Java는 객체지향 언어로, 모든 것이 클래스/메서드에 속해야 합니다. 하지만:
- 간단한 로직 하나를 위해 클래스를 만들기는 번거로움
- 함수를 값처럼 전달하고 싶을 때가 있음 (메서드를 매개변수로)

<div class="concept-box" markdown="1">

**핵심**: 람다는 **이름 없는 함수(익명 함수)**를 간결하게 작성하는 문법입니다. 메서드를 값처럼 전달할 수 있게 해줍니다.

</div>

### 어떻게 쓰는지

#### 전통적 방식 vs 람다

```java
// 1️⃣ 전통 방식: 무명 클래스
button.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick() {
        System.out.println("clicked");
    }
});

// 2️⃣ 람다 방식 (Java 8+)
button.setOnClickListener(() -> System.out.println("clicked"));
```

#### 람다식 문법

```text
(매개변수) -> { 본문 }
```

| 형태 | 예시 |
|------|------|
| 매개변수 있음, 반환값 있음 | `(a, b) -> a + b` |
| 매개변수 있음, 반환값 없음 | `(x) -> System.out.println(x)` |
| 매개변수 없음 | `() -> 42` |
| 본문이 복잡함 | `(x) -> { int result = x * 2; return result; }` |

#### 축약 규칙

```java
// 1. 타입 생략 가능 (컴파일러가 유추)
(int a, int b) -> a + b
(a, b) -> a + b  // ✅ 축약

// 2. 매개변수 1개면 괄호 생략 가능
(x) -> x * 2
x -> x * 2  // ✅ 축약

// 3. 본문이 1줄이면 {} 생략, return도 생략
(a, b) -> { return a + b; }
(a, b) -> a + b  // ✅ 축약

// 4. 본문이 void면 중괄호 생략
(x) -> { System.out.println(x); }
(x) -> System.out.println(x)  // ✅ 축약
```

#### 실제 예시

```java
// 정렬: 비교 로직을 람다로 전달
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.sort((a, b) -> a.compareTo(b));
System.out.println(names);  // [Alice, Bob, Charlie]

// 필터링: 조건을 람다로 전달
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.stream()
    .filter(n -> n % 2 == 0)  // 짝수만 필터링
    .forEach(System.out::println);  // 2, 4

// 변환: 변환 로직을 람다로 전달
List<Integer> squared = numbers.stream()
    .map(n -> n * n)  // 제곱
    .collect(Collectors.toList());
System.out.println(squared);  // [1, 4, 9, 16, 25]
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **함수형 인터페이스 구현** | ✅ 람다 | 간결함 |
| **콜백 로직 전달** | ✅ 람다 | 인라인으로 로직 표현 |
| **스트림 처리** | ✅ 람다 | 함수형 스타일로 가독성 높음 |
| **복잡한 객체 생성** | ❌ 무명 클래스 | 여러 메서드 구현 필요 |
| **상태 저장 필요** | ❌ 무명 클래스 | 람다는 상태 없이 순수 함수여야 함 |

### 장점

| 장점 | 설명 |
|------|------|
| **간결한 문법** | 무명 클래스 대비 코드 줄 수 크게 감소 |
| **가독성** | 의도한 로직이 명확하게 드러남 |
| **함수형 프로그래밍** | 함수를 1급 객체처럼 취급 가능 |
| **스트림 API와 호환** | Stream의 `filter`, `map` 등과 자연스러운 조합 |

### 단점

| 단점 | 설명 |
|------|------|
| **학습곡선** | 처음 접하면 문법이 낯설 수 있음 |
| **단순한 경우 오버킬** | 간단한 로직에는 메서드 참조가 더 읽기 쉬울 수 있음 |
| **스택 트레이스** | 디버깅 시 람다 내부 오류 위치 파악 어려움 |
| **다중 표현식** | 여러 줄 로직은 가독성 떨어짐 (메서드 분리 권장) |

### 특징

#### 1. 함수형 인터페이스 (Functional Interface)

람다는 **함수형 인터페이스**(추상 메서드 1개만 가진 인터페이스)를 구현할 때만 사용 가능합니다.

```java
// ✅ 함수형 인터페이스: 추상 메서드 1개
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// 람다로 구현
Calculator add = (a, b) -> a + b;
System.out.println(add.calculate(3, 5));  // 8

// ❌ 함수형 인터페이스 아님: 추상 메서드 2개 이상
interface BadInterface {
    void method1();
    void method2();
}
// 람다로 구현 불가
```

#### 2. 클로저 (Closure) - 외부 변수 캡처

```java
int baseValue = 10;  // final 또는 effectively final

// 람다가 외부 변수 캡처 (읽기만 가능)
Function<Integer, Integer> add = x -> x + baseValue;
System.out.println(add.apply(5));  // 15

baseValue = 20;  // ❌ 컴파일 에러! effectively final 위반
```

**규칙:** 람다 외부의 변수는 **final이거나 effectively final**이어야 합니다 (수정 불가).

#### 3. 메서드 참조 (Method Reference)

람다 대신 기존 메서드를 직접 참조할 수 있습니다.

```java
List<String> names = Arrays.asList("alice", "bob", "charlie");

// 람다로 표현
names.forEach(name -> System.out.println(name));

// 메서드 참조로 더 간결하게
names.forEach(System.out::println);

// 정렬
names.sort((a, b) -> a.compareTo(b));  // 람다
names.sort(String::compareTo);  // 메서드 참조 (더 간결)
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 함수형 인터페이스가 아닌 인터페이스에 람다 사용 불가**
```java
interface MyInterface {
    void method1();
    void method2();  // 추상 메서드 2개
}

// ❌ 컴파일 에러
MyInterface obj = () -> System.out.println("hello");
```

**✅ 올바른 방식:**
```java
@FunctionalInterface
interface SingleMethod {
    void execute();
}

SingleMethod obj = () -> System.out.println("hello");
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 외부 변수 수정 불가**
```java
int count = 0;

// ❌ 컴파일 에러
list.forEach(x -> {
    count++;  // count 수정 불가 (effectively final 위반)
});
```

**✅ 올바른 방식 - 새 변수 사용:**
```java
int[] count = {0};  // 배열은 참조이므로 내부 값 수정 가능
list.forEach(x -> count[0]++);

// 또는 스트림으로 변환
long newCount = list.stream().count();
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 복잡한 로직은 메서드로 분리하기**
```java
// ❌ 읽기 어려움
list.stream()
    .filter(x -> {
        // 여러 줄 복잡한 로직
        int result = x * 2;
        if (result > 100) return true;
        return false;
    })
    .forEach(System.out::println);

// ✅ 메서드로 분리
private boolean isLargeWhenDoubled(int x) {
    return x * 2 > 100;
}

list.stream()
    .filter(this::isLargeWhenDoubled)
    .forEach(System.out::println);
```

</div>

### 정리

| 구분 | 설명 |
|------|------|
| **람다란** | 함수형 인터페이스를 간결하게 구현하는 익명 함수 |
| **문법** | `(params) -> body` |
| **필수 조건** | 함수형 인터페이스 (추상 메서드 1개) |
| **장점** | 간결성, 가독성, 스트림 조합성 |
| **주의** | effectively final 변수만 캡처 가능 |
| **다음 단계** | [함수형 인터페이스](function.md), [메서드 참조](method.md), [스트림](stream.md) 참고 |
