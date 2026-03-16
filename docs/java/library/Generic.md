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

```