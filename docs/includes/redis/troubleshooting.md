<!-- Redis 장애 대응과 트러블슈팅 용어 -->

*[Connection Timeout]: Redis 연결 생성이나 연결 대기 시간이 제한을 넘는 상황.
*[Command Timeout]: 연결은 됐지만 Redis 명령 응답이 제한 시간 안에 오지 않는 상황.
*[timeout]: 작업이 정해진 제한 시간 안에 끝나지 않아 실패로 처리되는 상황.
*[Swapping]: 메모리 데이터가 OS swap으로 밀려 Redis 성능이 크게 느려지는 상황.
*[swap]: 메모리가 부족할 때 디스크를 메모리처럼 사용하는 영역. Redis에서는 큰 지연 원인이 된다.
*[Memory Fragmentation]: Redis가 실제 사용하는 메모리보다 OS가 잡은 RSS가 커지는 단편화 현상.
*[RSS]: Resident Set Size. OS가 프로세스에 실제로 할당한 물리 메모리 크기.
*[OOM]: Out Of Memory. 메모리가 부족해 쓰기 명령 실패나 장애가 발생하는 상태.
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
