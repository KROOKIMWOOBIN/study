# Spring 학습 로드맵

---

## Spring이란?

자바로 웹 애플리케이션을 만들 때 반복적으로 필요한 기능들(객체 생성, 의존관계 연결, 트랜잭션 처리 등)을 대신 처리해주는 **프레임워크**.

```
내가 할 일:  비즈니스 로직 (회원가입, 주문, 결제 등) 작성
Spring 할 일: 객체 생성/연결, HTTP 요청 처리, DB 연결, 트랜잭션 관리 등
```

---

## 왜 Spring을 쓰는가 — SOLID 원칙

Spring이 존재하는 핵심 이유는 **좋은 객체지향 설계**를 쉽게 할 수 있도록 돕기 위해서다.

좋은 설계의 기준이 되는 5가지 원칙이 SOLID다.

### SRP — 단일 책임 원칙 (Single Responsibility Principle)

> **하나의 클래스는 하나의 책임만 가진다.**

```markdown
// 나쁜 예: 주문 서비스가 너무 많은 일을 한다
public class OrderService {
    public void order(...) { ... }         // 주문 처리
    public void sendEmail(...) { ... }     // 이메일 발송
    public void saveToFile(...) { ... }    // 파일 저장
    public void validateMember(...) { ... } // 회원 검증
}

// 좋은 예: 역할을 나눈다
public class OrderService { public void order(...) { ... } }
public class EmailService { public void send(...) { ... } }
public class MemberValidator { public void validate(...) { ... } }
```

변경 사유가 하나일수록 변경 범위가 좁아지고, 영향이 줄어든다.

---

### OCP — 개방-폐쇄 원칙 (Open/Closed Principle)

> **확장에는 열려 있고, 변경에는 닫혀 있어야 한다.**
> 새 기능을 추가할 때 기존 코드를 수정하면 안 된다.

```markdown
// 할인 정책을 정액에서 정률로 바꾸는 상황

// OCP 위반 — OrderService 코드를 직접 수정해야 함
public class OrderServiceImpl {
    // private DiscountPolicy discountPolicy = new FixDiscountPolicy();  // 변경 전
    private DiscountPolicy discountPolicy = new RateDiscountPolicy();    // 변경 후 → OrderService 수정!
}

// OCP 준수 — AppConfig(설정)만 바꾸면 됨
public class AppConfig {
    // 여기만 바꾸면 OrderServiceImpl은 건드리지 않아도 됨
    public DiscountPolicy discountPolicy() {
        // return new FixDiscountPolicy();
        return new RateDiscountPolicy(); // 이 한 줄만 변경
    }
}
```

Spring의 DI가 OCP를 실현해준다.

---

### LSP — 리스코프 치환 원칙 (Liskov Substitution Principle)

> **구현체는 인터페이스가 약속한 기능을 위반하면 안 된다.**

```markdown
public interface DiscountPolicy {
    int discount(Member member, int price); // "할인 금액을 반환"이라는 약속
}

// LSP 위반 — 인터페이스 약속을 어김
public class BadDiscountPolicy implements DiscountPolicy {
    public int discount(Member member, int price) {
        return price * 2; // 할인인데 오히려 가격을 올림 → 계약 위반
    }
}
```

---

### ISP — 인터페이스 분리 원칙 (Interface Segregation Principle)

> **범용 인터페이스 하나보다 작은 인터페이스 여러 개가 낫다.**

```markdown
// 나쁜 예: 모든 기능이 한 인터페이스에
public interface UserInterface {
    void login();
    void logout();
    void changePassword();
    void viewAdminPanel();  // 일반 사용자는 필요 없음
    void deleteUser();      // 일반 사용자는 필요 없음
}

// 좋은 예: 역할별로 분리
public interface UserAuth { void login(); void logout(); }
public interface AdminOps { void viewAdminPanel(); void deleteUser(); }
```

불필요한 메서드에 의존하지 않아도 된다.

---

### DIP — 의존관계 역전 원칙 (Dependency Inversion Principle)

> **구현 클래스가 아닌 인터페이스(추상)에 의존해야 한다.**

