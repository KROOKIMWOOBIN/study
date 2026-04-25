# 모니터링

## 왜 쓰는지

서비스 장애는 대부분 사용자가 먼저 발견하면 늦습니다. 모니터링은 시스템 상태를 지표와 로그로 관찰해 장애를 빨리 감지하고 원인을 찾기 위한 운영 활동입니다.

<div class="concept-box" markdown="1">

**핵심**: 모니터링은 CPU 사용률을 보는 것이 전부가 아니라, 서비스가 정상적으로 사용자 요청을 처리하고 있는지 관찰하는 체계입니다.

</div>

## 어떻게 쓰는지

### 1. Metrics

숫자로 수집되는 지표입니다.

```text
http_requests_total{method="GET", status="200"} 12039
http_request_duration_seconds_bucket{le="0.5"} 9321
process_cpu_usage 0.42
```

대표 지표:

| 지표 | 의미 |
|------|------|
| 요청 수 | 트래픽 변화 |
| 에러율 | 장애 징후 |
| 응답 시간 | 사용자 경험 |
| CPU/Memory | 리소스 사용량 |
| DB connection | DB 병목 |
| queue lag | 비동기 처리 지연 |

### 2. Logs

이벤트와 에러의 상세 기록입니다.

```json
{
  "level": "ERROR",
  "traceId": "abc-123",
  "message": "payment approval failed",
  "orderId": 1001
}
```

로그는 장애 원인 분석에 강하고, 메트릭은 장애 감지에 강합니다.

### 3. Traces

하나의 요청이 여러 서비스와 저장소를 거치는 흐름을 추적합니다.

```text
GET /orders/1001
 ├─ order-service
 ├─ payment-service
 └─ database
```

분산 환경에서는 traceId를 로그에도 함께 남겨야 요청 흐름을 따라가기 쉽습니다.

### 4. Health Check

```http
GET /health

{
  "status": "UP",
  "database": "UP",
  "redis": "UP",
  "messageBroker": "UP"
}
```

단순 프로세스 생존 여부와 실제 의존성 상태는 구분해서 봅니다.

## 언제 쓰는지

| 상황 | 필요한 관찰 |
|------|-------------|
| **장애 조기 감지** | 에러율, 응답 시간, health check |
| **성능 저하 분석** | latency, DB query, CPU, memory |
| **트래픽 증가 대응** | 요청 수, 처리량, queue lag |
| **배포 영향 확인** | 배포 전후 에러율과 응답 시간 |
| **비동기 처리 확인** | broker lag, 실패 메시지 수 |
| **용량 계획** | 리소스 사용 추세 |

## 장점

| 장점 | 설명 |
|------|------|
| **장애 감지** | 사용자가 알기 전에 문제를 발견 |
| **원인 분석** | 로그와 trace로 병목 위치 확인 |
| **운영 판단** | 증설, 롤백, 장애 대응 근거 확보 |
| **성능 개선** | 느린 구간을 수치로 확인 |
| **재발 방지** | 장애 패턴을 알림과 대시보드에 반영 |

## 단점

| 단점 | 설명 |
|------|------|
| **데이터 비용** | 로그와 메트릭 저장 비용 발생 |
| **노이즈** | 의미 없는 알림이 많으면 무시하게 됨 |
| **설계 필요** | 무엇을 볼지 정하지 않으면 지표만 쌓임 |
| **민감 정보 위험** | 로그에 개인정보나 토큰이 남을 수 있음 |

## 특징

### 1. Golden Signals

| 신호 | 설명 |
|------|------|
| Latency | 요청 처리 시간 |
| Traffic | 요청량 |
| Errors | 실패율 |
| Saturation | 리소스 포화도 |

서비스 운영의 기본 대시보드는 이 네 가지를 중심으로 잡습니다.

### 2. SLI/SLO

| 용어 | 설명 |
|------|------|
| SLI | 실제 측정 지표 |
| SLO | 달성해야 하는 목표 |

예시:

```text
SLI: 5xx가 아닌 요청 비율
SLO: 99.9% 이상
```

### 3. Alert

알림은 "사람이 행동해야 하는 상황"에만 걸어야 합니다.

```text
5분 동안 5xx 비율 > 5%
10분 동안 p95 latency > 1s
consumer lag > 10000
```

### 4. Dashboard

대시보드는 장애 대응 시 바로 판단할 수 있어야 합니다.

| 대시보드 | 포함 지표 |
|----------|-----------|
| 서비스 | 요청 수, 에러율, latency |
| 인프라 | CPU, memory, disk, network |
| DB | connection, slow query, lock wait |
| 메시징 | publish rate, consume rate, lag |

## 주의할 점

<div class="warning-box" markdown="1">

**모든 지표에 알림을 걸지 않습니다.**

알림은 적을수록 강합니다. 사람이 즉시 대응해야 하는 지표부터 설정합니다.

</div>

<div class="danger-box" markdown="1">

**로그에 민감 정보를 남기지 않습니다.**

비밀번호, 토큰, 주민번호, 카드번호 같은 값은 마스킹하거나 기록하지 않습니다.

</div>

<div class="warning-box" markdown="1">

**평균 응답 시간만 보면 위험합니다.**

평균은 일부 느린 요청을 숨길 수 있습니다. p95, p99 같은 percentile을 함께 봅니다.

</div>

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| **요청 수/에러율/latency 기본 수집** | 서비스 상태를 빠르게 판단 |
| **traceId를 로그에 포함** | 요청 흐름 추적 |
| **p95/p99 latency 확인** | 느린 사용자 경험 파악 |
| **알림은 행동 기준으로 설정** | 알림 피로 방지 |
| **배포 이벤트 표시** | 배포와 장애 상관관계 파악 |
| **민감 정보 마스킹** | 보안 사고 방지 |
| **대시보드 정기 점검** | 오래된 지표와 무의미한 알림 제거 |

## 실무에서는?

| 상황 | 확인 지표 |
|------|-----------|
| **응답이 느림** | p95 latency, DB query, CPU, external API latency |
| **에러 증가** | 5xx rate, exception log, 배포 시점 |
| **비동기 처리 지연** | consumer lag, 실패 메시지 수 |
| **DB 병목** | connection pool, lock wait, slow query |
| **캐시 효과 확인** | cache hit ratio, Redis memory |
| **트래픽 급증** | request rate, queue depth, saturation |

## 정리

| 항목 | 설명 |
|------|------|
| **목적** | 장애 감지와 원인 분석 |
| **핵심 데이터** | Metrics, Logs, Traces |
| **기본 신호** | Latency, Traffic, Errors, Saturation |
| **주의** | 알림 노이즈, 민감 정보 로그, 평균 지표 착시 |

---

**관련 파일:**
- [Kafka](../infra/kafka.md) — consumer lag 관찰
- [Redis](../infra/redis.md) — cache hit ratio와 memory 관찰
