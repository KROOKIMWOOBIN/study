<!-- Redis 데이터 관리와 영속성 용어 -->

*[Key 설계]: key 이름, 식별자, TTL, 삭제 범위, cluster slot을 고려해 key 규칙을 정하는 작업.
*[key prefix]: key 앞부분에 도메인이나 용도를 붙인 문자열. 운영에서 검색, 삭제, 지표 분리에 유용하다.
*[prefix]: key 앞부분에 붙이는 도메인이나 용도 문자열. 운영에서 검색, 삭제, 지표 분리에 유용하다.
*[Expiration]: TTL이 지난 key가 만료되어 삭제 대상이 되는 과정.
*[Eviction]: Redis 메모리가 부족할 때 maxmemory-policy에 따라 key를 제거하는 동작.
*[eviction]: Redis 메모리가 부족할 때 maxmemory-policy에 따라 key를 제거하는 동작.
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
*[Cursor Scan]: cursor를 이용해 데이터를 조금씩 나누어 탐색하는 방식. `KEYS` 같은 전체 blocking 명령의 대안이다.
*[cursor]: scan 계열 명령에서 다음 탐색 위치를 나타내는 값. 0이 나오면 한 바퀴 탐색이 끝난다.
