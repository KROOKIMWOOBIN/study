<!-- Redis 캐시 용어 -->

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
*[TTL jitter]: TTL에 작은 랜덤 값을 섞어 많은 key가 동시에 만료되지 않게 하는 방법.
*[TTL Randomization]: TTL에 랜덤 값을 섞어 stampede와 avalanche를 줄이는 방법. TTL jitter와 같은 의미로 많이 쓴다.
