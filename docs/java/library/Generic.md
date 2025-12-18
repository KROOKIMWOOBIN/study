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
    Integer result2 = GenericMethod.genericMethod(i);
    Integer integerValue2 = GenericMethod.numberMethod(10);
    Double doubleValue2 = GenericMethod.numberMethod(20.0);
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
- Generic Method 가 Generic Type 보다 더 높은 우선순위를 가진다.
```java
public class Ex3 {
    public static void main(String[] args) {
        Dog dog = new Dog("멍멍이", 100);
        Cat cat = new Cat("냐옹이", 50);
        ComplexBox<Dog> hospital = new ComplexBox<>();
        hospital.set(dog);
        Cat returnCat = hospital.printAndReturn(cat);
        System.out.println("returnCat = " + returnCat);
    }
}
class ComplexBox<T extends Animal> {
    private T animal;
    public void set(T animal) {
        this.animal = animal;
    }
    public <Z> Z printAndReturn(Z z) {
        System.out.println("animal.className: " + animal.getClass().getName());
        System.out.println("z.className: " + z.getClass());
        return z;
    }
}
```
- wildcard
  - Wildcard 는 Generic Type, Generic Method 를 선언하는 것이 아니다. Wildcard 는 이미 만들어진 Generic Type 을 활용할 때 사용한다.
- 비제한 wildcard
  - ?만 사용해서 제한 없이 모든 타입을 다 받을 수 있는 와일드카드를 비제한 와일드카드라 한다.
```java
class Wildcard {
    /*
            Box<Dog> dogBox를 전달한다. 타입 추론의 의해 타입 T가 Dog가 된다.
     */
    static <T> void printGeneric(Box<T> box) {
      System.out.println("T = " + box.get());
    }
    /*
            Box<Dog> dogBox를 전달한다. 와일드카드는 ?는 모든 타입을 받을 수 있다.
     */
    static void printWildcard(Box<?> box) {
      System.out.println("? = " + box.get());
    }
}
```
- 상한 wildcard
  - 자기 자신 이하만 사용할 수 있다.
```java
class Wildcard {
    static void printWildcard(Box<? extends Animal> box) {
        Animal animal = box.get();
        System.out.println("이름 = " + animal.getName());
    }
}
```
- 하한 wildcard
  - 자기 자신 이상만 사용할 수 있다.
```java
class Wildcard {
    static void printWildcard(Box<? super Animal> box) {
        Animal animal = box.get();
      System.out.println("이름 = " + animal.getName());
    }
}
```
- Type eraser
  - 지우개라는 뜻이다.
  - 자바의 제네릭 타입은 컴파일 시점에 타입을 체크하고, 컴파일 이후에는 제네릭 정보가 지워지는데, 이것을 타입 이레이저라 한다.
  - 다음과 같이 타입 이레이저 때문에 사용할 수 없는 코드이다.
```java
class Main<T> {
    public T returnT(Object param) {
        return param instanceof T;
    }
    public T newT() {
        return new T();
    }
}
```