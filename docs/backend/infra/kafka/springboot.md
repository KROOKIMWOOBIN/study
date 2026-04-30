# Spring Boot Kafka 연동

Spring Boot에서 Kafka를 사용할 때는 **producer 설정, consumer offset commit, error handler, DLQ, serializer**를 먼저 정해야 합니다. 단순히 `@KafkaListener`만 붙이면 중복 처리와 장애 대응이 빈틈으로 남기 쉽습니다.

<div class="concept-box" markdown="1">

**Spring for Apache Kafka**는 Kafka client를 Spring 방식으로 쓰게 해주는 추상화입니다. Producer는 `KafkaTemplate`, Consumer는 `@KafkaListener`와 listener container를 주로 사용합니다.

</div>

## 왜 쓰는지

Kafka Java client를 직접 쓰면 poll loop, commit, error handling, retry를 모두 직접 관리해야 합니다. Spring Kafka는 반복적인 설정을 줄이고, Spring transaction, bean lifecycle, 관찰 지표와 연동하기 쉽게 해줍니다.

## 의존성

```groovy
dependencies {
    implementation 'org.springframework.kafka:spring-kafka'
}
```

Spring Boot를 사용하면 Boot 버전에 맞는 Spring Kafka와 Kafka client 버전이 dependency management로 맞춰집니다.

## Producer 설정

```yaml
spring:
  kafka:
    bootstrap-servers: kafka-1:9092,kafka-2:9092,kafka-3:9092
    producer:
      acks: all
      retries: 10
      properties:
        enable.idempotence: true
        delivery.timeout.ms: 120000
        linger.ms: 5
        compression.type: lz4
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

```java
@Service
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderCreatedEvent event) {
        kafkaTemplate.send("order-events", event.orderId(), event);
    }
}
```

| 설정 | 의미 | 주의 |
|------|------|------|
| `acks=all` | ISR 복제를 기다림 | `min.insync.replicas`와 같이 설계 |
| `enable.idempotence=true` | producer 재시도 중 log 중복 감소 | consumer 중복 반영 해결은 아님 |
| `linger.ms` | batch를 위해 짧게 대기 | 처리량과 지연의 trade-off |
| `compression.type` | 압축 | CPU와 network 비용 절충 |

## Consumer 설정

실무에서는 자동 commit보다 **처리 성공 후 수동 commit**을 기본 후보로 둡니다.

```yaml
spring:
  kafka:
    consumer:
      group-id: inventory-service
      enable-auto-commit: false
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.event"
        isolation.level: read_committed
    listener:
      ack-mode: manual
