## 고차 함수 (Higher-Order Function)

<div class="concept-box" markdown="1">

==고차 함수==: 함수를 값처럼 다루는 함수. Java는 함수 자체를 직접 전달할 수 없기 때문에 **함수형 인터페이스**를 사용한다.

</div>


### 일반적으로 아래 케이스를 고차 함수라 뜻한다

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
