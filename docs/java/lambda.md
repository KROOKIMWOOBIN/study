## 매개변수화
```markdown
프로그램의 유연성·재사용성을 높이기 위한 설계 기법이다.
핵심은 고정된 로직을 만들지 않고 외부에서 값을 전달받도록 만드는 것이다.
```
### 값 매개변수화
```markdown
값을 바꿔서 동일한 로직을 재사용하는 방식

int add(int a, int b) {
    return a + b;
}
add(1, 2);
```
### 동작 매개변수화
```markdown
동작(로직)을 외부에서 전달하는 방식

void execute(Runnable runnable) {
    runnable.run();
}
execute(() -> System.out.println("hello"));
```

## 함수
- 객체나 클래스에 속하지 않고 독립적으로 존재할 수 있다.
- 주로 절차지향 언어 또는 함수형 언어에서 사용되는 개념
## 메서드
- 객체(또는 클래스)에 속해 있는 함수
- 주로 객체지향 언어에서 사용되는 개념

## 람다
- 익명 함수를 지칭하는 일반적인 용어, 즉 개념이다.
## 람다식
- (매개변수) -> { 본문 } 형태로 람다를 구현하는 구체적인 문법을 지칭한다.

### 람다식 축약 규칙
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

## 스트림

## static factory