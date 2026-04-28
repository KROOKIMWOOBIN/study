<!-- Redis 전용 용어집: Redis 문서에서만 include해서 툴팁으로 사용합니다. -->

<!-- ===== Redis 기본 ===== -->
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

<!-- ===== 캐시 ===== -->
*[Cache]: 반복 조회를 빠르게 처리하기 위한 임시 저장소. 원본 DB 부하를 줄이는 데 사용한다.
*[cache]: 반복 조회를 빠르게 처리하기 위한 임시 저장소. 원본 DB 부하를 줄이는 데 사용한다.
*[Cache Hit]: 캐시에 원하는 값이 있어 DB를 조회하지 않고 바로 응답하는 상황.
*[cache hit]: 캐시에 원하는 값이 있어 DB를 조회하지 않고 바로 응답하는 상황.
*[Cache Miss]: 캐시에 값이 없어 원본 DB까지 조회해야 하는 상황.
*[cache miss]: 캐시에 값이 없어 원본 DB까지 조회해야 하는 상황.
*[Cache Aside]: 애플리케이션이 캐시를 먼저 읽고, miss면 DB 조회 후 캐시에 저장하는 패턴. 실무 조회 캐시의 기본 선택지다.
*[cache aside]: 애플리케이션이 캐시를 먼저 읽고, miss면 DB 조회 후 캐시에 저장하는 패턴. 실무 조회 캐시의 기본 선택지다.
*[Read Through]: 애플리케이션은 캐시 계층에만 요청하고, 캐시 계층이 DB 조회와 적재를 맡는 패턴.
*[Write Through]: 쓰기 요청을 캐시와 DB에 동기적으로 함께 반영하는 패턴. 정합성은 좋아지지만 쓰기 지연이 늘 수 있다.
*[Write Behind]: 먼저 캐시에 쓰고 DB 반영은 나중에 비동기로 처리하는 패턴. 빠르지만 유실 위험을 설계해야 한다.
*[Cache Warming]: 서비스 오픈, 배포, 이벤트 전에 주요 데이터를 미리 캐시에 넣어 초기 miss 폭증을 줄이는 작업.
*[Cache Penetration]: 존재하지 않는 key 요청이 반복되어 매번 DB까지 도달하는 문제. null cache나 Bloom Filter로 완화한다.
*[Cache Stampede]: 인기 key가 만료된 순간 많은 요청이 동시에 DB로 몰리는 문제. TTL jitter, mutex lock, stale cache로 완화한다.
*[Cache Avalanche]: 많은 key가 비슷한 시점에 한꺼번에 만료되어 DB 부하가 급증하는 문제.
*[Cache Invalidation]: 원본 데이터가 바뀌었을 때 오래된 캐시를 삭제하거나 갱신하는 작업.
*[Cache Consistency]: DB와 캐시 값이 서로 얼마나 일치하는지에 대한 기준. 업무별로 허용 가능한 불일치 시간을 정해야 한다.
*[Stale Data]: 원본 DB보다 오래된 값이 캐시에 남아 있는 상태.
*[stale data]: 원본 DB보다 오래된 값이 캐시에 남아 있는 상태.
*[Stale Cache]: 만료됐거나 오래됐지만 잠시 응답에 사용하는 캐시. stampede 완화에 쓸 수 있다.
*[stale cache]: 만료됐거나 오래됐지만 잠시 응답에 사용하는 캐시. stampede 완화에 쓸 수 있다.
*[Stale-While-Revalidate]: 오래된 값을 잠시 응답하고 백그라운드에서 새 값으로 갱신하는 캐시 전략.
*[Null Cache]: DB에 없는 결과도 짧게 캐시해 반복 miss와 DB 조회를 줄이는 방법.
*[null cache]: DB에 없는 결과도 짧게 캐시해 반복 miss와 DB 조회를 줄이는 방법.
*[Bloom Filter]: "확실히 없음"을 빠르게 판단하는 확률적 자료구조. cache penetration 방지에 자주 사용한다.
*[Mutex Lock]: 여러 요청 중 하나만 DB 조회와 캐시 재생성을 하게 막는 짧은 락.
*[mutex lock]: 여러 요청 중 하나만 DB 조회와 캐시 재생성을 하게 막는 짧은 락.
*[Double Delete]: DB update 후 캐시를 삭제하고, 짧은 시간 뒤 한 번 더 삭제해 stale cache 가능성을 줄이는 완화책.
*[Hit Ratio]: 전체 캐시 조회 중 hit 비율. 캐시가 실제로 DB 부하를 줄이는지 보는 핵심 지표다.
*[hit ratio]: 전체 캐시 조회 중 hit 비율. 캐시가 실제로 DB 부하를 줄이는지 보는 핵심 지표다.
*[TTL]: Time To Live. key가 자동 삭제되기까지 남은 시간 또는 만료 시간 설정.
*[TTL jitter]: TTL에 작은 랜덤 값을 섞어 많은 key가 동시에 만료되지 않게 하는 방법.
*[TTL Randomization]: TTL에 랜덤 값을 섞어 stampede와 avalanche를 줄이는 방법. TTL jitter와 같은 의미로 많이 쓴다.
*[Expiration]: TTL이 지난 key가 만료되어 삭제 대상이 되는 과정.
*[Eviction]: Redis 메모리가 부족할 때 maxmemory-policy에 따라 key를 제거하는 동작.
*[eviction]: Redis 메모리가 부족할 때 maxmemory-policy에 따라 key를 제거하는 동작.

