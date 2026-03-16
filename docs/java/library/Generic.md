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
=> 제네릭이 없다면, 런타임 오류
```