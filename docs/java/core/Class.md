## Class 객체 (java.lang.Class)

### 왜 쓰는지

**모든 클래스는 JVM이 로드할 때 Class 객체로 관리**됩니다. Class 객체를 통해:
- 런타임에 클래스 정보 조회 가능
- 프레임워크가 자동으로 객체 생성 및 의존성 주입 (Spring DI)
- 동적으로 메서드 호출, 필드 접근 (Reflection)

<div class="concept-box" markdown="1">

**핵심**: Class는 **JVM이 로드한 클래스의 메타정보를 담는 객체**입니다. Reflection의 기반이며, 프레임워크의 핵심입니다.

</div>

### 어떻게 쓰는지

#### Class 객체 얻기

```java
// 1️⃣ .class 리터럴 (컴파일 타임에 알아야 함)
Class<String> stringClass = String.class;

// 2️⃣ getClass() (인스턴스에서)
String str = "hello";
Class<?> clazz = str.getClass();  // String.class

// 3️⃣ Class.forName() (문자열로 동적 로드)
Class<?> clazz = Class.forName("java.lang.String");
Class<?> userClass = Class.forName("com.example.User");  // 전체 경로 필요
```

#### 메타정보 조회

```java
Class<?> clazz = String.class;

// 클래스 이름
System.out.println(clazz.getName());        // java.lang.String
System.out.println(clazz.getSimpleName());  // String

// 패키지
System.out.println(clazz.getPackage());     // package java.lang

// 상속 구조
Class<?> superclass = clazz.getSuperclass();  // Object
Class<?>[] interfaces = clazz.getInterfaces(); // Comparable, CharSequence, ...

// 생성자, 메서드, 필드
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
Method[] methods = clazz.getDeclaredMethods();
Field[] fields = clazz.getDeclaredFields();

// 어노테이션
Annotation[] annotations = clazz.getDeclaredAnnotations();

// 수정자 (public, final, abstract, ...)
int modifiers = clazz.getModifiers();
System.out.println(Modifier.isPublic(modifiers));  // true/false
```

#### Reflection: 동적 메서드 호출

```java
Class<?> clazz = String.class;

// 메서드 찾기
Method lengthMethod = clazz.getDeclaredMethod("length");

// 메서드 호출
String str = "hello";
int result = (int) lengthMethod.invoke(str);
System.out.println(result);  // 5

// 파라미터 있는 메서드
Method substringMethod = clazz.getDeclaredMethod("substring", int.class, int.class);
String substring = (String) substringMethod.invoke(str, 0, 2);
System.out.println(substring);  // "he"
```

#### Reflection: 필드 접근

```java
public class User {
    private String name;
    private int age;
}

Class<?> clazz = User.class;

// private 필드 접근
Field nameField = clazz.getDeclaredField("name");
nameField.setAccessible(true);  // 접근 제어 무시

User user = new User();

// 값 읽기
String name = (String) nameField.get(user);

// 값 쓰기
nameField.set(user, "Alice");
```

#### 객체 동적 생성

```java
Class<?> clazz = Class.forName("com.example.User");

// 생성자 없이 기본 생성자로 생성
User user1 = (User) clazz.getDeclaredConstructor().newInstance();

// 생성자로 생성
Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, int.class);
User user2 = (User) constructor.newInstance("Alice", 25);
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **프레임워크 (Spring, JPA)** | ✅ Class | 런타임에 클래스 스캔, 객체 생성 |
| **테스트 도구 (JUnit, Mockito)** | ✅ Class | 테스트 메서드 자동 발견 |
| **JSON/XML 마샬링** | ✅ Class | 필드 자동 매핑 |
| **플러그인 시스템** | ✅ Class | 동적 클래스 로드 |
| **일반 코드** | ❌ 타입 안전 | 컴파일 타임 결정이 낫음 |

### 장점

| 장점 | 설명 |
|------|------|
| **동적 로드** | 런타임에 클래스 로드 가능 |
| **메타정보 조회** | 클래스 구조를 프로그래밍적으로 분석 |
| **프레임워크 기반** | Spring DI, JPA 등의 핵심 기술 |
| **플러그인 시스템** | JAR 추가하면 자동 로드 |

### 단점

| 단점 | 설명 |
|------|------|
| **성능** | 동적 로드, 메서드 호출이 일반 방식보다 느림 |
| **복잡성** | Reflection 사용하면 코드 이해 어려움 |
| **타입 안전성** | 컴파일 타임 검사 불가능 |
| **캡슐화 파괴** | private 접근 가능하므로 보안 위험 |

### 특징

#### 1. 클래스 로딩 프로세스

```text
.class 파일
    ↓