<!-- ===== Redis 자료구조 ===== -->
*[String]: Redis의 가장 기본 자료구조. 단일 값, JSON 문자열, 카운터, 토큰 저장에 많이 쓴다.
*[List]: 삽입 순서를 유지하는 목록 자료구조. 간단한 queue나 최근 N개 보관에 쓴다.
*[Set]: 중복 없는 집합 자료구조. 포함 여부, 태그, 좋아요 사용자 목록에 적합하다.
*[Sorted Set]: member마다 score를 붙여 정렬하는 집합. 랭킹, 우선순위, 시간 정렬에 자주 쓴다.
*[Hash]: key 하나 아래 여러 field-value를 저장하는 자료구조. 객체 일부 필드 갱신에 유용하다.
*[Bitmap]: bit 단위로 boolean 값을 저장하는 방식. 출석 체크처럼 대량 true/false 저장에 효율적이다.
*[HyperLogLog]: 고유 개수를 근사치로 추정하는 자료구조. 정확한 목록은 필요 없고 UV 같은 수치만 필요할 때 쓴다.
*[Stream]: Redis의 append-only 메시지 로그 자료구조. ID가 붙은 메시지를 저장하고 consumer group으로 나눠 읽을 수 있다.
*[Geospatial]: 위치 좌표를 저장하고 거리 기반 검색을 하는 Redis 기능.
*[Geospatial Index]: 좌표 기반 검색을 위한 Redis 기능. 주변 매장 찾기 같은 반경 검색에 쓴다.
*[Cardinality]: 한 key나 집합 안에 들어 있는 원소 수. 너무 커지면 big key 위험이 있다.
*[Time Complexity]: 데이터 크기가 늘 때 명령 실행 비용이 어떻게 증가하는지 나타내는 기준.
*[시간 복잡도]: 데이터 크기가 늘 때 명령 실행 비용이 어떻게 증가하는지 나타내는 기준.
*[O(1)]: 데이터 크기와 거의 무관하게 일정한 시간에 처리되는 비용.
*[O(log N)]: 데이터가 늘어도 비교적 완만하게 증가하는 로그 시간 비용.
*[O(N)]: 원소 수 N에 비례해 실행 시간이 증가하는 비용. 큰 key에서는 지연 원인이 된다.
*[O(S+N)]: 시작 위치까지 이동하는 비용 S와 읽는 원소 수 N이 함께 드는 비용.
*[O(log N + M)]: 탐색 비용 log N과 반환 원소 수 M이 함께 드는 비용.
*[Cursor Scan]: cursor를 이용해 데이터를 조금씩 나누어 탐색하는 방식. `KEYS` 같은 전체 blocking 명령의 대안이다.
*[cursor]: scan 계열 명령에서 다음 탐색 위치를 나타내는 값. 0이 나오면 한 바퀴 탐색이 끝난다.
*[field]: Redis Hash 안에서 하나의 속성을 가리키는 이름.
*[member]: Set, Sorted Set, Geospatial 안에 들어가는 개별 원소.
*[score]: Sorted Set에서 member를 정렬하는 숫자 값.
*[offset]: Bitmap에서는 특정 bit 위치, Kafka나 Stream에서는 메시지 위치를 가리키는 번호.
*[UV]: Unique Visitor. 중복을 제거한 방문자 수.

