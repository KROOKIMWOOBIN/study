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
=> 
```