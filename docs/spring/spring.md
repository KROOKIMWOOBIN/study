# Spring 학습 로드맵

---

## Spring이란?

<div class="concept-box" markdown="1">

자바로 웹 애플리케이션을 만들 때 반복적으로 필요한 기능들을 프레임워크 수준에서 대신 처리해주는 도구다.

```text
내가 할 일:  비즈니스 로직 (회원가입, 주문, 결제 등) 작성
Spring 할 일: 객체 생성/연결, HTTP 요청 처리, DB 연결/트랜잭션 관리, 보안 등
```

Spring의 핵심 철학은 **좋은 객체지향 설계**를 쉽게 할 수 있도록 돕는 것이다.

</div>

---

## 왜 Spring을 쓰는가

### 순수 자바의 한계

Spring 없이 자바로만 주문 서비스를 만든다고 하자.

```java
public class OrderServiceImpl implements OrderService {
    // 직접 구현체를 선택하고 생성
    private MemberRepository memberRepository = new MemoryMemberRepository();
    private DiscountPolicy discountPolicy = new FixDiscountPolicy();

    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discount);
    }
}
```

이 코드의 문제는 무엇인가?

**요구사항이 바뀌면 어떻게 되는가?**

> "정액 할인(1000원 고정)에서 정률 할인(10%)으로 바꿔주세요."

```java
public class OrderServiceImpl implements OrderService {
    // OrderServiceImpl을 직접 수정해야 한다
    // private DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private DiscountPolicy discountPolicy = new RateDiscountPolicy(); // 변경!
}
```

`OrderServiceImpl`은 주문 처리가 담당인데, 할인 정책 변경 때문에 코드를 건드려야 한다.
이는 명백한 설계 원칙 위반이다.

---

## SOLID 원칙

좋은 객체지향 설계의 5가지 기준. Spring은 이 원칙들을 실현하는 도구다.
→ [SOLID 원칙 상세 보기](./solid.md)

| 원칙 | 이름 | 한 줄 요약 |
|------|------|-----------|
| **S** | SRP — 단일 책임 원칙 | 하나의 클래스는 변경 이유가 하나뿐이어야 한다 |
| **O** | OCP — 개방-폐쇄 원칙 | 확장에는 열려 있고, 변경에는 닫혀 있어야 한다 |
| **L** | LSP — 리스코프 치환 원칙 | 구현체는 인터페이스의 계약을 반드시 지켜야 한다 |
| **I** | ISP — 인터페이스 분리 원칙 | 범용 인터페이스 하나보다 작은 인터페이스 여러 개가 낫다 |
| **D** | DIP — 의존관계 역전 원칙 | 구현이 아닌 인터페이스(추상)에 의존해야 한다 |

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