<!-- ===== 데이터 관리와 영속성 ===== -->
*[Key 설계]: key 이름, 식별자, TTL, 삭제 범위, cluster slot을 고려해 key 규칙을 정하는 작업.
*[key prefix]: key 앞부분에 도메인이나 용도를 붙인 문자열. 운영에서 검색, 삭제, 지표 분리에 유용하다.
*[prefix]: key 앞부분에 붙이는 도메인이나 용도 문자열. 운영에서 검색, 삭제, 지표 분리에 유용하다.
*[Maxmemory]: Redis가 사용할 수 있는 최대 메모리 설정.
*[maxmemory]: Redis가 사용할 수 있는 최대 메모리 설정.
*[maxmemory-policy]: 메모리가 maxmemory에 도달했을 때 어떤 key를 제거할지 정하는 정책.
*[noeviction]: maxmemory 초과 시 key를 제거하지 않고 쓰기 명령을 실패시키는 eviction 정책.
*[allkeys-lru]: 모든 key 중 오래 사용하지 않은 key를 우선 제거하는 eviction 정책.
*[allkeys-lfu]: 모든 key 중 사용 빈도가 낮은 key를 우선 제거하는 eviction 정책.
*[allkeys-random]: 모든 key 중 임의의 key를 제거하는 eviction 정책.
*[volatile-lru]: TTL이 있는 key 중 오래 사용하지 않은 key를 우선 제거하는 eviction 정책.
*[volatile-lfu]: TTL이 있는 key 중 사용 빈도가 낮은 key를 우선 제거하는 eviction 정책.
*[volatile-ttl]: TTL이 있는 key 중 만료가 가까운 key를 우선 제거하는 eviction 정책.
*[Persistence]: Redis 메모리 데이터를 디스크에 남겨 재시작이나 장애 후 복구에 사용하는 기능.
*[persistence]: Redis 메모리 데이터를 디스크에 남겨 재시작이나 장애 후 복구에 사용하는 기능.
*[RDB]: Redis Database snapshot. 특정 시점의 메모리 데이터를 파일로 저장하는 영속화 방식.
*[AOF]: Append Only File. 쓰기 명령 로그를 계속 기록해 재시작 시 재생하는 영속화 방식.
*[RDB snapshot]: 특정 시점의 Redis 데이터를 디스크 파일로 저장하는 작업.
*[AOF rewrite]: AOF 로그를 현재 데이터 상태 기준으로 압축해 다시 쓰는 작업. fork 비용과 지연을 고려해야 한다.
*[fsync]: 메모리 버퍼의 데이터를 디스크에 강제로 기록하는 동작. AOF 내구성과 성능의 trade-off와 관련된다.
*[Backup]: 장애나 실수에 대비해 데이터를 별도로 보관하는 작업.
*[Restore]: 백업 데이터를 이용해 Redis 상태를 복구하는 작업.
*[DR]: Disaster Recovery. 장애 후 서비스를 복구하기 위한 백업, 복원, 전환 전략.

