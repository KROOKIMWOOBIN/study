## Annotation (어노테이션)

### 왜 쓰는지

Java 코드는 **코드 자체(비즈니스 로직)와 메타정보(설정, 검증, 마크)**를 섞어서 작성합니다. 메타정보를:
- 컴파일러에게 전달 (오류 체크)
- 빌드 도구에게 전달 (코드 생성)
- 런타임 프레임워크에게 전달 (자동 설정)

이를 명확하고 체계적으로 하기 위해 Annotation이 필요합니다.

<div class="concept-box" markdown="1">

**핵심**: Annotation은 **코드에 메타데이터를 붙이는 문법**입니다. 클래스, 메서드, 필드에 정보를 추가하면, 컴파일러·프레임워크·런타임이 이를 읽고 처리합니다.

</div>

### 어떻게 쓰는지

#### 기본 Annotation (Java 내장)

```java
// 1️⃣ @Override: 부모 메서드를 재정의했음을 선언
class Parent {
    public void greet() {}
}

class Child extends Parent {
    @Override
    public void greet() {  // 컴파일러가 부모에 greet 메서드 있는지 검사
        System.out.println("Hello");
    }
}

// 2️⃣ @Deprecated: 더 이상 사용하지 말 것을 알림
class OldApi {
    @Deprecated(since = "2.0", forRemoval = true)
    public void oldMethod() {}  // 경고: 대체 메서드를 사용하세요
}

// 3️⃣ @SuppressWarnings: 컴파일 경고 무시
@SuppressWarnings("unchecked")
List list = new ArrayList();  // 경고 메시지 표시 안 함

// 4️⃣ @FunctionalInterface: 함수형 인터페이스임을 보증
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}
```

#### 커스텀 Annotation 정의

```java
// 메타 어노테이션: Annotation을 정의하는 데 사용
import java.lang.annotation.*;

@Target(ElementType.METHOD)           // 어디에 붙일지
@Retention(RetentionPolicy.RUNTIME)   // 언제까지 유지할지
@Documented                           // JavaDoc에 포함
public @interface MyAnnotation {
    String value();                   // 필수 속성
    int count() default 1;            // 기본값 있는 속성
    String[] tags() default {};       // 배열 속성
}

// 사용
class MyClass {
    @MyAnnotation(value = "test", count = 5, tags = {"a", "b"})
    public void myMethod() {}
}
```

#### Spring의 Annotation 예시

```java
// 컨테이너에 빈 등록
@Component
@Service
@Repository
@Controller
public class UserService {
    
    // 의존성 자동 주입
    @Autowired
    private UserRepository repo;
    
    // 메서드 레벨 Annotation
    @Transactional  // 트랜잭션 자동 관리
    public void saveUser(User user) {
        repo.save(user);
    }
    
    // 유효성 검증
    public void register(@NotNull String name, @Email String email) {}
}

// 설정 클래스
@Configuration
@EnableTransactionManagement
public class AppConfig {
    
    @Bean
    public DataSource dataSource() { ... }
}
```

#### Reflection으로 Annotation 읽기

```java
class MyClass {
    @MyAnnotation(value = "test")
    public void myMethod() {}
}

// Reflection으로 Annotation 정보 읽기
Method method = MyClass.class.getDeclaredMethod("myMethod");

// Annotation 존재 여부 확인
if (method.isAnnotationPresent(MyAnnotation.class)) {
    // Annotation 객체 얻기
    MyAnnotation anno = method.getAnnotation(MyAnnotation.class);
    System.out.println(anno.value());  // "test"
}

// 모든 Annotation 조회
Annotation[] annotations = method.getDeclaredAnnotations();
for (Annotation anno : annotations) {
    System.out.println(anno.annotationType().getName());
}
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **프레임워크 설정** | ✅ Annotation | 설정을 코드에 가깝게, 자동 스캔 |
| **컴파일 타임 검사** | ✅ @Override 등 | 실수 방지 |
| **메타데이터 관리** | ✅ Annotation | 코드와 설정 통합 |
| **테스트 마킹** | ✅ @Test, @Before | 테스트 프레임워크 인식 |
| **유효성 검증** | ✅ @NotNull, @Email | 런타임 검증 |
| **과도한 마킹** | ❌ 코드가 지저분함 | 가독성 떨어짐 |

### 장점

| 장점 | 설명 |
|------|------|
| **코드 간결성** | XML 설정 파일 제거, 설정을 코드에 통합 |
| **자동화** | 프레임워크가 자동으로 처리 (DI, 트랜잭션 등) |
| **타입 안전성** | Annotation 속성도 타입 체크 가능 |
| **메타정보 보관** | 클래스, 메서드의 의도를 명확하게 표현 |
| **Reflection과 결합** | 런타임에 유연한 처리 가능 |

### 단점

| 단점 | 설명 |
|------|------|
| **학습곡선** | Annotation 개념과 메타 어노테이션 이해 필요 |
| **마법(Magic)** | Annotation이 자동으로 처리되면 동작 추적 어려움 |
| **과다 사용** | 코드가 Annotation으로 지저분해질 수 있음 |
| **오류 메시지** | 문제 발생 시 Annotation 관련 오류가 명확하지 않음 |
| **성능** | Reflection으로 Annotation 읽으면 성능 비용 발생 |

### 특징

#### 1. 메타 어노테이션 (Annotation을 정의하는 Annotation)

```java
@Target(ElementType.METHOD)           // 적용 대상
@Retention(RetentionPolicy.RUNTIME)   // 생명주기
@Documented                           // JavaDoc 포함
@Inherited                            // 상속 여부
public @interface MyAnnotation {}
```

| 메타 어노테이션 | 설명 |
|----------------|------|
| `@Target` | 어디에 붙을 수 있는가 (TYPE, METHOD, FIELD, PARAMETER, ...) |
| `@Retention` | 언제까지 유지되는가 (SOURCE, CLASS, RUNTIME) |
| `@Documented` | JavaDoc에 포함되는가 |
| `@Inherited` | 상속되는가 |
| `@Repeatable` | 같은 대상에 여러 번 붙을 수 있는가 |

#### 2. @Retention 레벨별 동작

```java
// 1️⃣ SOURCE: 컴파일 시 삭제됨
@Retention(RetentionPolicy.SOURCE)
@Override   // 컴파일러만 확인, 컴파일 후 사라짐

