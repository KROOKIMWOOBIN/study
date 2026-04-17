## Transactional Outbox Pattern

### 왜 쓰는지

DB 저장과 이벤트 발행을 **동일한 트랜잭션에서 원자적으로 처리**하기 위해 사용합니다.

#### 이중 쓰기(Dual Write) 문제

```java
// ❌ 문제: DB 저장 성공 → Kafka 발행 실패
@Transactional
public void placeOrder(Order order) {
    orderRepository.save(order);        // ✅ DB 커밋
    kafkaTemplate.send("orders", order); // ❌ 실패 → 이벤트 유실!
}

// DB에는 있지만 다른 서비스는 몰라 → 데이터 불일치
```

**문제점:**
| 상황 | 결과 | 문제 |
|------|------|------|
| DB ✅ + Kafka ❌ | 이벤트 유실 | 다른 서비스가 변경을 못 봄 |
| DB ❌ + Kafka ✅ | 유령 이벤트 | 존재하지 않는 주문으로 처리 |
| Kafka 재시도 | 중복 발행 | 멱등성 없으면 데이터 중복 |

`@Transactional`은 **DB만 원자성 보장**하며, Kafka 같은 외부 시스템은 커버하지 못합니다.

<div class="concept-box" markdown="1">

**핵심**: ==Transactional Outbox==는 **이벤트를 먼저 DB의 Outbox 테이블에 저장한 후, 별도 프로세스가 발행**하여 DB 트랜잭션의 원자성을 활용합니다.

</div>

---

### 어떻게 쓰는지

#### 1) Outbox 테이블 설계

```sql
CREATE TABLE outbox (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aggregate_id BIGINT NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, PUBLISHED, FAILED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    UNIQUE KEY uk_aggregate (aggregate_id, aggregate_type),
    INDEX idx_status (status, created_at)
);
```

#### 2) 이벤트 저장 — Outbox 테이블에 기록

```java
@Entity
@Table(name = "outbox")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id")
    private Long aggregateId;

    @Column(name = "aggregate_type")
    private String aggregateType;  // "Order", "Payment", ...

    @Column(name = "event_type")
    private String eventType;  // "ORDER_CREATED", "PAYMENT_COMPLETED", ...

    @Column(name = "payload", columnDefinition = "JSON")
    private String payload;  // 이벤트 JSON

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;  // PENDING, PUBLISHED, FAILED

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}

public enum OutboxStatus {
    PENDING,    // 미처리
    PUBLISHED,  // 성공 발행
    FAILED      // 발행 실패
}

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
    List<OutboxEvent> findByStatusAndCreatedAtBetween(OutboxStatus status, 
                                                       LocalDateTime start, 
                                                       LocalDateTime end);
}
```

