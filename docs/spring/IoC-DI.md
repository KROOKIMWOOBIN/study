# IoC & DI (제어의 역전 & 의존성 주입)

---

## 문제: 객체를 직접 생성하면 생기는 일

자바로 주문 시스템을 만든다고 하자. 처음에는 이렇게 작성하는 게 자연스럽다.

```java
public class OrderServiceImpl implements OrderService {
    // "나는 MemoryMemberRepository를 쓸 것이다"고 못 박음
    private MemberRepository memberRepository = new MemoryMemberRepository();
    // "나는 FixDiscountPolicy를 쓸 것이다"고 못 박음
    private DiscountPolicy discountPolicy = new FixDiscountPolicy();

    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discount);
    }
}
```

문제가 없어 보인다. 그런데 요구사항이 바뀐다.

> "정액 할인(고정 1000원)을 정률 할인(10%)으로 바꿔주세요."

```java
public class OrderServiceImpl implements OrderService {
    private MemberRepository memberRepository = new MemoryMemberRepository();
    // 이 줄을 수정해야 한다
    // private DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private DiscountPolicy discountPolicy = new RateDiscountPolicy(); // 변경!
}
```

`OrderServiceImpl`의 역할은 주문 처리다. 할인 정책이 어떻게 되는지는 관심 없어야 한다.
그런데 할인 정책이 바뀌었다는 이유로 `OrderServiceImpl` 코드를 건드려야 한다.

<div class="warning-box" markdown="1">

**이것이 OCP 위반, DIP 위반이다.**

`OrderServiceImpl`이 인터페이스(`DiscountPolicy`)에 의존하는 척하지만, 실제로는 구현체(`FixDiscountPolicy`)까지 직접 `new`로 생성하고 있다. 인터페이스에만 의존한다는 말이 무색하다.

</div>

---

## 해결: 관심사 분리

역할을 분리한다.

- `OrderServiceImpl` → 주문 처리만 담당. 어떤 구현체를 쓸지는 모른다.
- ==AppConfig== → 구현체를 선택하고 조립하는 역할만 담당.

```java
// AppConfig: 구현체를 선택하고, 객체를 생성하고, 연결한다
public class AppConfig {

    public MemberRepository memberRepository() {
        return new MemoryMemberRepository(); // 어떤 구현체를 쓸지는 여기서만 결정
    }

    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy(); // 어떤 구현체를 쓸지는 여기서만 결정
    }

    public OrderService orderService() {
        // OrderServiceImpl을 만들면서 의존관계를 연결해준다
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
}
```

```java
// OrderServiceImpl: 오직 주문 처리만. 구현체를 모른다.
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    // 어떤 구현체가 올지 모름 — 그냥 받기만 함
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discount);
    }
}
```

이제 할인 정책을 바꾸고 싶으면 `AppConfig`만 수정하면 된다.
`OrderServiceImpl`은 건드리지 않는다. OCP 준수, DIP 준수.

```text
AppConfig.discountPolicy() → return new FixDiscountPolicy();  ← 이 한 줄만 바꾸면 됨
OrderServiceImpl             → 코드 변경 없음 ✓
```

---

## IoC (Inversion of Control, 제어의 역전)

### 개념

<div class="concept-box" markdown="1">

**객체를 누가 만들고, 어떤 의존관계로 연결할지에 대한 제어권을 외부로 넘기는 것.**

프로그램의 흐름을 직접 제어하는 것이 아니라, 외부에서 관리하는 것을 ==제어의 역전(IoC)==이라 한다.

</div>

원래 방식:
```text
OrderServiceImpl이 직접 결정 → new MemoryMemberRepository()
OrderServiceImpl이 직접 결정 → new FixDiscountPolicy()
```

IoC 적용 후:
```text
AppConfig가 결정 → OrderServiceImpl에게 어떤 구현체를 줄지 AppConfig가 정함
OrderServiceImpl은 받기만 함 — 자신이 어떤 구현체를 사용하는지도 모름
```

### 프레임워크 vs 라이브러리

IoC가 적용되었느냐 아니냐가 프레임워크와 라이브러리의 차이다.

| 구분 | 제어권 | 설명 | 예 |
|---|---|---|---|
| 라이브러리 | 내 코드 | 내 코드가 라이브러리를 호출 | Jackson, Apache Commons |
| 프레임워크 | 프레임워크 | 프레임워크가 내 코드를 호출 | Spring, JUnit |

