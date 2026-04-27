# Redis

<div class="concept-box" markdown="1">

**Redis**: 메모리 기반 Key-Value 저장소이며, 캐시·세션·분산 락·카운터·랭킹·메시징 같은 빠른 상태 저장에 사용한다.

</div>

DB는 디스크, 트랜잭션, 인덱스, 락, 영속성을 책임지기 때문에 모든 요청을 DB로만 처리하면 응답 속도와 처리량에 한계가 옵니다. Redis는 자주 읽는 데이터, 짧게 살아도 되는 상태, 빠른 원자 연산을 메모리에서 처리해 DB와 애플리케이션의 부담을 줄입니다.

Redis는 "빠른 DB"라기보다 **보조 저장소**로 보는 것이 안전합니다. 장애가 나도 복구할 기준 데이터는 DB, 이벤트 로그, 외부 원장처럼 더 강한 저장소에 있어야 합니다.

---

## 먼저 보는 큰 그림

```text
Redis에 있으면 빠르게 응답한다.
Redis에 없으면 DB에서 가져와 다시 채운다.
Redis가 비어도 DB 기준으로 복구할 수 있어야 한다.
```

## 학습 로드맵

| 순서 | 카테고리 | 바로가기 | 핵심 질문 |
|------|----------|----------|-----------|
| 1 | 기초 및 데이터 구조 | [기본 사용과 자료구조](./redis/기본사용.md) | Redis는 어떤 자료구조를 언제 쓰는가? |
| 2 | 설계와 데이터 관리 | [Key 설계와 데이터 관리](./redis/데이터관리.md) | key, TTL, 메모리, persistence를 어떻게 설계하는가? |
| 3 | 캐시 전략과 정합성 | [캐시 전략과 정합성](./redis/캐시패턴.md) | DB 부하를 줄이면서 stale data를 어떻게 다루는가? |
| 4 | 트랜잭션과 동시성 | [트랜잭션과 동시성](./redis/동시성락.md) | 원자 처리, Lua, 분산 락, rate limit을 어떻게 구현하는가? |
| 5 | 메시징 고급 기능 | [Pub/Sub과 Stream](./redis/메시징.md) | Redis 메시징은 Kafka와 어떻게 다른가? |
| 6 | 운영 구조와 고가용성 | [운영 구조와 고가용성](./redis/구조와운영.md) | replication, Sentinel, Cluster는 어떤 문제를 푸는가? |
| 7 | Spring Boot 연동 | [Spring Boot Redis 연동](./redis/springboot.md) | RedisTemplate, serializer, client를 어떻게 고르는가? |
| 8 | 실무 유즈케이스 | [실무 유즈케이스](./redis/실무유즈케이스.md) | 세션, 토큰, 랭킹, 대기열에 어떻게 적용하는가? |
| 9 | 성능 최적화 | [성능 최적화](./redis/성능최적화.md) | 느린 명령, big key, hot key를 어떻게 피하는가? |
| 10 | 장애 대응 | [장애 대응과 트러블슈팅](./redis/장애대응.md) | timeout, OOM, failover, slot 문제를 어떤 순서로 보는가? |
| 11 | 모니터링과 보안 | [모니터링과 보안](./redis/모니터링보안.md) | 어떤 지표와 보안 설정을 운영 기준으로 둘 것인가? |
| 12 | 비교와 선택 기준 | [비교와 선택 기준](./redis/비교선택.md) | Redis를 언제 쓰고 언제 피해야 하는가? |
| 13 | Redis Stack | [Redis Stack과 확장 기능](./redis/redis-stack.md) | RedisJSON, Search, Bloom 같은 모듈은 언제 고려하는가? |
| 14 | 베스트 프랙티스 | [베스트 프랙티스](./redis/베스트프랙티스.md) | 실무 체크리스트와 안티패턴은 무엇인가? |

## 언제 쓰는지

| 상황 | 적합도 | 이유 |
|------|--------|------|
| 자주 읽지만 자주 바뀌지 않는 데이터 | 높음 | DB 조회를 줄이고 응답 속도 개선 |
| 세션, 인증 토큰 같은 짧은 상태 | 높음 | TTL로 자동 만료 가능 |
| 카운터, 조회수, Rate Limit | 높음 | 원자 증가 연산이 빠름 |
| 랭킹, 점수 정렬 | 높음 | Sorted Set이 적합 |
| 짧은 분산 락 | 조건부 | 단기 중복 실행 방지에 사용하되 멱등성 필요 |
| 반드시 유실되면 안 되는 원장 데이터 | 낮음 | Redis 장애·failover·설정 오류에 취약 |
| 복잡한 조인과 검색 | 낮음 | RDBMS나 검색 엔진 역할이 아님 |

## 실무 핵심

| 주제 | 기준 |
|------|------|
| Cache | Cache Aside, 짧은 TTL, DB 변경 후 캐시 삭제를 기본으로 검토 |
| Warming | 트래픽이 예측되는 핵심 데이터는 미리 적재하되 TTL jitter를 함께 고려 |
| Penetration | 존재하지 않는 값 반복 조회는 null cache, 입력 검증, Bloom Filter로 완화 |
| Key | 도메인과 용도가 드러나게 `:`로 계층화하고, cluster 다중 키는 hash tag 고려 |
| Memory | `maxmemory`, eviction 정책, big key 제한을 명확히 둠 |
| Lock | `SET NX PX`, 소유자 확인 삭제, DB unique나 멱등성과 함께 사용 |
| 장애 | Redis가 비어도 DB 기준으로 복구 가능한지 먼저 확인 |
| 관찰 | Hit Ratio, memory, latency, evictions, replication lag, connection을 알림으로 관리 |

## 정리

| 항목 | 설명 |
|------|------|
| 핵심 용도 | 캐시, TTL 상태, 카운터, 랭킹, 분산 락, 메시징 |
| 가장 큰 강점 | 메모리 기반 빠른 응답과 다양한 자료구조 |
| 가장 큰 주의점 | 메모리 한계, big key, hot key, 캐시 정합성, 비동기 복제 |
| 운영 핵심 | TTL, eviction, persistence, replication, slowlog, failover |

---

**관련 파일:**
- [동시성 제어](../operations/concurrency.md) — 분산 락과 중복 실행 방지
- [모니터링](../operations/monitoring.md) — 지표와 알림 설계
- [Kafka](./kafka.md) — 메시지 기반 비동기 처리
