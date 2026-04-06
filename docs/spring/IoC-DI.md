# IoC & DI (제어의 역전 & 의존성 주입)

---

## 문제: 객체를 직접 생성하면 생기는 일

자바로 주문 시스템을 만든다고 하자. 처음에는 이렇게 작성한다.

```markdown
public class OrderServiceImpl implements OrderService {
    // 직접 구현체를 생성 — "MemoryMemberRepository를 쓴다"고 못 박음
    private MemberRepository memberRepository = new MemoryMemberRepository();
    private DiscountPolicy discountPolicy = new FixDiscountPolicy();

    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discount);
    }
}
```

나중에 "정액 할인을 정률 할인으로 바꿔주세요"라는 요청이 온다.

```markdown
// OrderServiceImpl을 직접 수정해야 한다
private DiscountPolicy discountPolicy = new RateDiscountPolicy(); // 변경!
```

`OrderServiceImpl`은 할인 정책의 **변경을 알 필요가 없는데**, 코드를 건드려야 한다.
이게 OCP 위반, DIP 위반이다.

---

## IoC (Inversion of Control, 제어의 역전)

### 개념

> **객체를 누가 만들고, 어떤 의존관계로 연결할지에 대한 제어권을 외부로 넘기는 것.**

기존 방식은 `OrderServiceImpl`이 자신이 쓸 객체를 직접 생성하고 결정했다.
IoC를 적용하면 이 결정권이 외부(`AppConfig` 또는 Spring)로 넘어간다.

```markdown
// IoC 적용 — AppConfig가 모든 구현체를 생성하고 연결
public class AppConfig {

    public MemberRepository memberRepository() {
        return new MemoryMemberRepository(); // 구현체는 여기서 결정
    }

    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy(); // 구현체는 여기서 결정
    }

    public OrderService orderService() {
        // OrderServiceImpl을 만들면서 의존관계를 연결해줌
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
}
```

```markdown
// OrderServiceImpl은 이제 구현체를 모른다
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    // 누가 어떤 구현체를 줄지 모름 — 그냥 받기만 함
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

이제 할인 정책을 바꾸고 싶으면 `AppConfig`만 수정하면 된다.
`OrderServiceImpl`은 전혀 건드리지 않아도 된다.

### 프레임워크 vs 라이브러리

IoC 개념이 적용된 것이 프레임워크다.

| 구분 | 제어권 | 예 |
|---|---|---|
| 라이브러리 | 내 코드가 라이브러리를 호출 | Apache Commons, Jackson |
| 프레임워크 | 프레임워크가 내 코드를 호출 | Spring, JUnit |

JUnit 테스트를 작성할 때 `@Test` 메서드를 직접 호출하지 않는다. JUnit이 알아서 찾아서 호출해준다. 이것이 IoC다.

---

## DI (Dependency Injection, 의존성 주입)

### 의존관계란?

```markdown
public class OrderServiceImpl {
    private MemberRepository memberRepository; // 이 타입에 "의존"
}
```

`OrderServiceImpl`이 `MemberRepository`를 사용한다. 즉 `MemberRepository`가 변경되면 `OrderServiceImpl`도 영향을 받을 수 있다. 이를 **의존관계**라 한다.

### 정적 의존관계 vs 동적 의존관계

**정적 의존관계**는 코드만 보면 알 수 있는 의존관계다. 컴파일 시점에 결정된다.

```markdown
public class OrderServiceImpl {
    private MemberRepository memberRepository;  // MemberRepository 인터페이스에 의존 (정적)
    private DiscountPolicy discountPolicy;      // DiscountPolicy 인터페이스에 의존 (정적)
    // 실제로 어떤 구현체가 들어올지는 코드만 봐서는 모름
}
```

**동적 의존관계**는 런타임에 실제 구현체가 결정되는 의존관계다.

```markdown
컴파일 시점: OrderServiceImpl → MemberRepository (인터페이스)
런타임:      OrderServiceImpl → MemoryMemberRepository (구현체가 주입됨)
```

DI는 이 동적 의존관계를 외부에서 주입해주는 것이다.

### DI의 효과

구현체를 교체하고 싶을 때 `OrderServiceImpl`은 건드리지 않아도 된다.

```markdown
// 기존 코드 변경 없이 구현체 교체
public class AppConfig {
    public MemberRepository memberRepository() {
        // return new MemoryMemberRepository(); // 메모리 → JDBC로 교체
        return new JdbcMemberRepository();      // 이 한 줄만 바꾸면 됨
    }
}
// OrderServiceImpl 코드는 전혀 수정하지 않아도 됨
```

---

## IoC 컨테이너 = 스프링 컨테이너

`AppConfig`를 직접 만드는 방식도 IoC지만, Spring이 이 역할을 대신해준다.
`ApplicationContext`가 `AppConfig`의 역할을 맡는다.

```markdown
// AppConfig → 스프링 컨테이너로 전환
ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