JUnit 테스트를 작성할 때 `@Test`가 붙은 메서드를 내가 직접 호출하지 않는다.
JUnit이 알아서 찾아서 호출해준다. 이것이 IoC다.

Spring도 마찬가지다. `@Controller`의 메서드를 직접 호출하지 않는다.
HTTP 요청이 오면 Spring이 알아서 해당 메서드를 찾아서 호출한다.

---

## DI (Dependency Injection, 의존성 주입)

### 의존관계란?

```java
public class OrderServiceImpl {
    private MemberRepository memberRepository; // MemberRepository에 "의존"한다
}
```

`OrderServiceImpl`이 `MemberRepository`를 사용한다.
`MemberRepository`가 변경되면 `OrderServiceImpl`도 영향을 받을 수 있다.
이것을 **의존관계**라 한다.

### 정적 의존관계 vs 동적 의존관계

**정적 의존관계**: 코드만 보면 알 수 있다. 컴파일 시점에 확정된다.

```java
public class OrderServiceImpl {
    private MemberRepository memberRepository;  // MemberRepository 인터페이스에 의존 (정적)
    private DiscountPolicy discountPolicy;       // DiscountPolicy 인터페이스에 의존 (정적)
    // 실제로 어떤 구현체가 들어오는지는 코드만 봐서는 알 수 없음
}
```

**동적 의존관계**: 런타임에 실제 구현체가 결정된다. 앱이 실행되어야 알 수 있다.

```text
컴파일 시점:  OrderServiceImpl → MemberRepository (인터페이스)
런타임:       OrderServiceImpl → MemoryMemberRepository (실제 구현체가 주입됨)
```

DI는 이 **동적 의존관계**를 외부에서 결정하고 주입해주는 것이다.

### DI의 효과

구현체를 교체해도 `OrderServiceImpl`은 건드리지 않는다.

```java
// 기존 코드 변경 없이 구현체 교체
public class AppConfig {
    public MemberRepository memberRepository() {
        // return new MemoryMemberRepository(); // 메모리 DB → JDBC로 교체
        return new JdbcMemberRepository();      // 이 한 줄만 바꾸면 됨
    }
}
// OrderServiceImpl 코드는 전혀 수정하지 않아도 됨
```

---

## IoC 컨테이너 = 스프링 컨테이너

`AppConfig`를 직접 만드는 방식도 IoC지만, Spring이 이 역할을 대신 수행해준다.
`ApplicationContext`가 `AppConfig`의 역할을 맡는 Spring의 IoC 컨테이너다.

```java
// AppConfig → 스프링 컨테이너로 전환
// AppConfig에 정의된 @Bean들을 모두 등록하고 DI를 처리해준다
ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

// 스프링이 DI를 처리한 빈을 꺼내 쓰면 됨
OrderService orderService = ac.getBean("orderService", OrderService.class);
```

스프링 컨테이너가 해주는 일:

```text
1. 빈(Bean) 생성 — @Bean이 붙은 메서드를 호출하여 객체 생성
2. 의존관계 연결 — 생성된 빈들 사이의 의존관계를 연결(DI)
3. 싱글톤 보장  — 같은 빈을 여러 번 요청해도 같은 인스턴스 반환
4. 생명주기 관리 — 초기화/소멸 콜백 호출
```

---

## DI 3가지 방법

### 1. 생성자 주입 (강력 권장)

```java
@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired // 생성자가 딱 하나면 @Autowired 생략 가능
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

실무에서는 Lombok의 `@RequiredArgsConstructor`로 더 간결하게 쓴다.

```java
@Component
@RequiredArgsConstructor // final 필드를 파라미터로 받는 생성자를 자동 생성
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    // 생성자 코드가 사라짐 — Lombok이 만들어줌
    // @Autowired도 생략됨 (Lombok이 생성한 생성자는 생성자가 하나이므로 자동 적용)
}
```

---

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

**선택적 의존관계** (없어도 동작해야 하는 경우)에만 사용한다.

```java
// 메일 서버가 없어도 앱은 돌아가야 하는 경우
@Autowired(required = false) // 빈이 없으면 이 메서드 자체가 호출 안 됨
public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
}
```

---

### 3. 필드 주입 (비권장)

```java
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired private MemberRepository memberRepository;
    @Autowired private DiscountPolicy discountPolicy;
}
```

코드가 가장 짧지만, 스프링 컨테이너 없이는 의존관계를 주입할 방법이 없다.
순수 자바로 테스트할 때 `memberRepository`가 `null`이 되어 `NullPointerException`이 발생한다.

테스트 코드나 `@Configuration` 내부의 `@Bean` 메서드에서만 제한적으로 사용한다.

---

## 생성자 주입을 권장하는 4가지 이유

### 이유 1: `final`로 불변 보장

```java
@Component
public class OrderServiceImpl {
    private final MemberRepository memberRepository; // final → 생성 후 변경 불가

