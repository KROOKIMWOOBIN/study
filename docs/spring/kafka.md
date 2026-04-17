## Kafka (Spring Kafka)

### 왜 쓰는가?

<div class="concept-box" markdown="1">

서비스 간 직접 API 호출은 **호출 대상이 죽으면 전체가 영향**을 받는다. ==Kafka==는 이벤트를 중간에 저장해 **비동기·느슨한 결합**을 제공한다.

</div>

| 구분 | 동기 호출 (REST) | 비동기 (Kafka) |
|------|----------------|---------------|
| 결합도 | 강결합 (상대 서버 가동 필요) | 느슨한 결합 |
| 장애 전파 | 하나 죽으면 전체 영향 | 독립적 |
| 처리량 | 상대 처리 속도 의존 | 대량 이벤트 버퍼링 가능 |
| 재처리 | 어려움 | 오프셋으로 재처리 가능 |

### 핵심 개념

| 개념 | 설명 |
|------|------|
| Topic | 이벤트 저장 카테고리 (채널) |
| Partition | Topic의 분산 단위. 병렬 처리 가능 |
| Producer | 메시지 발행자 |
| Consumer | 메시지 구독자 |
| Consumer Group | 같은 그룹 내 컨슈머는 파티션 분배 |
| Offset | Consumer가 읽은 위치 |

### 의존성

```markdown
implementation 'org.springframework.kafka:spring-kafka'
```

```markdown
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: my-service
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
```

### Producer — 이벤트 발행

```markdown
@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    public void placeOrder(OrderRequest request) {
        Order order = orderRepository.save(new Order(request));

        // 주문 완료 이벤트 발행
        OrderEvent event = new OrderEvent(order.getId(), order.getMemberId(), order.getAmount());
        kafkaTemplate.send("order-completed", String.valueOf(order.getId()), event);
    }
}
```

### Consumer — 이벤트 구독

```markdown
@Slf4j
@Service
public class NotificationConsumer {

    @KafkaListener(topics = "order-completed", groupId = "notification-service")
    public void handleOrderCompleted(OrderEvent event) {
        log.info("주문 완료 이벤트 수신: orderId={}", event.getOrderId());
        emailService.sendOrderConfirmation(event.getMemberId(), event.getOrderId());
    }
}

@Service
public class InventoryConsumer {

    @KafkaListener(topics = "order-completed", groupId = "inventory-service")
    public void handleOrderCompleted(OrderEvent event) {
        // 같은 토픽을 다른 Consumer Group이 독립적으로 구독
        inventoryService.decrease(event.getItemId());
    }
}
```

### 실무 패턴 — Transactional Outbox

DB와 Kafka 발행을 하나의 트랜잭션으로 묶는 패턴. Kafka 발행 실패 시 이벤트 유실을 방지한다.

> 심화 내용: [아웃박스 패턴](outbox.md) — 폴링/CDC 방식, 멱등성 처리, 운영 전략

```markdown
@Transactional
public void placeOrder(OrderRequest request) {
    Order order = orderRepository.save(new Order(request));

    // 같은 트랜잭션에 outbox 테이블에 이벤트 저장
    OutboxEvent outbox = new OutboxEvent("order-completed", toJson(order));
    outboxRepository.save(outbox);
    // 별도 스케줄러가 outbox를 읽어 Kafka에 발행
}
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 메시지 중복 수신 | 컨슈머 재시작 시 재처리 | 멱등성(Idempotent) 처리 구현 |
| 순서 보장 | 파티션이 여러 개면 순서 보장 안 됨 | 같은 키는 같은 파티션으로 |
| 트랜잭션과 Kafka 발행 분리 | DB 커밋 후 Kafka 발행 실패 시 유실 | Outbox 패턴 |
| 컨슈머 오류 무한 재처리 | Dead Letter Queue 없으면 무한 루프 | DLQ(Dead Letter Topic) 설정 |
