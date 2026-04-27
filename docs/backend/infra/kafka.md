# Kafka

<div class="concept-box" markdown="1">

**Kafka**: 이벤트를 토픽에 append-only log로 저장하고, consumer group이 offset을 기준으로 독립적으로 읽어가는 분산 이벤트 스트리밍 플랫폼.

</div>

서비스가 다른 서비스에 직접 API를 호출하면 장애, 지연, 트래픽 증가가 호출한 쪽까지 전파됩니다. Kafka는 이벤트를 중간에 저장해 producer와 consumer를 분리하고, 소비자가 자기 속도에 맞춰 처리할 수 있게 합니다.

Kafka는 단순 큐라기보다 **보존 가능한 이벤트 로그**입니다. 그래서 실무에서는 producer, broker, consumer, sink DB 사이의 실패 지점을 나누어 보고, 중복 처리와 재처리를 기본 시나리오로 설계합니다.

---

## 먼저 보는 큰 그림

```text
Producer는 topic에 쓴다.
Kafka는 partition에 순서대로 보관한다.
Consumer group은 offset을 기억하며 읽는다.
서로 다른 consumer group은 같은 이벤트를 따로 읽을 수 있다.
```

| 읽고 싶은 내용 | 바로가기 | 핵심 질문 |
|----------------|----------|-----------|
| Kafka의 구성요소와 내부 구조 | [기본 개념과 구조](./kafka/기본개념.md) | topic, partition, offset, broker, ISR이 무엇인가? |
| 토픽 생성, 이벤트 설계, producer 설정 | [Producer와 이벤트 설계](./kafka/producer.md) | 어떤 key와 설정으로 이벤트를 안전하게 발행할 것인가? |
| consumer 처리, offset commit, rebalance | [Consumer와 전달 보장](./kafka/consumer.md) | 중복 소비와 순서 보장을 어떻게 다룰 것인가? |
| 장애 대응, 지표, 운영 체크리스트 | [운영과 장애 대응](./kafka/운영장애대응.md) | lag, poison pill, broker 장애를 어떤 순서로 볼 것인가? |

---

## 언제 쓰는지

| 상황 | 적합도 | 이유 |
|------|--------|------|
| 서비스 간 비동기 연동 | 높음 | 직접 호출 결합과 장애 전파를 줄임 |
| 이벤트 기반 아키텍처 | 높음 | 여러 consumer group이 같은 이벤트를 각자 활용 가능 |
| 대용량 로그·클릭 스트림 | 높음 | partition과 batch 기반으로 처리량 확장 |
| 재처리 필요 | 높음 | retention 기간 안에서 offset 조정으로 다시 읽을 수 있음 |
| 즉시 응답이 필요한 동기 조회 | 낮음 | 요청-응답 저장소가 아니라 이벤트 로그임 |
| 강한 단일 트랜잭션 | 낮음 | 기본은 최종 일관성이고 중복 처리를 고려해야 함 |

## 장점과 단점

| 구분 | 내용 |
|------|------|
| 장점 | 느슨한 결합, 높은 처리량, 이벤트 보존, 재처리, 수평 확장, 장애 흡수 |
| 단점 | 운영 복잡도, 중복 처리 가능성, partition 내부로 제한되는 순서 보장, schema 관리, 최종 일관성 |

## 실무 핵심

| 주제 | 기준 |
|------|------|
| Topic | partition, replication factor, retention, min ISR을 업무 기준으로 정함 |
| Producer | 중요한 이벤트는 `acks=all`, `enable.idempotence=true`를 기본으로 검토 |
| Key | 순서 보장이 필요한 aggregate 기준으로 선택하되 hot partition을 확인 |
| Consumer | 처리 성공 후 offset commit, `eventId` 기반 멱등 처리 |
| 장애 | DLQ, lag 알림, offset reset dry-run, schema compatibility를 준비 |
| 정합성 | DB 저장과 이벤트 발행 사이에는 [아웃박스 패턴](../architecture/outbox.md)을 고려 |

## 정리

| 항목 | 설명 |
|------|------|
| 핵심 단위 | Topic, Partition, Offset, Consumer Group |
| 가장 큰 강점 | 비동기 처리, 높은 처리량, 이벤트 보존과 재처리 |
| 가장 큰 주의점 | 중복 처리, partition 단위 순서, schema 변경, lag, rebalance |
| 운영 핵심 | ISR, `acks`, offset commit, DLQ, lag, retention |

---

**관련 파일:**
- [아웃박스 패턴](../architecture/outbox.md) — DB 변경과 이벤트 발행 정합성
- [MSA](../architecture/msa.md) — 서비스 간 비동기 통신
- [모니터링](../operations/monitoring.md) — lag와 처리량 관찰