ClassLoader (부트스트랩, 확장, 애플리케이션)
    ↓
JVM 메모리의 메타정보 영역 (Method Area)
    ↓
java.lang.Class 객체 생성
    ↓
Reflection으로 접근 가능
```

#### 2. Class 객체는 싱글톤

```java
Class<?> clazz1 = String.class;
Class<?> clazz2 = String.class;
Class<?> clazz3 = Class.forName("java.lang.String");

System.out.println(clazz1 == clazz2);  // true (같은 객체)
System.out.println(clazz1 == clazz3);  // true (같은 객체)
```

#### 3. 제네릭과 Class

```java
// 제네릭 정보는 컴파일 후 제거됨 (Type Erasure)
List<String> list = new ArrayList<>();
Class<?> clazz = list.getClass();
System.out.println(clazz);  // java.util.ArrayList (제네릭 정보 없음)

// Type Erasure 이후 런타임 Class 같음
ArrayList<String> list1 = new ArrayList<>();
ArrayList<Integer> list2 = new ArrayList<>();
System.out.println(list1.getClass() == list2.getClass());  // true
```

#### 4. isAssignableFrom: 타입 호환성 검사

```java
Class<?> stringClass = String.class;
Class<?> objectClass = Object.class;
Class<?> integerClass = Integer.class;

// String은 Object에 할당 가능?
System.out.println(objectClass.isAssignableFrom(stringClass));  // true

// Integer는 String에 할당 가능?
System.out.println(stringClass.isAssignableFrom(integerClass)); // false
```

#### 5. Reflection을 사용하는 라이브러리

```java
// Spring의 의존성 주입
@Component
public class UserService {
    @Autowired
    private UserRepository repository;  // 런타임에 주입
}

// Jackson의 JSON 변환
ObjectMapper mapper = new ObjectMapper();
User user = mapper.readValue(json, User.class);  // Class 객체로 타입 결정

// JPA의 엔티티 매핑
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
    
    @Column(name = "user_name")
    private String name;  // 필드와 컬럼 자동 매핑
}
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ ClassNotFoundException**

```java
try {
    Class<?> clazz = Class.forName("com.example.User");
} catch (ClassNotFoundException e) {
    // 클래스 경로에 없음
}

// 클래스 이름 오타, 패키지명 잘못 입력 주의
```

</div>

<div class="danger-box" markdown="1">

**❌ Reflection으로 private 접근**

```java
// private 메서드/필드에 setAccessible(true)로 접근 가능
Field privateField = clazz.getDeclaredField("secret");
privateField.setAccessible(true);
String value = (String) privateField.get(obj);

// 캡슐화 파괴, 보안 위험!
```

**✅ 필요시 프레임워크에 위임:**
```java
// Spring, Jackson 등 검증된 라이브러리 사용
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 성능 영향**

```java
// ❌ 반복문에서 메서드를 매번 탐색
for (int i = 0; i < 1000000; i++) {
    Method method = clazz.getDeclaredMethod("process");
    method.invoke(obj);
}

// ✅ 메서드를 미리 캐싱
Method method = clazz.getDeclaredMethod("process");
for (int i = 0; i < 1000000; i++) {
    method.invoke(obj);
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 타입 안전성 상실**

```java
// ❌ 컴파일 타임에 오류 감지 불가능
Method method = clazz.getDeclaredMethod("nonExistent");  // 런타임 NoSuchMethodException
method.invoke(obj, "wrong", "args");  // 런타임 IllegalArgumentException
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **Class 객체** | 런타임 클래스 메타정보 |
| **얻는 방법** | .class, getClass(), Class.forName() |
| **메타정보** | 필드, 메서드, 생성자, 어노테이션 |
| **Reflection** | 동적으로 메서드/필드 접근 |
| **사용처** | 프레임워크(Spring, JPA), 테스트 도구 |
| **주의** | 성능, 타입 안전성, 보안 |

---

**관련 파일:**
- [Reflection](../io-network/reflection.md) — Reflection 상세
- [Annotation](../io-network/annotation.md) — 메타데이터
