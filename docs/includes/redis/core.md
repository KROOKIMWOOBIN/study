<!-- Redis 공통 용어 -->

*[Redis]: Remote Dictionary Server. 메모리 기반 key-value 저장소. 캐시, 세션, 카운터, 랭킹, 락, 메시징에 자주 사용한다.
*[Key]: Redis에서 값을 찾는 이름. 보통 `domain:id:purpose`처럼 의미가 드러나게 만든다.
*[key]: Redis에서 값을 찾는 이름. 보통 `domain:id:purpose`처럼 의미가 드러나게 만든다.
*[Value]: key에 저장된 실제 데이터. 문자열, 해시, 집합, 스트림 같은 Redis 자료구조가 될 수 있다.
*[value]: key에 저장된 실제 데이터. 문자열, 해시, 집합, 스트림 같은 Redis 자료구조가 될 수 있다.
*[Key-Value]: key로 value를 바로 찾아가는 저장 방식. Redis의 기본 데이터 모델이다.
*[key-value]: key로 value를 바로 찾아가는 저장 방식. Redis의 기본 데이터 모델이다.
*[In-Memory]: 데이터를 주로 메모리에서 처리하는 방식. 빠르지만 메모리 비용과 용량 한계를 고려해야 한다.
*[in-memory]: 데이터를 주로 메모리에서 처리하는 방식. 빠르지만 메모리 비용과 용량 한계를 고려해야 한다.
*[Data Type]: Redis가 key 하나에 저장할 수 있는 자료구조 종류. String, Hash, Set, Sorted Set, Stream 등이 있다.
*[Command]: Redis에 보내는 명령. `GET`, `SET`, `HGET`, `ZADD`처럼 자료구조별 명령이 있다.
*[TTL]: Time To Live. key가 자동 삭제되기까지 남은 시간 또는 만료 시간 설정.
