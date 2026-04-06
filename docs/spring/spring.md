> [← 홈](/study/)

# Spring 학습 로드맵

Spring을 입문부터 실무까지 단계적으로 정리한 학습 노트.

---

## SOLID 원칙

Spring이 지향하는 객체지향 설계 5원칙. Spring DI/IoC의 존재 이유이기도 하다.

| 원칙 | 이름 | 핵심 |
|------|------|------|
| SRP | 단일 책임 원칙 | 한 클래스는 하나의 책임만 가져야 한다 |
| OCP | 개방-폐쇄 원칙 | 확장에는 열려 있고, 변경에는 닫혀 있어야 한다 |
| LSP | 리스코프 치환 원칙 | 구현체는 인터페이스가 약속한 기능을 위반하면 안 된다 |
| ISP | 인터페이스 분리 원칙 | 범용 인터페이스 하나보다 작은 인터페이스 여러 개가 낫다 |
| DIP | 의존관계 역전 원칙 | 구현 클래스가 아닌 인터페이스(추상)에 의존해야 한다 |

```markdown
// DIP 위반
private MemberRepository memberRepository = new MemoryMemberRepository();

// DIP 준수 (Spring DI 활용)
private MemberRepository memberRepository;  // 인터페이스에만 의존, 구현체는 Spring이 주입
```

---

## 학습 순서

### Spring Core (기초)

| 단계 | 주제 |
|------|------|
| 1 | [IoC & DI](./IoC-DI.md) |
| 2 | [스프링 컨테이너](./스프링컨테이너.md) |
| 3 | [싱글톤](./싱글톤.md) |
| 4 | [컴포넌트 스캔](./컴포넌트스캔.md) |
| 5 | [의존관계 주입](./의존관계주입.md) |
| 6 | [빈 생명주기](./빈생명주기.md) |
| 7 | [빈 스코프](./빈스코프.md) |

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