<!-- ===== 운영 구조 ===== -->
*[Standalone]: Redis를 단일 인스턴스로 운영하는 구조. 단순하지만 고가용성은 약하다.
*[Replication]: master 데이터를 replica로 복제하는 구조. Redis 복제는 기본적으로 비동기다.
*[replication]: master 데이터를 replica로 복제하는 구조. Redis 복제는 기본적으로 비동기다.
*[Master-Replica]: 쓰기는 master가 받고 replica가 복제본을 유지하는 구조.
*[master]: 쓰기를 받는 주 Redis 노드.
*[replica]: master 데이터를 복제해 보관하는 Redis 노드. 장애 시 승격 대상이 될 수 있다.
*[Sentinel]: Redis master 장애를 감지하고 failover를 자동화하는 구성 요소.
*[Failover]: 장애 난 master 대신 replica를 새 master로 승격해 서비스를 이어가는 절차.
*[failover]: 장애 난 master 대신 replica를 새 master로 승격해 서비스를 이어가는 절차.
*[Cluster]: hash slot을 기준으로 데이터를 여러 master에 나누어 저장하는 Redis 확장 구조.
*[Redis Cluster]: hash slot을 기준으로 데이터를 여러 master에 나누어 저장하는 Redis 확장 구조.
*[Hash Slot]: Redis Cluster가 key를 배치하는 16384개의 논리 슬롯.
*[hash slot]: Redis Cluster가 key를 배치하는 16384개의 논리 슬롯.
*[Hash Tag]: `{}` 안 문자열만 hash해 여러 key를 같은 slot에 배치하는 방법.
*[hash tag]: `{}` 안 문자열만 hash해 여러 key를 같은 slot에 배치하는 방법.
*[Slot Migration]: Redis Cluster에서 slot을 다른 노드로 옮기는 작업.
*[Resharding]: Redis Cluster에서 slot 배치를 다시 조정해 데이터를 다른 노드로 옮기는 작업.
*[rebalancing]: 노드 간 부하나 slot 분포를 맞추기 위해 데이터를 재배치하는 작업.
*[sharding]: 데이터를 여러 노드나 key로 나누어 저장해 부하와 크기를 분산하는 방식.
*[full resync]: replica가 master의 전체 데이터를 다시 받아 복제를 재구성하는 과정.
*[Managed Redis]: 클라우드 제공자가 운영, 백업, 장애 조치 일부를 관리해주는 Redis 서비스.

