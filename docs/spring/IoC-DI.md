> [← 홈](/study/) · [Spring](/study/spring/basic/)

# IoC & DI (제어의 역전 & 의존성 주입)

---

## IoC (Inversion of Control, 제어의 역전)

### 전통적인 방식 (제어권이 클래스 내부에 있음)

```java
// 클라이언트가 직접 구현체를 생성하고 실행 흐름을 제어
public class OrderServiceImpl implements OrderService {
    // 직접 구현체 선택 및 생성
    private MemberRepository memberRepository = new MemoryMemberRepository();
    private DiscountPolicy discountPolicy = new FixDiscountPolicy();

    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discount);
    }
}
```

문제: `OrderServiceImpl`이 `MemoryMemberRepository`와 `FixDiscountPolicy`에 직접 의존.
나중에 `RateDiscountPolicy`로 교체하려면 **`OrderServiceImpl` 코드를 수정**해야 함.

### IoC의 개념

> **프로그램의 제어 흐름을 외부에서 관리**하는 것

```java
// AppConfig가 모든 구현체를 생성하고 연결 (제어권이 외부로 이동)
public class AppConfig {
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository(); // 구현체 결정
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy()); // 의존관계 주입
    }

    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy(); // 구현체 결정
    }
}
```

```java
// OrderServiceImpl은 이제 구현체를 모름 (인터페이스에만 의존)
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    // 생성자 파라미터로 받음 - 무슨 구현체인지 모름
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

이제 `OrderServiceImpl`은 단순히 실행만 한다. 어떤 구현체가 들어올지 **제어권이 AppConfig로 역전**되었다.

### 프레임워크 vs 라이브러리

| 구분 | 제어권 | 예 |
|---|---|---|
| 라이브러리 | 내 코드가 제어 (내가 호출) | Apache Commons, Guava |
| 프레임워크 | 프레임워크가 내 코드를 제어 (프레임워크가 호출) | Spring, JUnit |

JUnit 테스트: 내 코드(`@Test` 메서드)를 JUnit이 호출 → IoC 적용

---

## DI (Dependency Injection, 의존성 주입)

### 의존관계란?

```java
public class OrderServiceImpl {
    // OrderServiceImpl은 MemberRepository에 "의존"
    private MemberRepository memberRepository;
    // MemberRepository가 변경되면 OrderServiceImpl도 영향받을 수 있음
}
```

### 정적 의존관계

컴파일 시점에 결정되는 의존관계. 코드만 봐도 알 수 있다.

```java
public class OrderServiceImpl implements OrderService {
    private MemberRepository memberRepository; // MemberRepository 인터페이스에 의존 (정적)
    private DiscountPolicy discountPolicy;     // DiscountPolicy 인터페이스에 의존 (정적)
}
```

### 동적 의존관계 (DI)

런타임에 실제 구현 객체가 주입되어 결정되는 의존관계.

```
컴파일 시점: OrderServiceImpl → MemberRepository (인터페이스)
런타임:      OrderServiceImpl → MemoryMemberRepository (구현체 결정)
```

### DI의 효과

```java
// 기존 코드 변경 없이 구현체 교체 가능!
public class AppConfig {
    // MemoryMemberRepository → JdbcMemberRepository 교체
    // OrderServiceImpl 코드는 전혀 건드리지 않음
    public MemberRepository memberRepository() {
        return new JdbcMemberRepository(); // 한 줄만 변경
    }
}
```

---

## IoC 컨테이너의 역할

스프링 컨테이너(`ApplicationContext`)가 `AppConfig`의 역할을 대신한다.

```
[스프링 컨테이너 역할]
1. 객체 생성 (Bean 생성)
2. 의존관계 연결 (DI)
3. 객체 라이프사이클 관리 (초기화/소멸)
4. 싱글톤 보장
```

```java
// AppConfig를 스프링 컨테이너로
ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
OrderService orderService = ac.getBean("orderService", OrderService.class);
```

---

## DI 3가지 방법

### 1. 생성자 주입 (권장)

```java
@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired // 생성자가 하나면 @Autowired 생략 가능
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

**@RequiredArgsConstructor 활용** (Lombok):

```java
@Component
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자 자동 생성
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
}
```

### 2. Setter 주입

```java
@Component
public class OrderServiceImpl implements OrderService {

    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
}
```

### 3. 필드 주입

```java
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DiscountPolicy discountPolicy;
}
```

---

## 생성자 주입 권장 이유

### 비교 표

| 특징 | 생성자 주입 | Setter 주입 | 필드 주입 |
|---|---|---|---|
| 불변성 | O (`final` 가능) | X | X |
| 필수 의존관계 | O (생성 시점에 주입 강제) | X (선택적) | X |
| 테스트 편의성 | O (순수 자바 테스트 가능) | O | X (스프링 컨테이너 필요) |
| 순환 참조 감지 | O (컴파일 타임 또는 시작 시) | 런타임 | 런타임 |
| 코드 가독성 | O (명확) | 보통 | O (간결하지만 위험) |
| 권장 여부 | **강력 권장** | 선택적 의존관계에만 | 테스트 코드 외 비권장 |

### 생성자 주입이 좋은 이유 (상세)

#### 1. 불변성 보장

```java
// 생성자 주입 - final로 불변 보장
@Component
public class OrderServiceImpl {
    private final MemberRepository memberRepository; // final! 변경 불가

    public OrderServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    // setMemberRepository() 메서드 없음 → 주입 후 변경 불가
}

// Setter 주입 - 언제든 바뀔 수 있음
public void setMemberRepository(MemberRepository r) {
    this.memberRepository = r; // 나중에 누군가 호출해서 변경 가능
}
```

#### 2. 누락 감지

```java
// 생성자 주입 - 주입 안 하면 컴파일 오류!
OrderServiceImpl service = new OrderServiceImpl(); // 컴파일 오류

// 필드 주입 - NPE가 런타임에 발생
@Autowired
private MemberRepository memberRepository;
// 스프링 없이 테스트 시 memberRepository = null → NPE!
```

#### 3. 테스트 코드 작성 용이

```java
// 생성자 주입 - 스프링 없이 순수 자바 테스트 가능
@Test
void test() {
    // 직접 Mock 주입 가능
    MemberRepository mockRepo = new MemoryMemberRepository();
    DiscountPolicy mockPolicy = new FixDiscountPolicy();
    OrderServiceImpl service = new OrderServiceImpl(mockRepo, mockPolicy);

    // 테스트 진행
    Order order = service.createOrder(1L, "item", 10000);
    assertThat(order.getDiscountPrice()).isEqualTo(1000);
}
```

#### 4. 순환 참조 방지

```java
// 순환 참조: A → B → A
@Component
public class A {
    @Autowired B b;
}
@Component
public class B {
    @Autowired A a;
}

// 생성자 주입 시: 애플리케이션 시작 시점에 즉시 감지되어 오류 발생
// 필드/Setter 주입 시: 런타임에 실제 메서드 호출 시 StackOverflowError 발생
```

### 언제 Setter 주입을 쓰는가?

```java
// 선택적 의존관계 (없어도 동작해야 하는 경우)
@Component
public class SomeService {
    private MailSender mailSender; // 없어도 동작

    @Autowired(required = false) // 선택적
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
}
```

> **원칙**: 생성자 주입을 기본으로 사용하고, 선택적 의존관계에는 `@Autowired(required=false)`와 Setter 주입 고려.
> 필드 주입은 테스트 코드(`@SpringBootTest` 환경)에서만 제한적으로 사용.
