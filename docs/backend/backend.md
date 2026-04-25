# Backend 학습 노트

## 왜 분리했는지

Spring은 프레임워크 사용법을 배우는 영역이고, Redis, Kafka, MSA, Outbox 같은 주제는 특정 프레임워크보다 넓은 **백엔드 시스템 설계와 운영** 영역입니다.

<div class="concept-box" markdown="1">

**핵심**: Backend 카테고리는 Spring 자체 기능이 아니라, 서비스를 안정적으로 만들기 위해 필요한 인프라, 아키텍처, 운영 주제를 따로 모아두는 공간입니다.

</div>

## 어떻게 볼 것인지

| 분류 | 주제 | 먼저 볼 문서 |
|------|------|--------------|
| **인프라** | 캐시, 메시징, 외부 시스템 연동 | [Redis](./infra/redis.md), [Kafka](./infra/kafka.md) |
| **아키텍처** | 구조 설계, 서비스 분리, 이벤트 발행 안정성 | [아키텍처 패턴](./architecture/architecture.md), [MSA](./architecture/msa.md), [아웃박스 패턴](./architecture/outbox.md) |
| **운영** | 장애 감지, 동시성, 분산 환경 제어 | [모니터링](./operations/monitoring.md), [동시성 제어](./operations/concurrency.md) |

## 언제 보는지

| 상황 | 확인할 문서 |
|------|-------------|
| DB 부하를 줄이고 응답 속도를 높여야 함 | [Redis](./infra/redis.md) |
| 서비스 간 결합을 낮추고 비동기로 처리해야 함 | [Kafka](./infra/kafka.md) |
| DB 저장과 이벤트 발행을 안전하게 묶어야 함 | [아웃박스 패턴](./architecture/outbox.md) |
| 모놀리식에서 서비스 분리를 고민함 | [MSA](./architecture/msa.md) |
| 코드 배치와 계층 기준이 흔들림 | [아키텍처 패턴](./architecture/architecture.md) |
| 여러 서버에서 같은 자원을 동시에 처리함 | [동시성 제어](./operations/concurrency.md) |
| 장애를 빨리 감지하고 원인을 추적해야 함 | [모니터링](./operations/monitoring.md) |

## Spring과의 관계

Spring 문서에서는 **Spring이 제공하는 기능과 사용법**을 다룹니다.

- DI, Bean, MVC, Transaction, JPA, Security처럼 Spring 프레임워크에 직접 속한 내용은 [Spring](../spring/spring.md)에 둡니다.
- Redis, Kafka, MSA, Outbox처럼 Spring으로도 구현할 수 있지만 개념 자체가 더 넓은 주제는 Backend에 둡니다.

## 정리

| 항목 | 설명 |
|------|------|
| **Spring** | 프레임워크 기능과 사용법 |
| **Backend** | 인프라, 아키텍처, 운영 설계 |
| **목표** | 특정 기술 사용법보다 시스템을 안정적으로 설계하는 기준 정리 |

---

**관련 파일:**
- [Spring 학습 로드맵](../spring/spring.md)
- [DB 학습 노트](../db/db.md)
- [Java 학습 노트](../java/java.md)
