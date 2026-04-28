<!-- Spring Boot Redis 연동 용어 -->

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