<!-- ===== 성능과 장애 ===== -->
*[Big Key]: value 크기나 컬렉션 원소 수가 너무 큰 key. 조회, 삭제, 복제, failover 지연을 만든다.
*[big key]: value 크기나 컬렉션 원소 수가 너무 큰 key. 조회, 삭제, 복제, failover 지연을 만든다.
*[Hot Key]: 특정 key에 요청이 과도하게 몰려 노드 CPU나 네트워크가 집중되는 상태.
*[hot key]: 특정 key에 요청이 과도하게 몰려 노드 CPU나 네트워크가 집중되는 상태.
*[Pipelining]: 여러 Redis 명령을 한 번에 보내 네트워크 왕복 시간을 줄이는 방식.
*[Pipeline]: 여러 Redis 명령을 한 번에 보내 네트워크 왕복 시간을 줄이는 방식.
*[Batch]: 여러 작업을 묶어 한 번에 처리하는 방식. 너무 크게 묶으면 지연과 메모리 부담이 커질 수 있다.
*[RTT]: Round Trip Time. 요청을 보내고 응답을 받기까지 걸리는 네트워크 왕복 시간.
*[Client-side Caching]: Redis client 쪽에 데이터를 잠시 보관해 Redis 왕복을 줄이는 방식.
*[Slow Log]: Redis에서 느리게 실행된 명령을 기록하는 기능.
*[slowlog]: Redis에서 느리게 실행된 명령 기록. 어떤 명령이 병목인지 찾는 데 쓴다.
*[Slow Queries]: 느리게 실행되는 Redis 명령들. big key, O(N) 명령, Lua 지연이 원인이 될 수 있다.
*[Latency Monitor]: Redis 내부 지연 이벤트를 관찰하는 기능.
*[latency]: 요청 처리 지연 시간. Redis에서는 명령 실행, 네트워크, fork, swap 등이 영향을 준다.
*[p95]: 전체 요청 중 95%가 이 시간 이하로 끝났다는 지연 시간 지표.
*[p99]: 전체 요청 중 99%가 이 시간 이하로 끝났다는 지연 시간 지표. tail latency 확인에 중요하다.
*[Connection Pool]: Redis 연결을 미리 만들어 재사용하는 풀. 너무 작으면 대기, 너무 크면 Redis 연결 한계를 압박한다.
*[connection pool]: Redis 연결을 미리 만들어 재사용하는 풀. 너무 작으면 대기, 너무 크면 Redis 연결 한계를 압박한다.
*[Connection Timeout]: Redis 연결 생성이나 연결 대기 시간이 제한을 넘는 상황.
*[Command Timeout]: 연결은 됐지만 Redis 명령 응답이 제한 시간 안에 오지 않는 상황.
*[timeout]: 작업이 정해진 제한 시간 안에 끝나지 않아 실패로 처리되는 상황.
*[Swapping]: 메모리 데이터가 OS swap으로 밀려 Redis 성능이 크게 느려지는 상황.
*[swap]: 메모리가 부족할 때 디스크를 메모리처럼 사용하는 영역. Redis에서는 큰 지연 원인이 된다.
*[Memory Fragmentation]: Redis가 실제 사용하는 메모리보다 OS가 잡은 RSS가 커지는 단편화 현상.
*[RSS]: Resident Set Size. OS가 프로세스에 실제로 할당한 물리 메모리 크기.
*[OOM]: Out Of Memory. 메모리가 부족해 쓰기 명령 실패나 장애가 발생하는 상태.
*[fork]: 현재 프로세스를 복제해 자식 프로세스를 만드는 OS 동작. RDB 저장이나 AOF rewrite 때 비용이 생길 수 있다.
*[fork 비용]: fork 시 메모리와 OS 작업 때문에 Redis 지연이 생길 수 있는 비용.
*[Replication Lag]: replica가 master의 최신 쓰기를 따라가지 못하고 뒤처진 정도.
*[replication lag]: replica가 master의 최신 쓰기를 따라가지 못하고 뒤처진 정도.
*[Stale Read]: replica lag 등으로 최신 값이 아닌 오래된 값을 읽는 상황.
*[packet loss]: 네트워크 패킷이 중간에 유실되는 현상. timeout과 지연의 원인이 될 수 있다.
*[MOVED]: Redis Cluster에서 해당 key의 slot 담당 노드가 바뀌었음을 알리는 리다이렉트 응답.
*[ASK]: Redis Cluster slot 이동 중 임시로 다른 노드에 요청하라는 리다이렉트 응답.
*[CROSSSLOT]: 다중 key 명령의 key들이 서로 다른 hash slot에 있어 실행할 수 없다는 오류.
*[READONLY]: replica나 잘못된 노드에 쓰기를 시도했을 때 볼 수 있는 오류.
*[Runbook]: 장애 상황에서 확인 순서와 대응 방법을 적어둔 운영 절차 문서.
*[Fallback]: 주 경로가 실패했을 때 사용할 대체 처리. 캐시 장애 시 DB 직접 조회가 대표적이다.
*[fallback]: 주 경로가 실패했을 때 사용할 대체 처리. 캐시 장애 시 DB 직접 조회가 대표적이다.
*[Circuit Breaker]: 장애 중인 외부 호출을 잠시 차단해 장애 전파를 막는 패턴.
*[circuit breaker]: 장애 중인 외부 호출을 잠시 차단해 장애 전파를 막는 패턴.
*[Bulkhead]: 장애가 번지지 않도록 기능별 thread pool이나 리소스를 분리하는 패턴.
*[bulkhead]: 장애가 번지지 않도록 기능별 thread pool이나 리소스를 분리하는 패턴.
*[graceful degradation]: 일부 기능을 제한하거나 품질을 낮춰 전체 서비스 장애를 피하는 전략.

