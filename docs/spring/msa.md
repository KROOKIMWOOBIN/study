## MSA (Microservices Architecture)

### 왜 쓰는지

<div class="concept-box" markdown="1">

**핵심**: MSA는 **하나의 애플리케이션을 여러 개의 작은 독립 서비스로 분리**하여, 각 서비스를 독립적으로 개발·배포·확장할 수 있게 하는 아키텍처입니다.

</div>

모놀리식 서비스는 규모가 커질수록:
- **배포 위험도**: 전체 재배포로 한 기능 변경이 전체 영향
- **확장 비효율**: 특정 모듈만 병목이어도 전체 스케일 아웃
- **장애 파급**: 하나의 모듈 장애가 전체 서비스 다운
- **기술 선택 제약**: 단일 기술 스택 강제

MSA는 이러한 문제를 해결합니다:

| 구분 | 모놀리식 | MSA |
|------|---------|-----|
| **배포** | 전체 재배포 | 서비스 단위 독립 배포 |
| **확장** | 전체 스케일 아웃 | 병목 서비스만 확장 |
| **장애 격리** | 하나 죽으면 전체 영향 | 일부 서비스만 영향 |
| **기술 선택** | 단일 스택 | 서비스별 최적 기술 |
| **복잡도** | 낮음 | 높음 (네트워크, 분산 트랜잭션) |

---

### 어떻게 쓰는지

#### Spring Cloud 구성요소

| 컴포넌트 | 역할 | 구현체 |
|---------|------|--------|
| **API Gateway** | 단일 진입점, 라우팅, 인증, 로깅 | Spring Cloud Gateway |
| **Service Discovery** | 서비스 위치 동적 등록/조회 | Eureka, Consul |
| **Config Server** | 중앙 설정 관리 (환경별 분리) | Spring Cloud Config |
| **Circuit Breaker** | 장애 전파 차단, Fallback | Resilience4j, Hystrix |
| **Load Balancer** | 서비스 인스턴스 부하 분산 | Spring Cloud LoadBalancer |
| **분산 트래킹** | 마이크로서비스 간 요청 추적 | Sleuth, Zipkin |
| **메시지 브로커** | 비동기 통신, 이벤트 기반 | Kafka, RabbitMQ |

#### 1) API Gateway (단일 진입점)

```yaml
# application.yml (Gateway)
spring:
  cloud:
    gateway:
      routes:
        - id: member-service
          uri: lb://MEMBER-SERVICE  # Eureka로 동적 조회
          predicates:
            - Path=/api/members/**
          filters:
            - AuthorizationFilter  # JWT 검증
            - name: CircuitBreaker
              args:
                name: memberServiceCB

        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/orders/**
```

#### 2) Service Discovery (Eureka)

```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServer { ... }

// Eureka Client (각 서비스)
@SpringBootApplication
@EnableEurekaClient
public class MemberServiceApplication { ... }
```

```yaml
# application.yml (각 서비스)
spring:
  application:
    name: MEMBER-SERVICE
eureka:
  client:
    service-url:
      defaultZone: http://discovery-server:8761/eureka
```

#### 3) 서비스 간 통신 (FeignClient)

```java
@FeignClient(name = "ORDER-SERVICE")
public interface OrderFeignClient {

    @GetMapping("/api/orders/member/{memberId}")
    List<OrderResponse> getOrdersByMember(@PathVariable Long memberId);
}

// 사용
@Service
@RequiredArgsConstructor
public class MemberService {

    private final OrderFeignClient orderFeignClient;

    public MemberDetailResponse getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<OrderResponse> orders = orderFeignClient.getOrdersByMember(memberId);
        return new MemberDetailResponse(member, orders);
    }
}
```

#### 4) Circuit Breaker (장애 전파 차단)

호출 대상 서비스가 장애일 때 연속 호출을 차단하고 fallback을 반환합니다.