```

```java
@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-events", groupId = "inventory-service")
    public void consume(OrderCreatedEvent event, Acknowledgment ack) {
        inventoryService.apply(event);
        ack.acknowledge();
    }
}
```

| 설정 | 의미 | 주의 |
|------|------|------|
| `enable-auto-commit=false` | 처리 후 직접 commit | 유실 위험 감소 |
| `ack-mode=manual` | listener에서 acknowledge | 실패 시 commit하지 않음 |
| `auto-offset-reset` | 저장 offset 없을 때 시작점 | 운영 group에서는 실수 영향 큼 |
| `max.poll.records` | 한 번에 처리할 record 수 | 처리 시간이 길면 줄임 |

## Error Handler와 DLQ

계속 실패하는 메시지는 consumer를 멈추게 만들 수 있으므로 retry 횟수를 제한하고 DLQ로 격리합니다.

```java
@Configuration
public class KafkaConsumerConfig {

    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            template,
            (record, exception) -> new TopicPartition(record.topic() + ".DLQ", record.partition())
        );

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }
}
```

DLQ에는 원본 topic, partition, offset, key, exception 정보를 남겨야 나중에 원인 수정 후 재처리할 수 있습니다.

## 언제 쓰는지

| 상황 | Spring Kafka 적합도 | 이유 |
|------|---------------------|------|
| Spring Boot 서비스에서 이벤트 발행 | 높음 | `KafkaTemplate`으로 단순화 |
| 업무 consumer 구현 | 높음 | `@KafkaListener`, error handler 활용 |
| 복잡한 stream join/aggregation | 조건부 | Kafka Streams 또는 별도 stream processing 검토 |
| 매우 세밀한 poll 제어 | 조건부 | native KafkaConsumer 직접 사용 검토 |
| 단순 테스트용 publish | 높음 | Embedded Kafka나 Testcontainers 활용 가능 |

## 장점

| 장점 | 설명 |
|------|------|
| 설정 통합 | Boot property로 producer/consumer 설정 관리 |
| listener 추상화 | poll loop 구현 부담 감소 |
| error handling 지원 | retry, backoff, DLQ 구성 가능 |
| Spring 생태계 연동 | transaction, metrics, test와 연계 쉬움 |

## 단점

| 단점 | 설명 |
|------|------|
| 추상화 오해 | offset commit과 retry 동작을 모르면 사고 가능 |
| 설정 조합 복잡 | ack mode, error handler, transaction 조합 이해 필요 |
| serializer 주의 | JSON 타입 정보, trusted package, schema 변경 관리 필요 |
| blocking 처리 위험 | listener thread에서 긴 작업을 하면 rebalance 발생 가능 |

## 특징

| 특징 | 설명 |
|------|------|
| `KafkaTemplate` | producer 발행 추상화 |
| `@KafkaListener` | consumer listener 선언 |
| Listener Container | poll, thread, commit, rebalance 관리 |
| Error Handler | retry, seek, DLQ 처리 |
| Ack Mode | offset commit 시점 제어 |

## 주의할 점

| 주의 | 설명 |
|------|------|
| 자동 commit을 기본으로 두지 않기 | 처리 실패와 commit 시점이 어긋날 수 있음 |
| listener에서 오래 blocking하지 않기 | `max.poll.interval.ms` 초과로 rebalance 가능 |
| DLQ 없이 무한 재시도 금지 | poison pill이 전체 소비를 막음 |
| JSON 역직렬화 신뢰 패키지 확인 | 보안과 호환성 문제 |
| business transaction과 offset commit 분리 이해 | DB commit 성공 후 offset commit 전 장애 시 중복 가능 |
| consumer group id를 운영에서 함부로 변경 금지 | 새 group처럼 다시 읽을 수 있음 |

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| 중요한 consumer는 수동 commit 사용 | 처리 성공 후 offset 저장 |
| consumer 로직은 멱등하게 구현 | 중복 소비 대비 |
| retry 횟수 제한과 DLQ 구성 | poison pill 격리 |
| `eventId`를 DB unique로 저장 | 중복 반영 차단 |
| `max.poll.records`를 처리 시간 기준으로 조정 | rebalance 방지 |
| 통합 테스트에 Kafka 포함 | serializer, listener, DLQ 동작 확인 |

## 실무에서는?

| 사용처 | 설계 기준 |
|--------|-----------|
| 주문 이벤트 발행 | outbox 또는 transaction 경계 검토 |
| 재고 consumer | 수동 commit, 멱등성, 상태 전이 검증 |
| 알림 consumer | idempotency key, retry, DLQ |
| 검색 색인 consumer | batch 처리, 재처리 가능 retention |
| 외부 API consumer | timeout, rate limit, DLQ, 보정 작업 |

## 정리

| 항목 | 설명 |
|------|------|
| Producer 도구 | `KafkaTemplate` |
| Consumer 도구 | `@KafkaListener` |
| 운영 핵심 | 수동 commit, error handler, DLQ, 멱등성 |
| 가장 큰 주의점 | Spring 추상화가 Kafka 중복 처리 문제를 없애주지는 않음 |
| 실무 기준 | listener 코드는 실패와 재처리를 기본 시나리오로 작성 |

---

**관련 파일:**
- [Producer와 이벤트 설계](./producer.md)
- [Consumer와 전달 보장](./consumer.md)
- [장애 대응과 트러블슈팅](./운영장애대응.md)

--8<-- "includes/kafka/producer-consumer.md"