<!-- ===== 동시성과 락 ===== -->
*[Atomic Command]: 한 Redis 명령이 중간에 끼어들지 않고 실행되는 성질.
*[atomic]: 중간 상태가 보이지 않고 한 번에 실행된 것처럼 보장되는 성질.
*[MULTI / EXEC]: 여러 Redis 명령을 queue에 쌓은 뒤 순서대로 실행하는 Redis transaction 명령 조합.
*[MULTI/EXEC]: 여러 Redis 명령을 queue에 쌓은 뒤 순서대로 실행하는 Redis transaction 명령 조합.
*[DISCARD]: MULTI로 쌓은 Redis transaction 명령을 취소하는 명령.
*[WATCH]: key 변경을 감시하다가 변경되면 EXEC를 실패시켜 낙관적 락처럼 쓰는 명령.
*[Optimistic Locking]: 충돌이 드물다고 보고 먼저 진행한 뒤, 변경 여부를 확인해 충돌 시 재시도하는 락 방식.
*[Lua]: Redis 서버 안에서 짧은 원자 로직을 실행할 때 쓰는 스크립트 언어.
*[Lua Script]: 여러 Redis 명령을 서버에서 원자적으로 실행하는 스크립트.
*[Read-Modify-Write]: 값을 읽고 계산한 뒤 다시 쓰는 작업 흐름. 동시성 경쟁이 생기기 쉬워 Lua나 락이 필요할 수 있다.
*[Distributed Lock]: 여러 서버가 같은 자원을 동시에 처리하지 않도록 외부 저장소로 잡는 락.
*[분산 락]: 여러 서버가 같은 자원을 동시에 처리하지 않도록 외부 저장소로 잡는 락.
*[owner token]: 락을 잡은 요청을 식별하는 고유 값. unlock 시 다른 요청의 락을 지우지 않게 확인한다.
*[Fencing Token]: 락 획득 순서를 나타내는 단조 증가 토큰. 늦게 도착한 오래된 작업을 DB나 외부 시스템에서 거절할 때 쓴다.
*[Redlock]: 여러 독립 Redis 노드에서 과반수 락을 얻는 분산 락 알고리즘.
*[lock watchdog]: Redisson이 락을 잡은 스레드가 살아 있는 동안 TTL을 자동 연장해주는 기능.
*[Rate Limit]: 일정 시간 동안 허용할 요청 수를 제한하는 기능.
*[Rate Limiting]: 일정 시간 동안 허용할 요청 수를 제한하는 기법.
*[rate limit]: 일정 시간 동안 허용할 요청 수를 제한하는 기능.
*[Fixed Window]: 정해진 시간 창 단위로 요청 수를 세는 rate limiting 방식.
*[Sliding Window]: 최근 일정 시간 범위를 미끄러지듯 계산해 요청 수를 제한하는 방식.
*[Token Bucket]: 일정 속도로 토큰을 채우고 요청마다 토큰을 소비하는 rate limiting 방식.
*[NX]: Redis SET 옵션. key가 없을 때만 저장한다.
*[XX]: Redis SET 옵션. key가 이미 있을 때만 저장한다.
*[EX]: Redis SET 옵션. 만료 시간을 초 단위로 지정한다.
*[PX]: Redis SET 옵션. 만료 시간을 밀리초 단위로 지정한다.
*[Idempotency Key]: 같은 요청이 여러 번 와도 한 번만 처리되도록 요청을 식별하는 key.
*[idempotency key]: 같은 요청이 여러 번 와도 한 번만 처리되도록 요청을 식별하는 key.

<!-- ===== 메시징 ===== -->
*[Pub/Sub]: publisher가 channel에 메시지를 발행하면 구독 중인 subscriber가 즉시 받는 Redis 메시징 방식.
*[pub/sub]: publisher가 channel에 메시지를 발행하면 구독 중인 subscriber가 즉시 받는 Redis 메시징 방식.
*[publisher]: Pub/Sub에서 메시지를 발행하는 주체.
*[subscriber]: Pub/Sub에서 channel을 구독해 메시지를 받는 주체.
*[Channel]: Pub/Sub 메시지를 주고받는 이름.
*[channel]: Pub/Sub 메시지를 주고받는 이름.
*[Keyspace Notification]: key 만료, 삭제, 변경 같은 이벤트를 Pub/Sub으로 알려주는 Redis 기능.
*[Consumer Group]: Stream 메시지를 여러 consumer가 나누어 읽기 위한 그룹.
*[consumer group]: Stream 메시지를 여러 consumer가 나누어 읽기 위한 그룹.
*[consumer]: Stream이나 메시지 큐에서 메시지를 읽고 처리하는 주체.
*[ACK]: Acknowledgement. 메시지를 정상 처리했음을 알리는 확인 응답.
*[PEL]: Pending Entries List. Stream에서 consumer에게 전달됐지만 아직 ACK되지 않은 메시지 목록.
*[Pending Entries List]: Stream에서 consumer에게 전달됐지만 아직 ACK되지 않은 메시지 목록.
*[pending]: 처리 완료 확인이 아직 끝나지 않은 대기 상태.
*[claim]: 오래 pending 된 메시지를 다른 consumer가 가져와 다시 처리하는 동작.
*[DLQ]: Dead Letter Queue. 반복 실패한 메시지를 격리해 전체 처리를 막지 않게 하는 큐.
*[trim]: Stream이나 List 길이를 제한하기 위해 오래된 항목을 잘라내는 작업.
*[append-only log]: 새 항목을 뒤에 계속 추가하는 로그 구조. Redis Stream과 AOF 설명에 자주 등장한다.

