- Generic
    - <>를 사용한 클래스를 제네릭 클래스라고 한다. 이 기호(<>)를 보통 다이아몬드라고 한다.
    - 제네릭 클래스를 사용할 때 Integer, String 같은 타입을 미리 결정하지 않는다.
    - 클래스명 오른쪽에 <T>와 같이 선언하면 제네릭 클래스라고 한다.
        - 여기서 T를 타입 매개변수라고 한다.
        - 이 타입 매개변수는 이후에 Integer, String 으로 변할 수 있다.
    - 클래스 내부에 T 타입이 필요한 곳에 T value 같이 타입 매개변수를 적어주면 된다.
```java
public class Main {
    public static void main(String[] args) {
        /**
         * 원하는 모든 타입 사용 가능
         */
        GenericBox<Integer> integerBox = new GenericBox<Integer>();
        integerBox.set(10);
        Integer integer = integerBox.get();
        System.out.println("integer: " + integer);
        /**
         * 타입 추론: 생성하는 제네릭 타입 생략 가능
         */
        GenericBox<String> stringBox = new GenericBox<>();
        stringBox.set("hello");
        Stirng str = stringBox.get();
        System.out.println("str: " + str);
    }
}
class GenericBox<T> { 
    private T value;
    public void set(T value) {
        this.value = value;
    }
    public T get() {
        return value;
    }
}
```
- 제네릭 명명 관례

| 타입         | 설명                  |
|:-----------|:--------------------|
| E          | Element             |
| K          | Key                 |
| N          | Number              |
| T          | Type                |
| V          | Value               |
| S,U,V etc. | 2nd, 3nd, 4th types |

- 제네릭 기타
  - 다음과 같이 한번에 여러 타입 매개변수를 선언할 수 있다.
  - 제네릭의 타입 인자로 기본형은 사용할 수 없다. 대신에 래퍼 클래스를 사용하면 된다.
```java
class Data<K, V> {}
```
- Row Type
  - 다음과 같이 <>을 지정하지 않을 수 있는데, 이런 것을 Row Type, 또는 원시 타입이라한다.
  - 원시 타입을 사용하면 내부의 타입 매개변수가 Object로 사용된다고 이해하면 된다.
```java
public class RowTypeMain {
    public static void main(String[] args) {
        GenericBox integerBox = new GenericBox();
        // GenericBox<Object> integerBox = new GenericBox<>();
        integerBox.setValue(10);
        Integer result = (Integer) integerBox.getValue();
        System.out.println("result: " + result);
    }
}
```
- 타입 매개변수 제한
  - 다음과 같이 Human 클래스를 타입 매개변수에 상속하면 Human 클래스 또는 자식 클래스만 타입 매개변수에 들어갈 수 있다.
  - 이와 같이 사용하는 이유는 컴파일 전 Human 클래스와 관련된 메서드를 호출하기 위해서다.
  ```java
  public class Main {
    public static void main(String[] args){
      // 아무런 클래스나 들어갈 수 있다.
      Hospital<Object> hospital = new Hospital<>();
    }
    static class Hospital<T> {
    }
  }
  ```
```java
// 사람과 관련된 클래스만 들어갈 수 있다.
public class Hospital<T extends Human> {
    private T human;
    // Human 상속받지 않으면, Object 메서드만 호출이 가능하다.
    public T getHuman() {
        return human.getInfo();
    }
}
```
- Generic Method
```java
public class Main {
  public static void main(String[] args) {
    Integer i = 10;
    Object object = GenericMethod.objMethod(i);

    System.out.println("명시적 타입 인자 전달");
    Integer result1 = GenericMethod.<Integer>genericMethod(i);
    Integer integerValue1 = GenericMethod.<Integer>numberMethod(10);
    Double doubleValue1 = GenericMethod.<Double>numberMethod(20.0);

    System.out.println("제네릭 메서드 타입 추론");
    Integer result2 = GenericMethod.<Integer>genericMethod(i);
    Integer integerValue2 = GenericMethod.<Integer>numberMethod(10);
    Double doubleValue2 = GenericMethod.<Double>numberMethod(20.0);
  }
}
class GenericMethod {
    public static Object objectMethod(Object obj) {
        System.out.println("Object print: " + obj);
        return obj;
    }
    public static <T> T genericMethod(T t) {
        System.out.println("Generic print: " + t);
        return t;
    }
    public static <T extends Number> T numberMethod(T t) {
        System.out.println("Number print: " + t);
        return t;
    } 
}
```
- Instance Method, static Method 둘 다 Generic 사용이 가능하다.
```java
class Box<T> { 
    static <V> V staticMethod(V v) {}
    <Z> Z instanceMethod(Z z) {}
}
```
- Class Generic 은 static Method 에서 타입 매개변수를 사용할 수 없다.
```java
class Box<T> {
    static <T> T staticMethod(T t) {} // 제네릭 타입의 T 사용 불가능
    <T> T instanceMethod(T t) {} // 가능
}
```