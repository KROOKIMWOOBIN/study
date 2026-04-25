# Kafka

## 왜 쓰는지

서비스가 다른 서비스에 직접 API를 호출하면 호출 대상 장애, 응답 지연, 일시적 트래픽 증가가 호출한 쪽까지 전파됩니다. Kafka는 이벤트를 중간에 저장해 생산자와 소비자를 느슨하게 연결합니다.

<div class="concept-box" markdown="1">

**핵심**: Kafka는 이벤트를 토픽에 저장하고 여러 소비자가 각자 필요한 시점에 읽어가는 분산 메시지 스트리밍 플랫폼입니다.

</div>

## 어떻게 쓰는지

### 토픽 생성

```bash
kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic order-created \
  --partitions 3 \
  --replication-factor 1
```

### 이벤트 발행

```json
{
  "eventId": "evt-1001",
  "type": "ORDER_CREATED",
  "orderId": 1001,
  "userId": 10,
  "createdAt": "2026-04-25T10:15:00Z"
}
```

Producer는 이벤트를 특정 토픽에 보냅니다. 같은 순서를 지켜야 하는 이벤트는 같은 Key를 사용해 같은 파티션으로 보내는 것이 일반적입니다.

```text
topic: order-created
key: order-1001
value: OrderCreatedEvent
```

### 이벤트 소비

Consumer는 토픽을 구독하고, 처리한 위치를 offset으로 기록합니다.

```text
order-created topic
 ├─ notification-service group
 ├─ inventory-service group
 └─ analytics-service group
```

서로 다른 Consumer Group은 같은 이벤트를 독립적으로 읽을 수 있습니다.

## 언제 쓰는지

| 상황 | Kafka 적합도 | 이유 |
|------|--------------|------|
| **서비스 간 비동기 연동** | 높음 | 직접 호출 결합 감소 |
| **이벤트 기반 아키텍처** | 높음 | 여러 소비자가 같은 이벤트 활용 |
| **대용량 로그/클릭 스트림** | 높음 | 높은 처리량과 순차 저장 |
| **재처리 필요** | 높음 | offset을 기준으로 다시 읽기 가능 |
| **즉시 응답이 필요한 동기 조회** | 낮음 | 요청-응답 모델이 아님 |
| **강한 트랜잭션 일관성** | 낮음 | 기본은 최종 일관성 |

## 장점

| 장점 | 설명 |
|------|------|
| **느슨한 결합** | 생산자와 소비자가 서로 직접 알 필요가 적음 |
| **높은 처리량** | 파티션 기반 병렬 처리 가능 |
| **이벤트 보존** | 소비 후에도 일정 기간 메시지 유지 |
| **수평 확장** | 파티션과 Consumer Group으로 확장 |
| **재처리 가능** | offset 조정으로 과거 이벤트를 다시 읽을 수 있음 |

## 단점

| 단점 | 설명 |
|------|------|
| **운영 복잡도** | 브로커, 파티션, 복제, lag 관리 필요 |
| **중복 처리 가능성** | 재시도와 장애 복구 과정에서 중복 소비 가능 |
| **순서 보장 범위 제한** | 토픽 전체가 아니라 파티션 내부에서만 순서 보장 |
| **스키마 관리 필요** | 이벤트 구조 변경이 소비자에 영향을 줌 |
| **최종 일관성** | 즉시 모든 서비스가 같은 상태가 아닐 수 있음 |

## 특징

### 1. Topic과 Partition

토픽은 이벤트를 분류하는 논리적 이름이고, 파티션은 토픽을 나누어 저장하는 단위입니다.

```text
topic: order-created
 ├─ partition 0
 ├─ partition 1
 └─ partition 2
```

같은 Key를 가진 메시지는 같은 파티션으로 들어가므로 Key 단위 순서를 유지할 수 있습니다.

### 2. Broker와 Replication

Broker는 Kafka 서버입니다. Replication은 파티션 복제본을 여러 Broker에 두어 장애에 대비하는 방식입니다.