<!-- ===== Spring Boot 연동 ===== -->
*[Spring Data Redis]: Spring에서 Redis 접근을 돕는 프로젝트. template, repository, cache 연동을 제공한다.
*[RedisTemplate]: Spring Data Redis에서 Redis 자료구조를 범용으로 다루는 template.
*[StringRedisTemplate]: key와 value를 문자열 중심으로 다루는 RedisTemplate 특화 버전.
*[ReactiveRedisTemplate]: reactive stack에서 non-blocking 방식으로 Redis를 다루는 template.
*[Spring Cache]: `@Cacheable`, `@CacheEvict` 같은 annotation 기반 캐시 추상화.
*[Spring Cache Abstraction]: 캐시 구현체와 비즈니스 코드를 분리하는 Spring의 캐시 추상화.
*[CacheManager]: Spring Cache에서 cache 생성과 설정을 관리하는 구성 요소.
*[RedisCacheManager]: Redis를 backend로 사용하는 Spring CacheManager 구현체.
*[Redis Repository]: Redis Hash 기반으로 객체 저장을 돕는 Spring Data 추상화.
*[Lettuce]: Netty 기반 Redis client. Spring Boot에서 기본 선택지로 많이 사용된다.
*[Jedis]: 전통적인 blocking 방식 Redis client.
*[Redisson]: Redis 기반 분산 락, map, queue 같은 고수준 API를 제공하는 client.
*[Netty]: Java 비동기 네트워크 프레임워크. Lettuce의 기반이다.
*[reactive stack]: blocking thread 점유를 줄이고 이벤트 기반으로 처리하는 애플리케이션 스택.
*[Serializer]: Java 객체와 Redis에 저장할 byte/string 데이터를 서로 변환하는 구성 요소.
*[serializer]: Java 객체와 Redis에 저장할 byte/string 데이터를 서로 변환하는 구성 요소.
*[StringRedisSerializer]: 문자열을 Redis key/value로 저장할 때 쓰는 Spring Redis serializer.
*[GenericJackson2JsonRedisSerializer]: 타입 정보를 포함해 객체를 JSON으로 저장하는 Spring Redis serializer.
*[Jackson2JsonRedisSerializer]: 지정한 타입 중심으로 객체를 JSON으로 저장하는 Spring Redis serializer.
*[JdkSerializationRedisSerializer]: Java 기본 직렬화를 사용하는 serializer. 호환성과 보안 주의가 필요하다.
*[Retry]: 실패한 Redis 호출을 다시 시도하는 전략. 무제한 재시도는 장애를 키울 수 있다.
*[fail-open]: 보안이나 제한 기능 실패 시 요청을 통과시키는 정책.
*[fail-closed]: 보안이나 제한 기능 실패 시 요청을 차단하는 정책.
*[Testcontainers]: 테스트 중 Docker container로 Redis 같은 외부 의존성을 띄우는 Java 테스트 라이브러리.

