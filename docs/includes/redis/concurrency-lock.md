<!-- Redis 동시성과 락 용어 -->

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
