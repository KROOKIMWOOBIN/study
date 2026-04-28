# Redis Stack과 확장 기능

Redis Stack은 Redis에 JSON, 검색, Bloom Filter, TimeSeries, Vector Search 같은 기능을 더한 확장 패키지입니다. 기본 Redis로 충분한지, 전용 저장소가 더 맞는지 비교하고 사용해야 합니다.

## 용어

| 용어 | 의미 |
|------|------|
| Redis Stack | Redis에 여러 모듈을 묶어 제공하는 확장 패키지 |
| RedisJSON | JSON 문서를 Redis에 저장·수정하는 기능 |
| RediSearch | secondary index, full-text search, vector search 기능 |
| RedisBloom | Bloom Filter, Cuckoo Filter 등 확률적 자료구조 |
| RedisTimeSeries | 시계열 데이터 저장과 집계 |
| Vector Search | embedding vector 유사도 검색 |

## 질문

### Redis Stack은 언제 쓰나?

기본 Redis 자료구조만으로 모델링이 너무 복잡해지고, 그래도 Redis의 빠른 응답과 메모리 기반 처리가 필요한 경우 검토합니다.

| 상황 | 검토 기능 |
|------|-----------|
| JSON 필드 일부를 자주 갱신 | RedisJSON |
| Redis 안에서 조건 검색이 필요 | RediSearch |
| 존재 여부를 빠르게 필터링 | RedisBloom |
| 지표성 시계열 저장 | RedisTimeSeries |
| embedding 유사도 검색 | Vector Search |

## RedisJSON

JSON 문서를 통째 문자열로 저장하면 필드 일부만 바꾸기 어렵습니다. RedisJSON은 JSON path 기반으로 일부 필드를 읽고 쓸 수 있습니다.

| 장점 | 주의 |
|------|------|
| JSON 필드 단위 조작 | 메모리 비용 증가 가능 |
| RediSearch와 연계 가능 | RDBMS 트랜잭션 대체 아님 |

## RediSearch

Redis 안에서 index를 만들고 검색할 수 있습니다.

| 사용처 | 주의 |
|--------|------|
| 작은 검색 기능 | 대용량 전문 검색은 검색 엔진과 비교 |
| tag, numeric, text index | index 메모리 비용 |
| autocomplete | 데이터 크기 제한 확인 |

## RedisBloom

Bloom Filter는 "없음"을 빠르게 판단하는 확률적 자료구조입니다.

| 특징 | 설명 |
|------|------|
| false positive | 실제로 없는데 있을 수 있다고 판단할 수 있음 |
| false negative | 일반적으로 없음 |
| 사용처 | cache penetration 방지, 중복 대략 판정 |

Cache Penetration에서 존재 가능성이 없는 ID를 DB까지 보내지 않는 용도로 쓸 수 있습니다.

## RedisTimeSeries

시간 순서의 지표 데이터를 저장하고 downsampling, aggregation을 할 수 있습니다.

| 사용처 | 주의 |
|--------|------|
| 간단한 metric 저장 | 전문 observability stack 대체 여부 검토 |
| 센서 데이터 | retention과 downsampling 설계 |

## Vector Search

embedding vector를 저장하고 유사도 검색을 수행합니다.

| 사용처 | 주의 |
|--------|------|
| 추천, semantic search | 데이터 크기와 index 메모리 |
| RAG 보조 검색 | 전용 vector DB와 비교 |

## Module 사용 시 주의사항

| 주의 | 설명 |
|------|------|
| 운영 복잡도 | 모듈별 설정, 백업, 모니터링이 추가됨 |
| 호환성 | managed Redis에서 지원 여부 확인 |
| 메모리 비용 | index와 metadata가 추가됨 |
| 대체재 비교 | 검색 엔진, vector DB, time-series DB와 비교 |
| 장애 영향 | Redis 핵심 캐시와 같은 cluster에 둘지 분리할지 결정 |

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| 기본 Redis로 충분한지 먼저 검토 | 불필요한 복잡도 방지 |
| 작은 범위에서 POC | 메모리와 latency 확인 |
| managed 서비스 지원 여부 확인 | 운영 가능성 확인 |
| 핵심 캐시와 리소스 분리 검토 | 검색 부하가 캐시에 영향 주는 것 방지 |

## 실무에서는?

| 요구 | 선택 |
|------|------|
| 캐시 key-value | 기본 Redis |
| JSON 필드 수정 | RedisJSON 검토 |
| 존재하지 않는 ID 차단 | RedisBloom 검토 |
| 간단한 검색 | RediSearch 검토 |
| 대규모 검색 | 검색 엔진 검토 |
| vector 검색 | Redis Stack 또는 전용 vector DB 비교 |

---

**관련 파일:**
- [캐시 전략과 정합성](./캐시패턴.md)
- [비교와 선택 기준](./비교선택.md)
- [모니터링과 보안](./모니터링보안.md)

--8<-- "includes/redis/core.md"
--8<-- "includes/redis/stack.md"
--8<-- "includes/redis/cache.md"
--8<-- "includes/redis/data-structures.md"
--8<-- "includes/redis/comparison.md"
