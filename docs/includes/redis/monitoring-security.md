<!-- Redis 모니터링과 보안 용어 -->

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
