# Backend 학습 노트

## 목차

| 분류 | 주제 | 먼저 볼 문서 |
|------|------|--------------|
| **인프라** | 캐시, 메시징, 외부 시스템 연동 | [Redis](./infra/redis.md), [Kafka](./infra/kafka.md) |
| **아키텍처** | 구조 설계, 서비스 분리, 이벤트 발행 안정성 | [아키텍처 패턴](./architecture/architecture.md), [MSA](./architecture/msa.md), [아웃박스 패턴](./architecture/outbox.md) |
| **운영** | 장애 감지, 동시성, 분산 환경 제어 | [모니터링](./operations/monitoring.md), [동시성 제어](./operations/concurrency.md) |

## 상황별 선택

| 상황 | 확인할 문서 |
|------|-------------|
| DB 부하를 줄이고 응답 속도를 높여야 함 | [Redis](./infra/redis.md) |
| 서비스 간 결합을 낮추고 비동기로 처리해야 함 | [Kafka](./infra/kafka.md) |
| DB 저장과 이벤트 발행을 안전하게 묶어야 함 | [아웃박스 패턴](./architecture/outbox.md) |
| 모놀리식에서 서비스 분리를 고민함 | [MSA](./architecture/msa.md) |
| 코드 배치와 계층 기준이 흔들림 | [아키텍처 패턴](./architecture/architecture.md) |
| 여러 서버에서 같은 자원을 동시에 처리함 | [동시성 제어](./operations/concurrency.md) |
| 장애를 빨리 감지하고 원인을 추적해야 함 | [모니터링](./operations/monitoring.md) |
