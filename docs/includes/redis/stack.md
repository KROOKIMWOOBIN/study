<!-- Redis Stack과 확장 기능 용어 -->

*[Redis Stack]: Redis에 JSON, 검색, Bloom Filter, TimeSeries, Vector Search 등을 묶어 제공하는 확장 패키지.
*[RedisJSON]: JSON 문서를 Redis에 저장하고 JSON path로 일부 필드를 읽고 수정하는 기능.
*[RediSearch]: Redis 안에서 secondary index, full-text search, vector search를 제공하는 검색 모듈.
*[RedisBloom]: Bloom Filter, Cuckoo Filter 같은 확률적 자료구조를 제공하는 Redis 모듈.
*[RedisTimeSeries]: 시계열 데이터를 저장하고 집계하는 Redis 모듈.
*[Vector Search]: embedding vector 사이의 유사도를 기준으로 검색하는 기능.
*[vector search]: embedding vector 사이의 유사도를 기준으로 검색하는 기능.
*[embedding]: 텍스트, 이미지 같은 데이터를 의미가 반영된 숫자 벡터로 표현한 값.
*[vector]: 여러 숫자로 이루어진 배열. 유사도 검색에서 의미 표현으로 사용한다.
*[secondary index]: 기본 key 외에 다른 필드 조건으로 검색하기 위해 만드는 보조 인덱스.
*[full-text search]: 문서의 단어를 분석해 텍스트 검색을 수행하는 기능.
*[Cuckoo Filter]: Bloom Filter처럼 존재 여부를 확률적으로 판단하지만 삭제도 지원하는 자료구조.
*[false positive]: 실제로는 없는데 있을 수 있다고 판단하는 오류.
*[false negative]: 실제로는 있는데 없다고 판단하는 오류. 일반적인 Bloom Filter에서는 발생하지 않게 설계한다.
*[TimeSeries]: 시간 순서로 쌓이는 지표성 데이터.
*[retention]: 데이터를 얼마나 오래 보관할지 정한 기간.
*[downsampling]: 고해상도 시계열 데이터를 더 큰 시간 단위로 요약해 저장하는 작업.
*[module]: Redis에 추가 기능을 붙이는 확장 단위.
*[POC]: Proof of Concept. 도입 전에 작은 범위로 가능성과 비용을 검증하는 작업.
*[RAG]: Retrieval-Augmented Generation. 검색으로 찾은 외부 지식을 생성 모델 답변에 보강하는 방식.