    public OrderServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    // set 메서드가 없으므로 외부에서 바꿀 수 없음
}
```

`final`을 쓸 수 있기 때문에, 한 번 주입된 의존관계는 이후에 절대 변경되지 않는다.
Setter 주입은 `final`을 쓸 수 없다. 누군가 실수로 `setMemberRepository(null)`을 호출할 수 있다.

### 이유 2: 주입 누락을 컴파일 타임에 발견

```java
// 생성자 주입 — 주입 안 하면 컴파일 에러
OrderServiceImpl service = new OrderServiceImpl(); // 에러: 인자 2개 필요

// 필드 주입 — 주입 안 해도 컴파일 됨 → 런타임에 NPE 발생
@Autowired
private MemberRepository memberRepository;
// 스프링 없이 new OrderServiceImpl() 하면 memberRepository = null → NPE
```

컴파일 에러가 나는 것이 런타임 에러보다 훨씬 낫다.

### 이유 3: 스프링 없이 순수 자바로 테스트 가능

```java
// 생성자 주입 — 스프링 없이 테스트 가능
@Test
void orderTest() {
    MemberRepository mockRepo = new MemoryMemberRepository();
    DiscountPolicy mockPolicy = new FixDiscountPolicy();

    // 직접 new로 만들어서 테스트
    OrderServiceImpl service = new OrderServiceImpl(mockRepo, mockPolicy);

    Order order = service.createOrder(1L, "item", 10000);
    assertThat(order.getDiscountPrice()).isEqualTo(1000);
}
// 스프링 컨테이너 없이도 빠르고 간결한 단위 테스트 가능

// 필드 주입 — 스프링 컨테이너가 있어야만 주입됨
// @SpringBootTest를 붙여야 해서 테스트가 무거워지고 느려짐
```

### 이유 4: 순환 참조를 애플리케이션 시작 시점에 발견

```java
// A → B → A 순환 참조 상황
@Component
public class A {
    public A(B b) { ... } // B에 의존
}
@Component
public class B {
    public B(A a) { ... } // A에 의존 → A가 B를 필요로 하고, B가 A를 필요로 함 → 무한 루프
}
```

```text
생성자 주입: 애플리케이션 시작 시점에 즉시 BeanCreationException 발생 → 배포 전에 발견 ✓
필드/Setter:  실제 메서드 호출 시점에 StackOverflowError → 운영 중에 발견 ✗
```

운영 중에 StackOverflowError가 터지는 것보다 시작 시점에 에러가 나는 게 훨씬 낫다.

---

## 비교 요약

| 특징 | 생성자 주입 | Setter 주입 | 필드 주입 |
|---|---|---|---|
| `final` 사용 | O | X | X |
| 불변 보장 | O | X | X |
| 컴파일 타임 누락 감지 | O | X | X |
| 스프링 없이 테스트 | O | O | X |
| 순환 참조 조기 감지 | O (시작 시점) | X (런타임) | X (런타임) |
| 권장 여부 | **강력 권장** | 선택적 의존관계에만 | 테스트 코드 외 비권장 |

> **원칙**: 기본은 생성자 주입. 선택적 의존관계는 `@Autowired(required=false)` Setter 주입.

---

## 내부 동작 원리

### BeanFactory vs ApplicationContext

<div class="concept-box" markdown="1">

`BeanFactory`는 스프링 컨테이너의 최상위 인터페이스다. 빈 조회·생성의 핵심 기능만 가진다.
`ApplicationContext`는 `BeanFactory`를 상속하면서 6가지 부가 기능을 추가한 것이다.

실무에서는 항상 `ApplicationContext`를 사용한다.

</div>

```text
BeanFactory
  └─ ApplicationContext
       ├─ MessageSource          — 국제화(i18n): getMessage("hello", Locale.KOREAN)
       ├─ EnvironmentCapable     — 환경변수 분리: dev/prod 프로필, application.yml 프로퍼티
       ├─ ApplicationEventPublisher — 이벤트 발행/구독 (Observer 패턴)
       ├─ ResourcePatternResolver — 파일, URL, classpath 등 리소스 추상화
       ├─ HierarchicalBeanFactory — 부모 컨테이너 위임 (컨텍스트 계층 구조)
       └─ ListableBeanFactory    — 여러 빈을 목록으로 조회
