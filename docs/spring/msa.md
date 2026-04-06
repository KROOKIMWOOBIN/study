## MSA (Spring Cloud)

### 왜 쓰는가?

모놀리식 서비스는 규모가 커질수록 배포가 어렵고, 한 모듈의 장애가 전체에 영향을 준다. MSA는 서비스를 독립적으로 배포·확장할 수 있게 분리한다.

| 구분 | 모놀리식 | MSA |
|------|---------|-----|
| 배포 | 전체 재배포 | 서비스 단위 독립 배포 |
| 확장 | 전체 스케일 아웃 | 병목 서비스만 확장 |
| 장애 격리 | 하나 죽으면 전체 영향 | 일부 서비스만 영향 |
| 기술 선택 | 단일 스택 | 서비스별 최적 기술 |
| 복잡도 | 낮음 | 높음 (네트워크, 분산 트랜잭션) |

---

### Spring Cloud 구성요소

| 컴포넌트 | 역할 | 구현체 |
|---------|------|--------|
| API Gateway | 단일 진입점, 라우팅, 인증 | Spring Cloud Gateway |
| Service Discovery | 서비스 위치 동적 등록/조회 | Eureka |
| Config Server | 중앙 설정 관리 | Spring Cloud Config |
| Circuit Breaker | 장애 전파 차단 | Resilience4j |
| Load Balancer | 서비스 인스턴스 부하 분산 | Spring Cloud LoadBalancer |

---

### API Gateway

```markdown
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

---

### Service Discovery (Eureka)

```markdown
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServer { ... }

// Eureka Client (각 서비스)
@SpringBootApplication
@EnableEurekaClient
public class MemberServiceApplication { ... }
```

```markdown
# application.yml (각 서비스)
spring:
  application:
    name: MEMBER-SERVICE
eureka:
  client:
    service-url:
      defaultZone: http://discovery-server:8761/eureka
```

---

### 서비스 간 통신 — FeignClient

```markdown
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

---

### Circuit Breaker (Resilience4j)

호출 대상 서비스가 장애일 때 연속 호출을 차단하고 fallback을 반환한다.

```markdown
@CircuitBreaker(name = "orderService", fallbackMethod = "getOrdersFallback")
public List<OrderResponse> getOrders(Long memberId) {
    return orderFeignClient.getOrdersByMember(memberId);
}

public List<OrderResponse> getOrdersFallback(Long memberId, Exception e) {
    log.warn("주문 서비스 장애, fallback 반환: {}", e.getMessage());
    return Collections.emptyList();  // 빈 목록으로 graceful degradation
}
```

---

### 분산 트랜잭션 — SAGA 패턴

MSA에서 `@Transactional`은 서비스 간 원자성을 보장하지 못한다. SAGA 패턴으로 보상 트랜잭션을 구현한다.

```markdown
// Choreography 방식 (이벤트 기반)
주문 서비스: 주문 생성 → "ORDER_CREATED" 이벤트 발행
결제 서비스: 이벤트 수신 → 결제 처리 → "PAYMENT_COMPLETED" 발행
재고 서비스: 이벤트 수신 → 재고 차감

// 결제 실패 시
결제 서비스: "PAYMENT_FAILED" 이벤트 발행
주문 서비스: 이벤트 수신 → 주문 취소 (보상 트랜잭션)
```

---

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 네트워크 지연 | 서비스 간 호출 오버헤드 | 필요한 경우만 동기 호출, 나머지 비동기 |
| 분산 트랜잭션 | @Transactional 적용 불가 | SAGA 패턴 |
| 운영 복잡도 | 서비스 수만큼 모니터링 포인트 | 중앙 로그 수집 (ELK), 분산 추적 (Zipkin) |
| 초기 도입 | 작은 서비스에 MSA는 과도한 복잡성 | 모놀리식으로 시작 후 필요 시 분리 |
