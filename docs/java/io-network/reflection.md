## Reflection (리플렉션)

### 왜 쓰는지

Java 프로그램은 일반적으로 **컴파일 타임에 클래스 정보를 알고** 사용합니다. 하지만 때로는:
- 실행 중에 어떤 클래스를 사용할지 결정해야 함 (프레임워크, 플러그인)
- 클래스 구조를 분석하고 동적으로 제어해야 함 (테스트, 마샬링)
- 메서드를 이름으로 찾아 호출해야 함 (직렬화, 네트워크 통신)

<div class="concept-box" markdown="1">

**핵심**: Reflection은 **런타임에 클래스 메타정보를 읽고 조작**하는 메커니즘입니다. 컴파일 타임 정보 대신 실행 중에 클래스 구조를 분석합니다.

</div>

### 어떻게 쓰는지

#### Class 객체 얻기

```java
// 1️⃣ .class 리터럴
Class<?> clazz = String.class;

// 2️⃣ Class.forName() - 전체 클래스명으로 동적 로드
Class<?> clazz = Class.forName("java.lang.String");  // ClassNotFoundException 발생 가능

// 3️⃣ 객체에서 얻기
String str = "hello";
Class<?> clazz = str.getClass();
```

#### 클래스 메타정보 조회

```java
Class<?> clazz = String.class;

// 기본 정보
System.out.println(clazz.getName());        // java.lang.String
System.out.println(clazz.getSimpleName());  // String
System.out.println(clazz.getPackage());     // java.lang

// 상속 구조
Class<?> superclass = clazz.getSuperclass();  // Object
Class<?>[] interfaces = clazz.getInterfaces(); // Comparable, CharSequence, ...

// 생성자, 메서드, 필드 목록
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
Method[] methods = clazz.getDeclaredMethods();
Field[] fields = clazz.getDeclaredFields();
```

#### 메서드 동적 호출

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public void greet(String name) {
        System.out.println("Hello, " + name);
    }
}

// 메서드 찾기
Class<?> clazz = Calculator.class;
Method addMethod = clazz.getDeclaredMethod("add", int.class, int.class);
Method greetMethod = clazz.getDeclaredMethod("greet", String.class);

// 메서드 호출
Calculator calc = new Calculator();
Object result = addMethod.invoke(calc, 5, 3);  // 8
greetMethod.invoke(calc, "Alice");             // Hello, Alice