// 2️⃣ CLASS: Class 파일까지만 유지
@Retention(RetentionPolicy.CLASS)
public @interface BuildTime {}  // 빌드 도구가 읽음, 런타임에는 없음

// 3️⃣ RUNTIME: 런타임에도 유지
@Retention(RetentionPolicy.RUNTIME)
@Service    // 프레임워크가 런타임에 Reflection으로 읽음
```

#### 3. Spring의 Annotation 처리 프로세스

```text
@Bean 달린 메서드
    ↓ (Spring ApplicationContext가 Reflection으로 스캔)
ClassPathBeanDefinitionScanner
    ↓ (메타데이터 수집)
BeanDefinition 생성
    ↓ (Bean 인스턴스 생성 & 의존성 주입)
@Autowired 붙은 필드에 주입
```

#### 4. 주요 Java/Jakarta EE Annotation

| Annotation | 용도 |
|-----------|------|
| `@Override` | 부모 메서드 재정의 |
| `@Deprecated` | 더 이상 사용 금지 |
| `@FunctionalInterface` | 함수형 인터페이스 보증 |
| `@SuppressWarnings` | 경고 무시 |
| `@SafeVarargs` | 가변인수 경고 무시 |

### 주의할 점

<div class="danger-box" markdown="1">

**❌ Annotation은 동작을 수행하지 않음**

```java
@MyAnnotation  // 이것만으로는 아무것도 안 함
public class MyClass {}

// Annotation 처리 코드가 필요
public class AnnotationProcessor {
    public static void process(Class<?> clazz) {
        if (clazz.isAnnotationPresent(MyAnnotation.class)) {
            // 실제 동작
            System.out.println("처리됨");
        }
    }
}
```

**✅ 올바른 방식:**
- 프레임워크가 처리하는 표준 Annotation 사용 (Spring, JPA)
- 커스텀 Annotation은 처리 코드와 함께

</div>

<div class="warning-box" markdown="1">

**⚠️ @Retention(RUNTIME) 주의**

```java
// RUNTIME으로 설정한 Annotation은 메모리에 계속 유지
@Retention(RetentionPolicy.RUNTIME)
public @interface Heavy {}

// 많은 Annotation이 RUNTIME이면 메모리 누적
// 필요한 경우만 RUNTIME 사용
```

</div>

<div class="warning-box" markdown="1">

**⚠️ Annotation 값은 상수만 가능**

```java
// ❌ 불가능: 변수, 메서드 호출 불가
public @interface Bad {
    String value = new String("test");  // 런타임 값 불가
    int count = Math.max(1, 2);         // 메서드 호출 불가
}

// ✅ 가능: 리터럴, 상수만
public @interface Good {
    String value() default "test";
    int count() default 1;
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ @Inherited는 메서드에 상속되지 않음**

```java
@Inherited
@interface MyAnnotation {}

@MyAnnotation
class Parent {}

class Child extends Parent {}

// Child는 MyAnnotation을 상속받음
// 하지만 Parent.method()를 override한 Child.method()는 annotation을 상속받지 않음
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **Annotation** | 메타데이터를 코드에 붙이는 문법 |
| **메타 어노테이션** | Annotation을 정의하는 @Target, @Retention 등 |
| **@Retention** | SOURCE(컴파일만), CLASS(빌드), RUNTIME(프레임워크) |
| **용도** | 프레임워크 설정, 컴파일 검사, 런타임 처리 |
| **주의** | Annotation 자체는 동작 안 함, 처리 코드 필요 |
| **권장** | 표준 Annotation(Spring, JPA) 사용 |

---

**관련 파일:**
- [Reflection](reflection.md) — 런타임에 Annotation 읽는 방법
- [Lambda](../lambda/function.md) — @FunctionalInterface와 함께 사용
