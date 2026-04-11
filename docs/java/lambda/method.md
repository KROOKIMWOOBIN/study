## 메서드 참조
- 람다식에서 이미 존재하는 메서드를 그대로 전달
- :: 연산자를 사용
- 함수형 인터페이스와 결합됨
```markdown
list.forEach(System.out::println);
// (x) -> System.out.println(x)
```

### 정적 메서드 참조 (Static Method Reference)

#### 개념
- 클래스에 정의된 static 메서드를 직접 참조
- 인스턴스 생성 없이 호출

#### 문법
```markdown
ClassName::staticMethod
```

#### 예제
```markdown
public class Main {
    public static void main(String[] args) {
        BinaryOperator<Integer> max = Math::max;

        int result = max.apply(10, 20);
        System.out.println(result); // 20
    }
}
```

### 인스턴스 메서드 참조 (특정 객체)

#### 개념
- 이미 생성된 객체의 메서드를 참조
- 고정된 객체 기준으로 동작

#### 문법
```markdown
instance::method
```

#### 예제
```markdown
public class Main {
    public static void main(String[] args) {
        Printer printer = new Printer();

        Consumer<String> consumer = printer::print;
        consumer.accept("Hello");
    }
}

class Printer {
    public void print(String message) {
        System.out.println(message);
    }
}
```

### 인스턴스 메서드 참조 (임의 객체)

#### 개념
- 특정 객체가 아니라 파라미터로 전달된 객체를 기준으로 호출
- 첫 번째 인자가 "this" 역할을 함

#### 문법
```markdown
ClassName::method
```

#### 예시
```markdown
public class Main {
    public static void main(String[] args) {
        Function<String, Integer> lengthFunc = String::length;
        int len = lengthFunc.apply("hello");
        System.out.println(len); // 5
    }
}
```

### 생성자 참조 (Constructor Reference)

#### 개념
- 생성자를 함수처럼 참조
- 객체 생성 로직을 람다로 대체

#### 문법
```markdown
ClassName::new
```

#### 예시
```markdown
public class Main {
    public static void main(String[] args) {
        Supplier<ArrayList<String>> supplier = ArrayList::new;
        ArrayList<String> list = supplier.get();
        list.add("A");
        System.out.println(list);
    }
}
```

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
- 타입 추론 의존
    - 컴파일러가 문맥으로 매칭
```markdown
Function<String, Integer> f = String::length;
```
- 가독성 중심 기능
    - 로직이 아닌 “전달”에 초점
