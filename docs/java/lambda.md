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

### 종류
| 유형                  | 형태                    | 예시               | 설명         |
| ------------------- | --------------------- | ---------------- | ---------- |
| 정적 메서드 참조           | `Class::staticMethod` | `Math::max`      | static 호출  |
| 인스턴스 메서드 참조 (특정 객체) | `instance::method`    | `obj::print`     | 특정 객체 기준   |
| 인스턴스 메서드 참조 (임의 객체) | `Class::method`       | `String::length` | 첫 파라미터가 대상 |
| 생성자 참조              | `Class::new`          | `ArrayList::new` | 객체 생성      |

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