<!-- ===== 모니터링과 보안 ===== -->
*[Ops/sec]: 초당 처리 명령 수. Redis 처리량을 보는 지표다.
*[Connected Clients]: Redis에 연결된 client 수.
*[Blocked Clients]: blocking 명령 등으로 대기 중인 client 수.
*[Evicted Keys]: 메모리 부족으로 eviction된 key 수.
*[Expired Keys]: TTL 만료로 삭제된 key 수.
*[used_memory]: Redis가 데이터 저장에 사용하는 메모리 양.
*[used_memory_rss]: OS가 Redis 프로세스에 실제로 할당한 메모리 양.
*[mem_fragmentation_ratio]: RSS와 used_memory의 비율로 보는 메모리 단편화 지표.
*[connected_clients]: 현재 Redis에 연결된 client 수.
*[blocked_clients]: blocking 명령 등으로 대기 중인 client 수.
*[rejected_connections]: maxclients 초과 등으로 거부된 연결 수.
*[evicted_keys]: 메모리 부족으로 제거된 key 수.
*[expired_keys]: TTL 만료로 제거된 key 수.
*[instantaneous_ops_per_sec]: Redis가 현재 처리 중인 초당 명령 수.
*[ACL]: Access Control List. 사용자별 명령, key 접근 범위를 제한하는 Redis 보안 기능.
*[AUTH]: Redis 접속 시 비밀번호나 사용자 인증을 수행하는 명령/기능.
*[Protected Mode]: 외부에서 무심코 Redis에 접속하지 못하게 막는 보호 모드.
*[TLS]: 네트워크 통신을 암호화하는 프로토콜. Redis 외부 통신 보호에 사용한다.
*[Command Rename]: 위험 명령 이름을 바꾸거나 비활성화해 실수와 공격을 줄이는 설정.

<!-- ===== Redis Stack ===== -->
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

<!-- ===== 비교와 선택 ===== -->
*[Local Cache]: 애플리케이션 프로세스 내부 캐시. 가장 빠르지만 서버 간 정합성 문제가 생길 수 있다.
*[local cache]: 애플리케이션 프로세스 내부 캐시. 가장 빠르지만 서버 간 정합성 문제가 생길 수 있다.
*[Redis Cache]: 여러 서버가 공유하는 외부 Redis 기반 캐시.
*[Distributed Cache]: 여러 애플리케이션 서버가 함께 사용하는 외부 캐시.
*[Message Broker]: 메시지를 저장하거나 전달해 producer와 consumer를 분리하는 시스템.
*[Message Queue]: 메시지를 큐에 보관했다가 consumer가 가져가 처리하는 메시징 구조.
*[RDBMS]: 관계형 데이터베이스 관리 시스템. 트랜잭션, 조인, 정합성이 강점이다.
*[Document DB]: JSON 문서 중심으로 데이터를 저장하는 NoSQL 데이터베이스.
*[NoSQL]: 관계형 테이블 모델 밖의 다양한 데이터 저장소를 통칭하는 말.
*[Memcached]: 단순 key-value 캐시에 집중한 메모리 캐시 시스템.
*[Caffeine]: Java 애플리케이션 내부에서 많이 쓰는 고성능 local cache 라이브러리.
*[Kafka]: partitioned commit log 기반 메시지 플랫폼. 긴 보관, 재처리, 대규모 이벤트 스트림에 강하다.
*[topic]: Kafka에서 메시지를 분류하는 논리 이름.
*[partition]: Kafka topic을 나눠 저장하고 병렬 처리하는 단위.
*[offset commit]: consumer가 어디까지 처리했는지 저장하는 Kafka 처리 위치 기록.

<!-- ===== 실무 유즈케이스 ===== -->
*[조회 캐시]: DB 조회 결과를 Redis에 저장해 반복 조회를 빠르게 하는 캐시.
*[세션]: 로그인 상태나 사용자 상태를 서버 외부 저장소에 보관한 데이터.
*[Refresh Token]: access token을 재발급받기 위한 토큰. 폐기와 만료 관리를 위해 Redis에 저장하기도 한다.
*[인증번호]: 이메일/SMS 인증 코드처럼 짧은 TTL로 저장하는 임시 값.
*[Presence]: 사용자의 접속 중, 자리 비움 같은 현재 상태 정보.
*[랭킹 보드]: 점수 기준으로 사용자를 정렬해 보여주는 목록. Redis Sorted Set이 자주 쓰인다.
*[대기열]: 요청을 순서대로 처리하기 위해 잠시 쌓아두는 구조.
*[원장]: 돈, 재고, 포인트처럼 감사와 정합성이 중요한 기준 기록. 보통 Redis 단독 저장을 피한다.
*[무효화]: 원본 데이터 변경 후 오래된 캐시를 삭제하거나 더 이상 쓰지 않게 만드는 작업.
*[멱등성]: 같은 요청이 여러 번 실행되어도 결과가 한 번 실행한 것과 같게 유지되는 성질.