Spring DI의 핵심 원칙. 가장 중요하다.

```markdown
// DIP 위반 — 구현 클래스에 직접 의존
public class OrderServiceImpl {
    private MemoryMemberRepository memberRepository = new MemoryMemberRepository(); // 구체 클래스!
    private FixDiscountPolicy discountPolicy = new FixDiscountPolicy();              // 구체 클래스!
    // 구현체가 바뀌면 이 코드도 바꿔야 함
}

// DIP 준수 — 인터페이스에만 의존
public class OrderServiceImpl {
    private MemberRepository memberRepository;  // 인터페이스만 알면 됨
    private DiscountPolicy discountPolicy;       // 인터페이스만 알면 됨
    // 어떤 구현체가 들어올지는 Spring이 결정해서 주입해줌
}
```

---

## Spring이 SOLID를 어떻게 실현하는가

순수 자바만으로는 DIP를 지키기 어렵다. 인터페이스에만 의존하면 구현체를 `new`로 직접 생성하지 못하기 때문이다.

```markdown
// 딜레마: 인터페이스에 의존하면 구현체를 어디서 생성하나?
MemberRepository memberRepository; // 인터페이스만 있고
// memberRepository = new ??? → 누군가는 구현체를 만들어야 함
```

Spring이 이 역할을 대신한다.

```
개발자 →  인터페이스 기반으로 코드 작성
Spring → 구현체를 생성하고, 의존관계를 연결(주입)해줌
```

---

## 학습 순서

### Spring Core (기초)

| 단계 | 주제 | 핵심 질문 |
|------|------|-----------|
| 1 | [IoC & DI](./IoC-DI.md) | 제어권이 왜 외부로 넘어가야 하는가? |
| 2 | [스프링 컨테이너](./스프링컨테이너.md) | 스프링은 빈을 어떻게 관리하는가? |
| 3 | [싱글톤](./싱글톤.md) | 왜 하나의 객체만 써야 하는가? |
| 4 | [컴포넌트 스캔](./컴포넌트스캔.md) | 빈 등록을 자동화하는 방법은? |
| 5 | [의존관계 주입](./의존관계주입.md) | 같은 타입 빈이 여러 개면 어떻게 주입하는가? |
| 6 | [빈 생명주기](./빈생명주기.md) | 초기화/소멸 시점에 어떻게 개입하는가? |
| 7 | [빈 스코프](./빈스코프.md) | 매번 새 객체가 필요할 때는? |

### Spring Boot & Web

| 단계 | 주제 |
|------|------|
| 8 | [Spring Boot](./springboot.md) |
| 9 | [Spring MVC](./mvc.md) |
| 10 | [REST API](./restapi.md) |
| 11 | [Validation](./validation.md) |
| 12 | [Exception Handler](./exception.md) |
| 13 | [API 문서화 (Swagger)](./swagger.md) |

### 공통 기능

| 단계 | 주제 |
|------|------|
| 14 | [AOP](./aop.md) |
| 15 | [Transaction](./transaction.md) |
| 16 | [Logging](./logging.md) |

### 데이터 접근

| 단계 | 주제 |
|------|------|
| 17 | [JPA / Spring Data JPA](./jpa.md) |
| 18 | [QueryDSL](./querydsl.md) |
| 19 | [JPA 성능 최적화](./jpa-performance.md) |
| 20 | [Redis](./redis.md) |

### 보안 & 동시성

| 단계 | 주제 |
|------|------|
| 21 | [Spring Security + JWT](./security.md) |
| 22 | [동시성 제어](./concurrency.md) |

### 메시징 & 테스트

| 단계 | 주제 |
|------|------|
| 23 | [Kafka](./kafka.md) |
| 24 | [Testing](./testing.md) |

### 운영 & 아키텍처

| 단계 | 주제 |
|------|------|
| 25 | [모니터링 (Actuator + Grafana)](./monitoring.md) |
| 26 | [아키텍처 패턴](./architecture.md) |
| 27 | [MSA (Spring Cloud)](./msa.md) |
