## Generic

### 개념
- <>를 사용한 클래스를 제네릭(Generic) 클래스라고 한다.
- <> 기호를 다이아몬드 연산자(Diamond Operator) 라고 한다.
- 제네릭은 타입을 매개변수화(parameterize) 하는 기능이다.

즉, 클래스나 메서드가 사용할 타입을 외부에서 결정할 수 있게 한다.
```markdown
class Box<T> {
    T value;
}
-> 사용 시점에서 타입이 결정된다.
Box<Integer>
Box<String>
```

### 제네릭을 사용하는 이유
1. 타입 안전성 (Type Safety)
```markdown
=> 컴파일 시점에 타입 오류를 발견한다.
List<Integer> list = new ArrayList<>();
list.add("hello"); // 컴파일 오류

=> 제네릭이 없다면
List list = new ArrayList();
list.add("hello");
Integer num = (Integer) list.get(0); // 런타임 오류
```
| 구분     | 오류 발생 |
| ------ | ----- |
| 제네릭 없음 | 런타임   |
| 제네릭 있음 | 컴파일   |
2. 캐스팅 제거
```markdown
=> 제네릭
List<Integer> list = new ArrayList<>();
Integer num = list.get(0);
=> 제네릭이 없으면
Integer num = (Integer) list.get(0); 
```

### 타입 매개변수 선언
```markdown
class GenericBox<T> {
    private T value;
}
```
| 위치        | 의미         |
| --------- | ---------- |
| `<T>`     | 타입 매개변수 선언 |
| `T value` | 실제 타입 사용   |
#### 사용 예시
```markdown
GenericBox<Integer> box = new GenericBox<>();
```

### 제네릭 명명 관례
| 타입    | 의미      |
| ----- | ------- |
| T     | Type    |
| E     | Element |
| K     | Key     |
| V     | Value   |
| N     | Number  |
| S,U,V | 여러 타입   |

#### 여러 타입 매개변수
```markdown
class Pair<K,V> {
    K key;
    V value;
}
Pair<String, Integer>
```

### 제네릭 타입 제한 (Bounded Type)
- 특정 타입만 허용한다.
```markdown
class Hospital<T extends Human>
=> 가능 : Human, HumanChild
=> 불가능 : String, Integer
```

### 다중 상한 (Multiple Bounds)
- 클래스 1개 + 인터페이스 여러개
```markdown
class C<T extends A & B & C>
```
#### 규칙
1. 클래스는 1개만 가능
2. 클래스는 맨 앞
3. 이후는 인터페이스

### Generic Method
- 메서드 자체가 제네릭이 될 수 있다.
```markdown
public static <T> T genericMethod(T t)
=> Integer num = genericMethod(10);
=> 컴파일러가 타입 추론한다.
```

### Instance / Static Generic Method
```markdown
class Box<T> {

    static <V> V staticMethod(V v) {}

    <Z> Z instanceMethod(Z z) {}
}
```

### Class Generic vs Method Generic
```markdown
클래스 제네릭
=> class Box<T>

메서드 제네릭
=> <T> T method(T t)
```

#### 우선순위
- 메서드 제네릭이 클래스 제네릭보다 우선
```markdown
class Box<T> {
    <T> T method(T t) { }
}
```
- 여기서 T 는 메서드 타입

### Raw Type
- 제네릭을 사용하지 않는 타입
```markdown
List list = new ArrayList();
=> List<Object>처럼 동작
```

#### 문제
- 타입 안정성 깨짐
- 캐스팅 필요
- 컴파일 경고

### Wildcard
- 제네릭 타입을 사용할 때 타입 범위를 표현
```markdown
Box<?>
```

#### 주의
- 제네릭 선언이 아님
- 사용 시점 타입 표현

#### 비제한 wildcard
```markdown
Box<?> box
=> 모든 타입 허용
Box<String>
Box<Integer>
Box<Dog>
=> box.set() 불가능 왜? 어떤 타입인지 모름
```

#### 상한 wildcard
```markdown
<? extends Animal>
=> Animal 이하
Animal
Dog
Cat
=> 특징
읽기 가능
쓰기 불가능
```

#### 하한 wildcard
```markdown
<? super Animal>
=> Animal 이상
Animal
Object
=> 특징
쓰기 가능
읽기 제한
```

### PECS 원칙 (매우 중요)
- 제네릭 설계 핵심 원칙
```markdown
Producer Extends
Consumer Super
```
| 역할         | 사용      |
| ---------- | ------- |
| 데이터를 꺼내는 곳 | extends |
| 데이터를 넣는 곳  | super   |

#### 예시
```markdown
public static double sum(List<? extends Number> list)
=> 데이터를 꺼낼 수 있지만, 어떤 리스트인지 몰라 넣을 수 없음

public static void add(List<? super Integer> list)
=> 데이터를 넣을 수 있지만, 꺼낼 때 어떤 리스트인지 몰라 꺼낼 수 없음
```

### 제네릭 불공변성 (Invariance)
- Java 제네릭은 불공변
```markdown
List<Dog>은 List<Animal>의 하위 타입이 아니다.
List<Animal> animals = new ArrayList<Dog>(); // 오류
=> 그래서 등장한 것이 [wildcard]
```

### Generic 배열 생성 제한
```markdown
=> 불가능
T[] array = new T[10];
=> 가능
Object[] array = new Object[10]; 
List<T>
```

### Type Erasure (타입 소거)
- 자바 제네릭은 컴파일 이후 제거된다.
```markdown
=> 컴파일 전
List<String>
List<Integer>
=> 컴파일 후
List
=> 즉, [JVM]은 제네릭을 모른다
```

#### Type Erasure 과정
```markdown
=> 시작
class Box<T> {
    T value;
}
=> 컴파일 후
class Box {
    Object value;
}

=> 제한이 있으면
class Box<T extends Number>
=> 컴파일 후
class Box {
    Number value;
}
```

#### Type Erasure 때문에 불가능한 것
```markdown
=> new T()
=> T.class
=> instanceof T
```

### 제네릭 핵심

#### 목적
1. 타입 안정성
2. 캐스팅 제거
3. 코드 재사용

#### 특징
1. 컴파일 타입 체크
2. 타입 소거 기반
3. 불공변
4. 와일드카드 사용