#### 3) 주문 생성 시 Outbox 저장

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // 1️⃣ 주문 저장
        Order order = new Order(request.memberId(), request.items());
        Order savedOrder = orderRepository.save(order);

        // 2️⃣ 같은 트랜잭션에서 Outbox 이벤트 저장
        OutboxEvent event = new OutboxEvent(
            aggregateId = savedOrder.getId(),
            aggregateType = "Order",
            eventType = "ORDER_CREATED",
            payload = toJson(savedOrder),  // Order를 JSON으로 직렬화
            status = OutboxStatus.PENDING
        );
        outboxRepository.save(event);
        // 트랜잭션 커밋 시 Order + OutboxEvent 동시에 커밋됨

        return OrderResponse.from(savedOrder);
    }

    private String toJson(Order order) {
        return objectMapper.writeValueAsString(order);
    }
}
```

#### 4) Publisher — Outbox 폴링 방식 (Polling Relay)

```java
@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 1초마다 Outbox에서 미처리 이벤트를 읽어 발행
    @Scheduled(fixedDelay = 1000)  // 1초마다 실행
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository
            .findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pending) {
            try {
                // Kafka에 발행
                kafkaTemplate.send(
                    "events",                    // 토픽명
                    event.getAggregateId().toString(),  // 메시지 키
                    event.getPayload()          // 메시지 값
                );

                // 발행 성공 → 상태 업데이트
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(LocalDateTime.now());
                outboxRepository.save(event);

                log.info("이벤트 발행 성공: id={}, type={}", 
                         event.getId(), event.getEventType());

            } catch (Exception e) {
                // 발행 실패 → 재시도 카운트 증가 (별도 필드 필요)
                log.warn("이벤트 발행 실패: id={}, error={}", 
                         event.getId(), e.getMessage());
                event.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}
```

#### 5) Consumer — 멱등성 처리

```java
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentService paymentService;
    private final OutboxEventIdempotencyRepository idempotencyRepository;

    @KafkaListener(topics = "events", groupId = "payment-service")
    public void handleOrderCreated(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);

            // 멱등성 체크: 같은 메시지는 한 번만 처리
            if (idempotencyRepository.exists(order.getId())) {
                log.info("이미 처리된 메시지: orderId={}", order.getId());
                return;
            }

            // 결제 처리
            paymentService.processPayment(order);

            // 처리 기록
            idempotencyRepository.save(
                new IdempotencyRecord(order.getId(), LocalDateTime.now())
            );

        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", message, e);
            // DLQ로 전송 (별도 처리)
        }
    }
}

@Entity
@Table(name = "idempotency_record")
public class IdempotencyRecord {
    @Id
    private Long messageId;  // Order ID + Event Type 결합

    @CreationTimestamp
    private LocalDateTime processedAt;
}
```

#### 6) CDC 방식 (Debezium) — 자동 감지

Polling 방식 대신 **변경 로그 감시(Change Data Capture)**로 자동 발행:

```yaml
# docker-compose.yml
version: '3'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_LOG_BIN: mysql-bin
      MYSQL_BINLOG_FORMAT: row  # CDC 필수

  debezium-kafka-connect:
    image: debezium/connect:latest
    environment:
      KAFKA_BROKERS: kafka:9092
      GROUP_ID: debezium-group