```

| 구분 | BeanFactory | ApplicationContext |
|------|------------|-------------------|
| 빈 생성 시점 | 요청 시 지연 생성 (Lazy) | 컨테이너 시작 시 즉시 생성 (Eager) |
| 국제화 | X | O |
| 이벤트 발행 | X | O |
| 환경변수 처리 | X | O |
| 실무 사용 | 거의 안 씀 | 항상 사용 |

> **왜 ApplicationContext가 Eager 로딩인가?**
> 시작 시점에 모든 빈을 생성하면 설정 오류(순환참조, 누락된 빈)를 배포 전에 발견할 수 있다.
> BeanFactory의 지연 생성은 첫 요청 때 오류가 발생해 운영 중에 장애를 내는 위험이 있다.

---

### 스프링 컨테이너 내부 동작 5단계

`new AnnotationConfigApplicationContext(AppConfig.class)` 한 줄이 실행될 때 내부에서 일어나는 일:

```java
① @Configuration 클래스 로딩
   AppConfig.class를 읽어 BeanDefinition(메타데이터) 수집
   — 클래스명, 스코프, 의존관계, 초기화 메서드 등을 Map에 저장

② BeanDefinition 메타데이터 수집
   스프링은 빈을 "생성"하기 전에 먼저 "설계도"를 만든다
   BeanDefinition {
     beanClass     = OrderServiceImpl.class
     scope         = singleton
     lazyInit      = false
     constructorArgs = [MemberRepository, DiscountPolicy]
     initMethodName = null
   }

③ 빈 인스턴스 생성 (리플렉션)
   Class.forName("OrderServiceImpl")
     → Constructor con = clazz.getDeclaredConstructor(MemberRepository.class, DiscountPolicy.class)
     → con.newInstance(memberRepository, discountPolicy)
   → 실제 객체가 힙에 생성됨

④ 의존관계 주입 (AutowiredAnnotationBeanPostProcessor)
   @Autowired가 붙은 필드·메서드를 리플렉션으로 탐색
   → 타입으로 빈 검색 → field.setAccessible(true) → field.set(bean, resolvedBean)

⑤ 생명주기 콜백
   @PostConstruct → InitializingBean.afterPropertiesSet() → init-method 순으로 실행
```

---

### @Autowired가 실제로 처리되는 방법

`@Autowired`는 `AutowiredAnnotationBeanPostProcessor`가 처리한다. 이 처리기는 빈이 생성된 직후 `postProcessProperties()`를 호출한다.

```java
// AutowiredAnnotationBeanPostProcessor 의사코드
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {

    // 1. 이 빈 클래스에서 @Autowired 달린 필드·메서드 목록 수집
    InjectionMetadata metadata = findAutowiringMetadata(bean.getClass());

    // 2. 각 주입 대상에 대해 실행
    for (InjectedElement element : metadata.injectedElements) {

        // 3. 필드 타입으로 컨테이너에서 빈 검색
        Object value = beanFactory.resolveDependency(element.type);

        // 4. 리플렉션으로 실제 주입
        Field field = element.field;
        field.setAccessible(true);   // private 접근 허용
        field.set(bean, value);      // 값 주입
    }

    return pvs;
}
```

> **왜 리플렉션을 쓰는가?**
> 스프링은 컴파일 시점에 어떤 클래스의 어떤 필드에 주입해야 할지 알 수 없다.
> 런타임에 클래스 구조를 읽어서 동적으로 주입하기 위해 리플렉션을 사용한다.
> 이것이 `private` 필드에도 `@Autowired`가 동작하는 이유다.