```text
partition 0 leader  -> broker-1
partition 0 follower -> broker-2
```

Producer와 Consumer는 leader partition을 기준으로 읽고 씁니다.

### 3. Offset

Offset은 파티션 안에서 메시지 위치를 나타내는 번호입니다. Consumer Group은 어디까지 읽었는지 offset을 저장합니다.

```text
partition 0
offset: 0  1  2  3  4
        A  B  C  D  E
```

### 4. Consumer Group

같은 Consumer Group 안에서는 파티션을 나누어 처리합니다.

```text
topic partitions: p0, p1, p2

consumer group: inventory-service
 ├─ consumer-1 -> p0, p1
 └─ consumer-2 -> p2
```

Consumer 수가 파티션 수보다 많으면 놀고 있는 Consumer가 생길 수 있습니다.

### 5. 전달 보장

| 방식 | 설명 | 주의 |
|------|------|------|
| At-most-once | 처리 전 offset commit | 유실 가능 |
| At-least-once | 처리 후 offset commit | 중복 가능 |
| Exactly-once | 트랜잭션/멱등 producer 조합 | 조건과 설정이 복잡 |

실무에서는 At-least-once를 기준으로 두고 소비자 멱등성을 설계하는 경우가 많습니다.

## 주의할 점

<div class="warning-box" markdown="1">

**Kafka 메시지는 중복될 수 있습니다.**

결제, 포인트, 재고 차감처럼 중복 처리에 민감한 로직은 `eventId`를 저장해 이미 처리한 이벤트인지 확인해야 합니다.

</div>

<div class="warning-box" markdown="1">

**순서는 파티션 안에서만 보장됩니다.**

주문별 순서가 중요하면 주문 ID를 Key로 사용해 같은 주문 이벤트가 같은 파티션에 들어가도록 합니다.

</div>

<div class="danger-box" markdown="1">

**DB 저장과 이벤트 발행은 한 번에 원자적으로 처리되지 않습니다.**

DB 저장 성공 후 Kafka 발행이 실패하면 이벤트가 유실될 수 있습니다. 중요한 이벤트는 Outbox 같은 패턴을 고려합니다.

</div>

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| **이벤트에 고유 ID 포함** | 중복 소비 방지 |
| **Key 설계 먼저 결정** | 순서 보장 단위와 파티션 분산에 영향 |
| **Consumer는 멱등하게 구현** | 재처리와 중복 메시지에 안전 |
| **DLQ 준비** | 계속 실패하는 메시지를 격리 |
| **Schema 관리** | 이벤트 필드 변경 시 소비자 호환성 유지 |
| **lag 모니터링** | 소비 지연과 장애 징후 파악 |
| **중요 이벤트는 Outbox 고려** | DB 변경과 이벤트 발행 불일치 방지 |

## 실무에서는?

| 사용처 | 예시 |
|--------|------|
| **주문 이벤트 전파** | 주문 생성 후 결제, 재고, 알림 서비스가 각각 처리 |
| **알림 비동기 처리** | 메일, 푸시, 문자 발송을 요청 흐름에서 분리 |
| **로그 수집** | 서버 로그, 클릭 로그, 행동 이벤트 수집 |
| **데이터 동기화** | 검색 색인, 분석 저장소, 캐시 갱신 |
| **이벤트 소싱 일부 흐름** | 상태 변경 이벤트를 장기간 보관하고 재처리 |

## 정리

| 항목 | 설명 |
|------|------|
| **Kafka** | 분산 메시지 스트리밍 플랫폼 |
| **핵심 단위** | Topic, Partition, Offset, Consumer Group |
| **강점** | 비동기 처리, 높은 처리량, 재처리 |
| **주의** | 중복 처리, 파티션 순서, 스키마 변경, lag 관리 |

---

**관련 파일:**
- [아웃박스 패턴](../architecture/outbox.md) — DB 변경과 이벤트 발행 정합성
- [MSA](../architecture/msa.md) — 서비스 간 비동기 통신
- [모니터링](../operations/monitoring.md) — lag와 처리량 관찰