// 스프링이 DI를 처리한 빈을 꺼내 쓰면 됨
OrderService orderService = ac.getBean("orderService", OrderService.class);
```

스프링 컨테이너가 해주는 일:

```
1. 빈(객체) 생성
2. 의존관계 연결 (DI)
3. 싱글톤 보장
4. 빈 생명주기 관리 (초기화/소멸)
```

---

## DI 3가지 방법

### 1. 생성자 주입 (권장)

```markdown
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

**Lombok의 @RequiredArgsConstructor**를 쓰면 더 간결하다.

```markdown
@Component
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 자동 생성해줌
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;  // final → 불변 보장
    private final DiscountPolicy discountPolicy;
    // 생성자 코드가 사라짐 — Lombok이 만들어줌
}
```

### 2. Setter 주입

```markdown
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

선택적 의존관계(없어도 동작해야 하는 경우)에만 사용한다.

### 3. 필드 주입

```markdown
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired private MemberRepository memberRepository;
    @Autowired private DiscountPolicy discountPolicy;
}
```

코드가 가장 짧지만, 스프링 컨테이너 없이는 테스트할 수 없어서 **비권장**이다.
테스트 코드에서만 제한적으로 사용한다.

---

## 생성자 주입을 권장하는 이유

### 1. final로 불변 보장

```markdown
@Component
public class OrderServiceImpl {
    private final MemberRepository memberRepository; // final → 생성 후 변경 불가

    public OrderServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    // set 메서드가 없으므로 외부에서 바꿀 수 없음
}
```

`final` 키워드 덕분에 주입 후에 의존관계가 바뀌지 않는다.
Setter 주입은 `final`을 쓸 수 없어서 누군가 나중에 바꿀 수 있다.

### 2. 주입 누락을 컴파일 타임에 발견

```markdown
// 생성자 주입 — 주입 안 하면 컴파일 에러
OrderServiceImpl service = new OrderServiceImpl(); // 에러: 파라미터 빠짐

// 필드 주입 — 주입 안 해도 컴파일은 됨, 런타임에 NPE 발생
@Autowired
private MemberRepository memberRepository;
// 스프링 없이 직접 new OrderServiceImpl() 하면 memberRepository = null → NPE
```

### 3. 순수 자바로 테스트 가능

```markdown
// 생성자 주입 — 스프링 없이 테스트 가능
@Test
void test() {
    MemberRepository mockRepo = new MemoryMemberRepository();    // 직접 구현체 넣기
    DiscountPolicy mockPolicy = new FixDiscountPolicy();
    OrderServiceImpl service = new OrderServiceImpl(mockRepo, mockPolicy);

    Order order = service.createOrder(1L, "item", 10000);
    assertThat(order.getDiscountPrice()).isEqualTo(1000);
}

// 필드 주입 — 스프링 컨테이너가 있어야만 주입됨 → 테스트가 무거워짐
```

### 4. 순환 참조 조기 발견

```markdown
// A → B → A 순환 참조 상황
@Component
public class A {
    public A(B b) { ... } // B에 의존
}
@Component
public class B {
    public B(A a) { ... } // A에 의존 → 순환!
}
// 생성자 주입: 애플리케이션 시작 시점에 즉시 오류 → 빠르게 발견
// 필드/Setter 주입: 실제 메서드 호출 시점에 StackOverflowError → 늦게 발견
```

### 비교 요약

| 특징 | 생성자 주입 | Setter 주입 | 필드 주입 |
|---|---|---|---|
| `final` 사용 | O | X | X |
| 컴파일 타임 누락 감지 | O | X | X |
| 스프링 없이 테스트 | O | O | X |
| 순환 참조 조기 감지 | O | 런타임 | 런타임 |
| 권장 여부 | **강력 권장** | 선택적 의존관계에만 | 테스트 코드 외 비권장 |

### 언제 Setter 주입을 쓰는가?

```markdown
// 없어도 동작해야 하는 의존관계 (선택적)
@Component
public class NotificationService {
    private MailSender mailSender; // 메일 서버가 없어도 앱은 돌아가야 함

    @Autowired(required = false) // 빈이 없으면 이 메서드 자체가 호출 안 됨
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
}
```

> **원칙**: 기본은 생성자 주입. 선택적 의존관계만 `@Autowired(required=false)` Setter 주입 고려.
