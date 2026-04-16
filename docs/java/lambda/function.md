## 함수형 인터페이스 & 고차 함수

### 왜 쓰는지

Java는 OOP 언어로, **모든 것이 객체**여야 합니다. 함수를 직접 전달할 수 없으므로:
- 함수를 값처럼 다루려면 **인터페이스로 감싸야 함**
- 메서드를 매개변수로 전달하려면 **함수형 인터페이스 필요**

<div class="concept-box" markdown="1">

**핵심**:
- **함수형 인터페이스**: 추상 메서드 1개만 가진 인터페이스
- **고차 함수**: 함수를 매개변수로 받거나 반환하는 함수
- 이 두 개가 함께 **함수형 프로그래밍을 가능**하게 합니다.

</div>

### 어떻게 쓰는지

#### 함수형 인터페이스 정의

```java
// ✅ 함수형 인터페이스: 추상 메서드 1개만
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// ❌ 함수형이 아님: 추상 메서드 2개
interface TooMany {
    int method1();
    int method2();
}
```

#### 고차 함수 예시

```java
// 1️⃣ 함수를 매개변수로 받기
public void execute(Runnable task) {
    System.out.println("시작");
    task.run();
    System.out.println("종료");
}

execute(() -> System.out.println("작업"));
// 출력: 시작 → 작업 → 종료

// 2️⃣ 함수를 반환하기
public Function<Integer, Integer> makeMultiplier(int factor) {
    return x -> x * factor;
}

Function<Integer, Integer> double = makeMultiplier(2);
System.out.println(double.apply(5));  // 10
```

#### 필터링 함수 만들기

```java
// 고차 함수: Predicate를 받아서 필터링
public <T> List<T> filter(List<T> list, Predicate<T> condition) {
    return list.stream()
        .filter(condition)
        .toList();
}

// 사용
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evens = filter(numbers, n -> n % 2 == 0);
System.out.println(evens);  // [2, 4]

List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<String> longNames = filter(names, name -> name.length() > 3);
System.out.println(longNames);  // [Alice, Charlie]
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **콜백 처리** | ✅ 함수형 인터페이스 | 로직을 외부에서 주입 |
| **필터링/변환** | ✅ Function/Predicate | 조건을 파라미터로 전달 |
| **버튼 클릭 등 이벤트** | ✅ ActionListener | 이벤트 발생 시 실행 |
| **스트림 처리** | ✅ 함수형 인터페이스 | filter, map, forEach 등 |
| **복잡한 객체** | ❌ 일반 인터페이스 | 메서드 여러 개 필요 |

### 장점

| 장점 | 설명 |
|------|------|
| **유연성** | 로직을 런타임에 주입 가능 |
| **재사용성** | 같은 함수를 다양한 곳에서 사용 |
| **간결함** | 람다로 인라인 작성 가능 |
| **테스트 용이** | 함수를 쉽게 테스트할 수 있음 |

### 단점

| 단점 | 설명 |
|------|------|
| **학습곡선** | 함수형 프로그래밍 개념 필요 |
| **가독성** | 중첩 함수형 인터페이스는 복잡 |
| **타입 추론 실패 가능** | 타입 명시 필요할 때도 있음 |

### 특징

#### 기본 함수형 인터페이스 (java.util.function)

| 인터페이스 | 메서드 | 매개변수 | 반환 | 용도 |
|-----------|--------|---------|------|------|
| `Function<T,R>` | `R apply(T t)` | O (1개) | O | 입력 → 출력 변환 |
| `Consumer<T>` | `void accept(T t)` | O (1개) | X (void) | 값 소비 (출력 등) |
| `Supplier<T>` | `T get()` | X | O | 값 생성 |
| `Predicate<T>` | `boolean test(T t)` | O (1개) | boolean | 조건 검사 |

#### 확장 함수형 인터페이스

| 인터페이스 | 메서드 | 설명 |
|-----------|--------|------|
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | 2개 매개변수 → 1개 반환 |
| `BiConsumer<T,U>` | `void accept(T t, U u)` | 2개 매개변수 → 소비 |
| `BiPredicate<T,U>` | `boolean test(T t, U u)` | 2개 매개변수 → boolean |
| `UnaryOperator<T>` | `T apply(T t)` | 같은 타입 변환 (Function<T,T>) |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | 2개 같은 타입 → 같은 타입 |

#### 기본형 특화 인터페이스 (성능 최적화)

```java
// 박싱/언박싱 오버헤드 제거
IntFunction<String> converter = i -> "Value: " + i;
ToIntFunction<String> parser = s -> Integer.parseInt(s);
IntUnaryOperator doubler = x -> x * 2;
```

| 인터페이스 | 메서드 | 예시 |
|-----------|--------|------|
| `IntFunction<R>` | `R apply(int i)` | 정수 → 임의 타입 |
| `ToIntFunction<T>` | `int applyAsInt(T t)` | 임의 타입 → 정수 |
| `IntUnaryOperator` | `int applyAsInt(int i)` | 정수 → 정수 변환 |
| `IntBinaryOperator` | `int applyAsInt(int a, int b)` | 정수 2개 → 정수 |

#### 기타 함수형 인터페이스

| 인터페이스 | 용도 |
|-----------|------|
| `Runnable` | 스레드 실행 (매개변수 없음, 반환값 없음) |
| `Callable<V>` | 스레드 실행, 결과 반환 |
| `Comparator<T>` | 객체 비교/정렬 |

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 추상 메서드 2개 이상인 인터페이스에 람다 불가**
```java
interface BadInterface {
    void method1();
    void method2();
}

