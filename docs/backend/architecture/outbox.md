# Transactional Outbox Pattern

## 왜 쓰는지

DB 저장과 메시지 발행을 함께 처리해야 하는 경우가 많습니다.

```text
1. 주문을 DB에 저장
2. 주문 생성 이벤트를 메시지 브로커에 발행
```

문제는 DB와 메시지 브로커가 서로 다른 시스템이라는 점입니다. DB 저장은 성공했는데 메시지 발행이 실패하면 다른 서비스는 주문 생성 사실을 알지 못합니다.

<div class="concept-box" markdown="1">

**핵심**: Transactional Outbox는 비즈니스 데이터와 발행할 이벤트를 같은 DB 트랜잭션에 저장하고, 별도 프로세스가 Outbox 테이블을 읽어 메시지 브로커로 발행하는 패턴입니다.

</div>

## 어떻게 쓰는지

### 1. Outbox 테이블

```sql
CREATE TABLE outbox (
    id BIGINT PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP NULL
);

CREATE INDEX idx_outbox_status_created
ON outbox(status, created_at);
```

### 2. 같은 DB 트랜잭션에 저장

```text
BEGIN
  INSERT INTO orders (...)
  INSERT INTO outbox (
    aggregate_type,
    aggregate_id,
    event_type,
    payload,
    status,
    created_at
  ) VALUES (
    'ORDER',
    '1001',
    'ORDER_CREATED',
    '{...}',
    'PENDING',
    NOW()
  )
COMMIT
```

주문과 Outbox 이벤트가 같은 DB에 저장되므로 둘 중 하나만 성공하는 상황을 줄입니다.

### 3. Relay가 이벤트 발행

```text
1. PENDING 이벤트 조회
2. 메시지 브로커로 발행
3. 성공하면 PUBLISHED로 변경
4. 실패하면 재시도 대상 유지 또는 FAILED 처리
```

```sql
SELECT *
FROM outbox
WHERE status = 'PENDING'
ORDER BY created_at ASC
LIMIT 100;
```

### 4. Polling 또는 CDC

| 방식 | 설명 |
|------|------|
| Polling | 애플리케이션/배치가 Outbox 테이블을 주기적으로 조회 |
| CDC | DB 변경 로그를 읽어 이벤트로 변환 |

## 언제 쓰는지

| 상황 | Outbox 적합도 | 이유 |
|------|---------------|------|
| **DB 변경 후 이벤트 발행 필수** | 높음 | 이벤트 유실 방지 |
| **서비스 간 최종 일관성 필요** | 높음 | 변경 사실을 안정적으로 전달 |
| **메시지 브로커 장애 대비 필요** | 높음 | 이벤트가 DB에 남아 재시도 가능 |
| **단순 알림 실패 허용** | 낮음 | 운영 복잡도 대비 이득이 작음 |
| **강한 즉시 일관성 필요** | 낮음 | Outbox는 최종 일관성 패턴 |

## 장점

| 장점 | 설명 |
|------|------|
| **이벤트 유실 방지** | DB 변경과 이벤트 기록을 함께 저장 |
| **재시도 가능** | 브로커 장애 시에도 Outbox에 이벤트가 남음 |
| **감사 추적** | 어떤 이벤트가 언제 발행됐는지 기록 가능 |
| **서비스 결합 감소** | 이벤트 기반으로 후속 처리를 분리 |

## 단점

| 단점 | 설명 |
|------|------|
| **추가 테이블 필요** | Outbox 테이블 생성과 관리 필요 |
| **발행 지연** | Polling 방식은 약간의 지연 발생 |
| **중복 발행 가능** | 발행 성공 후 상태 변경 실패 시 재발행 가능 |
| **정리 작업 필요** | 오래된 이벤트를 삭제/아카이빙해야 함 |
| **운영 복잡도 증가** | Relay, 재시도, DLQ, 모니터링 필요 |

## 특징

### 1. At-least-once 전달

Outbox는 보통 최소 한 번 전달을 보장합니다. 즉 유실은 줄이지만 중복은 발생할 수 있습니다.

```text
발행 성공
상태 업데이트 실패
다음 Relay 실행 때 같은 이벤트 재발행
```

그래서 소비자는 멱등하게 처리해야 합니다.

### 2. 소비자 멱등성

```sql
CREATE TABLE processed_event (
    event_id VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);
```

소비자는 이벤트 처리 전에 `event_id`가 이미 처리됐는지 확인합니다.

### 3. Outbox 생명주기

```text
PENDING -> PUBLISHED
        -> FAILED
```

발행 실패가 반복되면 무한 재시도 대신 FAILED나 DLQ로 격리합니다.

### 4. 조회 성능

Outbox는 계속 쌓이는 테이블입니다. `status`, `created_at` 기준 인덱스가 없으면 Relay 조회가 느려질 수 있습니다.

## 주의할 점

<div class="warning-box" markdown="1">

**Outbox는 중복을 없애는 패턴이 아닙니다.**

유실을 줄이는 대신 중복 가능성을 받아들이고, 소비자 멱등성으로 해결합니다.

</div>

<div class="danger-box" markdown="1">

**Outbox 테이블을 방치하면 계속 커집니다.**

발행 완료 이벤트는 일정 기간 보관 후 삭제하거나 아카이빙해야 합니다.

</div>

<div class="warning-box" markdown="1">

**Relay가 여러 개면 같은 이벤트를 동시에 잡을 수 있습니다.**

행 잠금, 상태 전이, 분산 락, 원자적 업데이트 중 하나로 중복 발행 가능성을 줄입니다.

</div>

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| **event_id를 전역 고유값으로 생성** | 소비자 멱등성 처리 |
| **status + created_at 인덱스 생성** | PENDING 이벤트 조회 성능 확보 |
| **배치 크기 제한** | 한 번에 너무 많은 이벤트 처리 방지 |
| **재시도 횟수 제한** | 실패 이벤트가 무한 반복되지 않게 함 |
| **DLQ 또는 FAILED 상태 운영** | 문제 이벤트 격리 |
| **오래된 이벤트 정리** | 테이블 비대화 방지 |
| **발행 lag 모니터링** | 이벤트 지연 감지 |

## 실무에서는?

| 상황 | 적용 예 |
|------|---------|
| **주문 생성 이벤트** | 주문 저장과 이벤트 기록을 같은 트랜잭션에 저장 |
| **결제 완료 이벤트** | 결제 상태 변경 후 정산/알림 서비스에 전달 |
| **포인트 적립 이벤트** | 적립 기록과 후속 이벤트 발행 정합성 확보 |
| **검색 색인 갱신** | 원본 데이터 변경 이벤트를 검색 시스템에 전달 |
| **MSA 데이터 동기화** | 서비스 간 최종 일관성 유지 |

## 정리

| 항목 | 설명 |
|------|------|
| **문제** | DB 저장과 메시지 발행의 이중 쓰기 |
| **해결** | 이벤트를 Outbox 테이블에 먼저 저장 |
| **발행 방식** | Polling 또는 CDC |
| **전달 보장** | 보통 At-least-once |
| **필수 설계** | 소비자 멱등성, 재시도 제한, 테이블 정리 |

---

**관련 파일:**
- [Kafka](../infra/kafka.md) — 메시지 브로커 기반 이벤트 발행
- [MSA](msa.md) — 서비스 간 최종 일관성
- [모니터링](../operations/monitoring.md) — lag와 실패 이벤트 관찰