```java
@CircuitBreaker(name = "orderService", fallbackMethod = "getOrdersFallback")
public List<OrderResponse> getOrders(Long memberId) {
    return orderFeignClient.getOrdersByMember(memberId);
}

public List<OrderResponse> getOrdersFallback(Long memberId, Exception e) {
    log.warn("주문 서비스 장애, fallback 반환: {}", e.getMessage());
    return Collections.emptyList();  // 빈 목록으로 graceful degradation
}
```

#### 5) 분산 트랜잭션 (SAGA 패턴)

MSA에서 `@Transactional`은 서비스 간 원자성을 보장하지 못합니다. **SAGA 패턴**으로 보상 트랜잭션을 구현합니다.

```text
Choreography 방식 (이벤트 기반)
주문 서비스: 주문 생성 → "ORDER_CREATED" 이벤트 발행
결제 서비스: 이벤트 수신 → 결제 처리 → "PAYMENT_COMPLETED" 발행
재고 서비스: 이벤트 수신 → 재고 차감

결제 실패 시 (보상 트랜잭션)
결제 서비스: "PAYMENT_FAILED" 이벤트 발행
주문 서비스: 이벤트 수신 → 주문 취소
재고 서비스: 이벤트 수신 → 재고 복구
```

---

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **높은 확장성 필요** | ✅ MSA | 병목 서비스만 독립 확장 |
| **팀 규모 크고 다양** | ✅ MSA | 팀별 독립 개발/배포 |
| **기술 스택 다양화** | ✅ MSA | 서비스별 최적 기술 선택 |
| **빈번한 배포 필요** | ✅ MSA | 단일 서비스만 배포 |
| **작은 팀, 단순 서비스** | ❌ 모놀리식 | 복잡도 대비 이득 부족 |
| **강한 ACID 보장 필요** | ⚠️ 신중 | 분산 트랜잭션 복잡도 증가 |

---

### 장점

| 장점 | 설명 |
|------|------|
| **독립 배포** | 각 서비스 독립 배포로 배포 위험 감소 |
| **병목 확장** | 병목 서비스만 선택적으로 스케일 아웃 |
| **장애 격리** | 한 서비스 장애가 다른 서비스에 영향 최소화 |
| **기술 자유도** | 서비스별로 최적의 기술 스택 선택 가능 |
| **팀 독립성** | 팀별로 독립적 개발/배포/운영 가능 |
| **높은 가용성** | 일부 서비스 장애 시에도 부분 서비스 제공 |

---

### 단점

| 단점 | 설명 |
|------|------|
| **네트워크 오버헤드** | 서비스 간 호출로 인한 지연 증가 |
| **분산 트랜잭션 복잡성** | ACID 보장 불가능, SAGA 패턴 필수 |
| **데이터 일관성** | 최종 일관성(Eventual Consistency) 모델 |
| **운영 복잡도** | 서비스 수만큼 모니터링·로깅·추적 포인트 증가 |
| **테스트 어려움** | 통합 테스트, E2E 테스트 복잡도 증가 |
| **초기 비용** | CI/CD, 모니터링, 인프라 구축 필요 |

---

### 특징

#### 1. 분산 시스템의 도전

```text
1️⃣ 네트워크 지연
   - 서비스 간 호출마다 네트워크 왕복
   - 최대 지연 시간 예측 어려움

2️⃣ 부분 장애 (Partial Failure)
   - 일부 서비스만 장애 가능
   - 전체 요청 실패로 처리할지 부분 성공 반환할지 판단 어려움

3️⃣ 데이터 일관성
   - 서비스별 독립 DB로 인한 데이터 동기화 문제
   - ACID 보장 불가능 → 최종 일관성(Eventual Consistency)

4️⃣ 네트워크 분할 (Network Partition)
   - 데이터센터 간 네트워크 단절 시 합의 불가능
   - CAP 정리에 따라 일관성과 가용성 선택
```

#### 2. 동기 vs 비동기 통신