# connector 설정 (자동으로 outbox → Kafka로 발행)
POST /connectors
{
  "name": "outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql",
    "database.port": 3306,
    "database.user": "root",
    "database.password": "root",
    "database.server.name": "order-service",
    "table.include.list": "order_db.outbox",
    "transforms": "unwrap",
    "transforms.unwrap.type": "io.debezium.transforms.outbox.EventRouter",
    "transforms.unwrap.event.key": "aggregate_id",
    "transforms.unwrap.table.expand.json.payload": "true"
  }
}
```

---

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **이벤트 유실 허용 안 됨** | ✅ Outbox | 금융, 주문, 결제 등 중요 도메인 |
| **최종 일관성 보장 필요** | ✅ Outbox | MSA 서비스 간 데이터 동기화 |
| **높은 가용성 필요** | ✅ Outbox | 브로커 장애 시에도 이벤트 보존 |
| **단순한 통지성 메시지** | ❌ 직접 발행 | 로그 생성, 분석 이벤트는 유실 가능 |
| **강한 ACID 보장 필요** | ⚠️ Saga | Outbox + Saga 조합 고려 |

---

### 장점

| 장점 | 설명 |
|------|------|
| **이중 쓰기 문제 해결** | 이벤트와 데이터 변경을 하나의 트랜잭션으로 묶음 |
| **At-least-once Delivery** | DB에 저장되면 반드시 한 번 이상 발행 보장 |
| **DB 트랜잭션 보장** | `@Transactional` 범위 내 원자성 확보 |
| **브로커 장애 격리** | Kafka 다운 시에도 Outbox에 이벤트 보존 |
| **재시도 가능** | 발행 실패한 이벤트 나중에 재시도 |
| **이벤트 감사 추적** | 모든 이벤트가 Outbox 테이블에 기록 |

---

### 단점

| 단점 | 설명 |
|------|------|
| **추가 테이블 관리** | Outbox 테이블 생성·유지·모니터링 필요 |
| **폴링 지연** | 1초 폴링이라면 최대 1초 발행 지연 |
| **중복 가능성** | Kafka 재시도 시 중복 메시지 (소비자 멱등성 필수) |
| **운영 복잡도** | Outbox 테이블 무한 증가 방지, 배치 정리 필요 |
| **스케줄러 포인트** | 단일 폴링 스케줄러가 한 인스턴스에서만 실행되면 병목 |
| **성능 오버헤드** | 매번 Outbox 쓰기로 인한 추가 I/O |

---

### 특징

#### 1. Polling vs CDC (Debezium)

```text
┌─────────────────────────────────────────────────────┐
│ Polling (폴링) 방식                                    │
├─────────────────────────────────────────────────────┤
│ 1. 스케줄러가 주기적으로 Outbox 테이블 조회           │
│ 2. PENDING 상태 이벤트를 Kafka로 발행                 │
│ 3. 상태를 PUBLISHED로 업데이트                        │
│                                                     │
│ 장점: 간단, 추가 도구 불필요                         │
│ 단점: 지연 발생, 폴링 주기마다 DB 쿼리               │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ CDC (Change Data Capture) 방식 — Debezium           │
├─────────────────────────────────────────────────────┤
│ 1. MySQL 바이너리 로그 감시                          │
│ 2. Outbox 테이블 변경 감지                            │
│ 3. 즉시 Kafka로 발행                                 │
│                                                     │
│ 장점: 실시간 발행, 폴링 오버헤드 없음                 │
│ 단점: Debezium 설치·운영 필요, 복잡도 증가             │
└─────────────────────────────────────────────────────┘
```

#### 2. At-Least-Once vs Exactly-Once

```text
At-Least-Once (Outbox의 기본 동작)
├─ 이벤트가 최소 1회 이상 발행 보장
├─ 네트워크 실패 시 재시도 → 중복 발행 가능
├─ 소비자가 멱등성 처리 필수
└─ 단순, 대부분의 경우 충분

Exactly-Once (매우 어려움)
├─ 이벤트 정확히 1회만 발행
├─ 분산 시스템에서 구현 어려움 (CAP 정리)
├─ Kafka 트랜잭션 + 소비자 멱등성 조합
└─ 오버헤드 크지만, 금융 거래에서 필수
```

#### 3. Outbox 테이블의 생명주기

```text
OrderService.placeOrder() 호출
   ↓
order, outbox 동시 INSERT (트랜잭션)
   ↓
COMMIT (둘 다 성공)
   ↓
OutboxPublisher.publishPendingEvents()
   ↓
Kafka 발행 시도
   ├─ 성공: status = PUBLISHED, published_at = NOW()
   └─ 실패: status = FAILED, retry_count++
   ↓
배치 정리 작업 (예: 7일 지난 PUBLISHED 레코드 삭제)
   ↓
디스크 공간 관리
```

#### 4. 분산 환경에서의 다중 인스턴스

```java
// 문제: 다중 인스턴스에서 중복 발행 방지
// 해결 1: 분산 락 (Redisson, ShedLock)
@Scheduled(fixedDelay = 1000)
@SchedulerLock(name = "publishOutbox", 
               lockAtMostFor = "30s", 
               lockAtLeastFor = "5s")
public void publishPendingEvents() {
    // 한 번에 한 인스턴스만 실행
}

// 해결 2: 스케줄러 전용 서비스 (별도 pod에서만 실행)
// Kubernetes StatefulSet으로 1개 레플리카만 배포
```

---

### 주의할 점

<div class="danger-box" markdown="1">

**❌ Outbox 테이블 무한 증가**

```sql
-- 문제: 7일 후에도 PUBLISHED 레코드가 삭제 안 됨
-- 디스크 풀 가능성 → 장애 발생

-- ✅ 정기적 정리 배치 필수
DELETE FROM outbox 
WHERE status = 'PUBLISHED' 
  AND published_at < DATE_SUB(NOW(), INTERVAL 7 DAY)