// private 메서드 접근
Method privateMethod = clazz.getDeclaredMethod("privateMethod");
privateMethod.setAccessible(true);  // 접근 제어 무시
privateMethod.invoke(calc);
```

#### 필드 동적 조작

```java
public class User {
    private String name;
    private int age;
    
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

// 필드 찾기
Class<?> clazz = User.class;
Field nameField = clazz.getDeclaredField("name");
Field ageField = clazz.getDeclaredField("age");

// private 필드 접근
nameField.setAccessible(true);
ageField.setAccessible(true);

// 값 읽기
User user = new User("Alice", 25);
String name = (String) nameField.get(user);  // "Alice"
int age = (int) ageField.get(user);          // 25

// 값 변경
nameField.set(user, "Bob");
ageField.set(user, 30);
```

#### 객체 동적 생성 (리플렉션)

```java
// 생성자 없이 호출
Class<?> clazz = String.class;

// String() 기본 생성자
String empty = (String) clazz.getDeclaredConstructor().newInstance();

// String(String) 생성자
Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
String str = (String) constructor.newInstance("hello");
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **프레임워크 (Spring, Hibernate)** | ✅ Reflection | 런타임에 클래스 스캔, 의존성 주입 |
| **테스트 (Mockito, JUnit)** | ✅ Reflection | private 메서드 테스트, 상태 검증 |
| **직렬화/역직렬화 (JSON, XML)** | ✅ Reflection | 객체 ↔ 데이터 변환 |
| **ORM (JPA, MyBatis)** | ✅ Reflection | 컬럼 ↔ 필드 매핑 |
| **동적 프록시, AOP** | ✅ Reflection | 런타임 메서드 가로채기 |
| **컴파일 타임에 정보를 아는 경우** | ❌ 일반 호출 | 성능과 타입 안전성 |

### 장점

| 장점 | 설명 |
|------|------|
| **유연성** | 런타임에 클래스 동적 로드, 메서드 호출 가능 |
| **프레임워크 구현** | 의존성 주입, AOP, 자동 매핑 등 고급 기능 가능 |
| **제네릭 타입 정보 접근** | 일반 제네릭은 컴파일 후 제거되지만 Reflection으로 일부 복구 가능 |
| **디버깅 편의** | 객체 상태, 클래스 구조를 동적으로 조사 가능 |

### 단점

| 단점 | 설명 |
|------|------|
| **성능 오버헤드** | 메서드 탐색, 객체 생성 비용 크짐 |
| **타입 안전성 상실** | 컴파일 타임 검사 없음, 런타임 에러 발생 가능 |
| **복잡성** | 코드 가독성 떨어짐, 디버깅 어려움 |
| **접근 제어 우회** | private 필드/메서드 접근 가능 (보안 위험) |
| **캡슐화 위반** | 내부 구현에 의존하면 메이저 버전 업그레이드 시 깨짐 |

### 특징

#### 1. 동적 로딩 vs 정적 로딩

```java
// 정적 로딩: 컴파일 타임에 결정
String str = new String("hello");

// 동적 로딩: 런타임에 결정
String className = "java.lang.String";
Class<?> clazz = Class.forName(className);  // 실행 중 클래스 선택
Object str = clazz.getDeclaredConstructor(String.class).newInstance("hello");
```

#### 2. Type Erasure와 Generic 정보

```java
// 컴파일 후 제네릭 정보 소실
List<String> list = new ArrayList<>();

// Reflection으로는?
Class<?> clazz = list.getClass();
Type[] types = list.getClass().getGenericInterfaces();  // List<String> 정보 부분적 복구 가능
```

#### 3. Constructor vs Field vs Method

```java
Class<?> clazz = User.class;

// Constructor: 객체 생성
Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, int.class);
User user = (User) constructor.newInstance("Alice", 25);

// Field: 상태 접근
Field nameField = clazz.getDeclaredField("name");
nameField.setAccessible(true);
String name = (String) nameField.get(user);

// Method: 행동 호출
Method greetMethod = clazz.getDeclaredMethod("greet");
greetMethod.invoke(user);
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ private 필드/메서드 무단 접근**

```java
// setAccessible(true)로 캡슐화 무시 가능
Field privateField = clazz.getDeclaredField("secret");
privateField.setAccessible(true);
privateField.set(obj, "해킹됨");

// 이는 보안 위험이고, 유지보수성 악화 (내부 구현 변경 시 깨짐)
```

**✅ 올바른 방식:**
- public API를 통해 접근
- 필요 시 프레임워크에 위임 (Spring, Hibernate)

</div>

<div class="danger-box" markdown="1">

**❌ 런타임 타입 에러 무시**

```java
// 메서드를 문자열로 지정하면 오류를 미리 알 수 없음
try {
    Method method = clazz.getDeclaredMethod("nonExistentMethod");  // NoSuchMethodException
    method.invoke(obj);
} catch (NoSuchMethodException e) {
    // 런타임에 터짐
}
```

**✅ 올바른 방식:**
- 프레임워크 사용 (Spring의 @Autowired 등)
- 필요시 검증 로직 추가

</div>

<div class="warning-box" markdown="1">

**⚠️ 성능 영향**

```java
// 반복문에서 메서드를 매번 탐색하면 성능 저하
for (int i = 0; i < 1000000; i++) {
    Method method = clazz.getDeclaredMethod("process");  // 매번 탐색 (비효율)
    method.invoke(obj);
}

// ✅ 메서드를 미리 탐색하고 재사용
Method method = clazz.getDeclaredMethod("process");
for (int i = 0; i < 1000000; i++) {
    method.invoke(obj);
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 타입 캐스팅 주의**

```java
// Reflection 결과는 Object이므로 수동 캐스팅 필요
Object result = method.invoke(obj, args);  // Object

// 잘못된 캐스팅
String value = (String) result;  // ClassCastException 발생 가능
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **용도** | 프레임워크, 테스트, 직렬화, ORM |
| **장점** | 런타임 유연성, 프레임워크 구현 가능 |
| **단점** | 성능 저하, 타입 안전성 상실, 복잡성 |
| **주의** | 캡슐화 위반, 성능, 타입 에러 |
| **권장** | 필요한 경우에만 사용, 프레임워크에 위임 |

---

**관련 파일:**
- [Class](Class.md) — Class 객체 상세
- [Annotation](annotation.md) — 런타임 메타데이터