// ❌ 컴파일 에러
BadInterface obj = () -> System.out.println("hello");
```

**✅ 올바른 방식:**
```java
@FunctionalInterface
interface GoodInterface {
    void method1();
}

GoodInterface obj = () -> System.out.println("hello");
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 복잡한 로직은 명확한 메서드 참조 사용**

```java
// ❌ 읽기 어려움
list.forEach(item -> {
    if (item.isActive()) {
        System.out.println(item.getName());
    }
});

// ✅ 메서드로 분리
private void printActiveItem(Item item) {
    if (item.isActive()) {
        System.out.println(item.getName());
    }
}

list.forEach(this::printActiveItem);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 기본형 특화 인터페이스 사용하기**

```java
// ❌ 박싱 오버헤드
Function<Integer, Integer> f = x -> x * 2;  // Integer 박싱/언박싱

// ✅ 기본형 특화 사용
IntUnaryOperator f = x -> x * 2;  // int 직접 처리, 오버헤드 없음
```

특히 **반복문이 많은 경우** 성능 차이 발생

</div>

### 실전 예시

#### 콜백 처리

```java
public class Button {
    private Runnable onClickListener;
    
    public void setOnClickListener(Runnable listener) {
        this.onClickListener = listener;
    }
    
    public void click() {
        System.out.println("버튼 클릭됨");
        onClickListener.run();  // 콜백 실행
    }
}

// 사용
Button btn = new Button();
btn.setOnClickListener(() -> System.out.println("처리됨"));
btn.click();  // 버튼 클릭됨 → 처리됨
```

#### 필터링 함수

```java
public <T> List<T> filter(List<T> list, Predicate<T> condition) {
    return list.stream()
        .filter(condition)
        .toList();
}

List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evens = filter(numbers, n -> n % 2 == 0);
List<Integer> large = filter(numbers, n -> n > 3);
System.out.println(evens);  // [2, 4, 6]
System.out.println(large);  // [4, 5, 6]
```

#### 팩토리 함수

```java
public Function<Integer, Integer> createMultiplier(int factor) {
    return x -> x * factor;  // 함수 반환
}

Function<Integer, Integer> double = createMultiplier(2);
Function<Integer, Integer> triple = createMultiplier(3);

System.out.println(double.apply(5));  // 10
System.out.println(triple.apply(5));  // 15
```

### 정리

| 항목 | 설명 |
|------|------|
| **함수형 인터페이스** | 추상 메서드 1개만 가진 인터페이스 + @FunctionalInterface |
| **고차 함수** | 함수를 받거나 반환하는 함수 |
| **기본 인터페이스** | Function, Consumer, Supplier, Predicate |
| **기본형 특화** | IntFunction, ToIntFunction 등 (성능 최적화) |
| **사용처** | 콜백, 필터링, 스트림 API |

---

**관련 내용:**
- [람다 기초](start.md) — 함수형 인터페이스 구현 방법
- [메서드 참조](method.md) — 더 간결한 표현
- [Stream API](streamApi.md) — 함수형 인터페이스 활용