LIMIT 10000;  -- 한 번에 대량 삭제 방지
```

</div>

<div class="danger-box" markdown="1">

**❌ 중복 메시지 처리 미흡**

```java
// ❌ 문제: 멱등성 없이 그대로 처리
@KafkaListener(topics = "events")
public void handle(Order order) {
    paymentService.processPayment(order);  // 중복 호출 시 중복 결제!
}

// ✅ 멱등성 처리: 메시지 ID 기반 중복 체크
@KafkaListener(topics = "events")
public void handle(@Payload Order order, @Header(KafkaHeaders.MESSAGE_ID) String messageId) {
    if (idempotencyRepository.exists(messageId)) {
        return;  // 이미 처리됨
    }
    paymentService.processPayment(order);
    idempotencyRepository.record(messageId);
}
```

</div>

<div class="danger-box" markdown="1">

**❌ 스케줄러 단일 실패 지점**

```java
// 문제: 한 인스턴스에서만 폴링하면
// 그 인스턴스 다운 시 이벤트 발행 멈춤

// ✅ 해결: 분산 락 또는 전용 서비스
@Configuration
public class SchedulerConfig {
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        // ShedLock: DB 기반 분산 락
        return new JdbcLockProvider(dataSource);
    }
}

@Scheduled(fixedDelay = 1000)
@SchedulerLock(name = "publishOutbox")
public void publishPendingEvents() { ... }
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 발행 재시도 상한선 설정**

```java
// ❌ 문제: 무한 재시도로 Outbox 테이블 폭증
event.setStatus(OutboxStatus.PENDING);  // 계속 PENDING 상태

// ✅ 재시도 횟수 제한
@Column(name = "retry_count")
private Integer retryCount = 0;

if (event.getRetryCount() > 10) {
    event.setStatus(OutboxStatus.FAILED);  // DLQ로 옮기기
    dlqRepository.save(event);
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 폴링 주기와 발행 지연**

```java
// 폴링 주기 1000ms → 최대 1초 지연
@Scheduled(fixedDelay = 1000)  // 너무 김
public void publishPendingEvents() { ... }

// 실시간성 필요 시 CDC (Debezium) 사용
// 또는 이벤트 발행을 @Transactional 후에 바로 시도
@Transactional
public void placeOrder(Order order) {
    orderRepository.save(order);
    outboxRepository.save(event);
    // 트랜잭션 후에 즉시 발행 시도 (선택사항)
    publisherService.tryPublish(event);
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ Outbox 테이블 조회 성능**

```sql
-- ❌ 인덱스 없으면 조회 느림
SELECT * FROM outbox WHERE status = 'PENDING';

-- ✅ 복합 인덱스 필수
CREATE INDEX idx_status_created ON outbox(status, created_at);

-- ❌ 페이지네이션 없이 대량 조회
SELECT * FROM outbox WHERE status = 'PENDING';

-- ✅ LIMIT으로 배치 처리
SELECT * FROM outbox 
WHERE status = 'PENDING' 
ORDER BY created_at ASC 
LIMIT 1000;
```

</div>

---

### 정리

| 항목 | 설명 |
|------|------|
| **이중 쓰기 문제** | DB + 브로커 발행 중 하나만 성공하면 불일치 발생 |
| **해결 방법** | Outbox 테이블에 이벤트 저장 후 별도 프로세스 발행 |
| **발행 방식** | Polling (폴링) 또는 CDC (Debezium) |
| **전송 보장** | At-Least-Once (재시도로 중복 가능) |
| **필수 처리** | 소비자 멱등성, Outbox 정리, 재시도 제한 |
| **운영 주의** | 테이블 무한 증가 방지, 분산 락, 조회 성능 |

---

**관련 파일:**
- [kafka.md](kafka.md) — Kafka 기본과 메시지 발행
- [msa.md](msa.md) — MSA 아키텍처와 분산 트랜잭션
- [transaction.md](transaction.md) — 트랜잭션 개념과 격리 수준