```text
동기 호출 (FeignClient)
- 장점: 응답 대기, 즉시 처리 가능, 구현 단순
- 단점: 연쇄 지연, 타임아웃 처리 필수, 신뢰성 낮음

비동기 호출 (메시지 브로커)
- 장점: 느슨한 결합, 높은 처리량, 부분 장애 격리
- 단점: 복잡한 디버깅, 순서 보장 어려움, 메시지 손실 처리
```

#### 3. API Gateway의 역할

```text
API Gateway 없이
- 클라이언트가 각 서비스 URL 알아야 함
- 서비스 변경 시 클라이언트 수정 필요
- 인증/로깅 중복 구현

API Gateway 적용
- 단일 진입점으로 서비스 발견 자동화
- 라우팅, 인증, 로깅 중앙 처리
- 서비스 변경 시 클라이언트 영향 없음
```

---

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 초기 도입 오류**

```text
작은 조직, 단순 서비스에 MSA 도입
→ 구축 비용: 높음
→ 운영 복잡도: 높음
→ 실제 이득: 미미
```

**✅ 올바른 접근:**
- 모놀리식으로 시작
- 서비스 수가 많아지고 확장 필요 시 분리
- "느린 소프트웨어 사망" (Slow software death) 문제 발생 후 마이그레이션

</div>

<div class="danger-box" markdown="1">

**❌ 동기 호출 과다**

```text
서비스 A → B → C → D 의존 체인
- 한 서비스 장애 시 전체 요청 실패
- 네트워크 지연 누적: 4 배의 지연 발생
```

**✅ 올바른 방식:**
- 필수 기능만 동기 호출
- 나머지는 비동기 (메시지 브로커)
- Circuit Breaker로 장애 전파 차단

</div>

<div class="warning-box" markdown="1">

**⚠️ 분산 트랜잭션 오류**

```java
// ❌ MSA에서 작동하지 않음
@Transactional  // 서비스 경계를 넘을 수 없음
public void placeOrder(Order order) {
    orderService.save(order);      // Service A
    paymentService.pay(order);     // Service B (다른 서버)
}
```

**✅ SAGA 패턴 사용:**
- Choreography: 이벤트 기반 (느슨한 결합)
- Orchestration: 중앙 조정자 (명확한 흐름)

</div>

<div class="warning-box" markdown="1">

**⚠️ 모니터링 부재**

```text
MSA 환경에서 문제 추적 어려움
- 요청이 여러 서비스를 거침
- 어느 서비스에서 느린지 파악 어려움
- 한 서비스의 오류가 전체 영향
```

**✅ 필수 구성:**
- 분산 추적 (Sleuth, Zipkin)
- 중앙 로그 수집 (ELK Stack)
- 메트릭 수집 (Prometheus, Grafana)

</div>

<div class="warning-box" markdown="1">

**⚠️ 데이터 일관성 기대 오류**

```text
// ❌ 즉시 일관성 기대
주문 생성 → 결제 처리 → 재고 차감
// 결제 중 장애 시 롤백 불가능 (서로 다른 DB)
```

**✅ 최종 일관성 설계:**
- 보상 트랜잭션으로 일관성 복구
- 임시적 불일치 허용
- 일관성 감시 도구 (Saga 보상 로직)

</div>

---

### 정리

| 항목 | 설명 |
|------|------|
| **구성요소** | API Gateway, Service Discovery, Config Server, Circuit Breaker, 분산 추적 |
| **통신 방식** | 동기 (FeignClient), 비동기 (Kafka, RabbitMQ) |
| **트랜잭션** | SAGA 패턴 (Choreography / Orchestration) |
| **장점** | 확장성, 독립 배포, 기술 자유도, 높은 가용성 |
| **단점** | 복잡도, 분산 트랜잭션, 네트워크 오버헤드, 운영 복잡도 |
| **주의점** | 조직 규모에 맞는 도입, 적절한 통신 방식 선택, 모니터링 필수 |

---

**관련 파일:**
- [architecture.md](architecture.md) — 다양한 아키텍처 패턴
- [kafka.md](kafka.md) — 메시지 브로커 기반 비동기 통신
