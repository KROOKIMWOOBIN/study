# Spring Boot Redis 연동

Spring Boot에서 Redis를 사용할 때는 **어떤 추상화로 접근할지, 어떤 serializer를 쓸지, 장애 시 어떻게 fallback할지**를 먼저 정해야 합니다.

## 용어

| 용어 | 의미 |
|------|------|
| Spring Data Redis | Spring에서 Redis 접근을 돕는 프로젝트 |
| RedisTemplate | Redis 자료구조를 범용으로 다루는 template |
| StringRedisTemplate | key/value를 문자열 중심으로 다루는 template |
| ReactiveRedisTemplate | reactive stack에서 사용하는 Redis template |
| Spring Cache | `@Cacheable`, `@CacheEvict` 기반 캐시 추상화 |
| Redis Repository | Redis Hash 기반 repository 추상화 |
| Lettuce | 기본적으로 많이 쓰이는 Netty 기반 Redis client |
| Jedis | 전통적인 Redis client |
| Redisson | 분산 락, 자료구조 API를 제공하는 client |

## 질문

### RedisTemplate과 Spring Cache는 언제 다르게 쓰나?

| 구분 | RedisTemplate | Spring Cache |
|------|---------------|--------------|
| 제어 | 명령과 자료구조를 직접 제어 | annotation 기반 캐시 |
| 사용처 | 카운터, 락, 랭킹, custom key | 조회 결과 캐시 |
| 장점 | 세밀한 제어 | 코드가 간결 |
| 단점 | 구현 코드 증가 | 복잡한 캐시 정책은 숨겨져 보일 수 있음 |

## 의존성 설정

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-cache'
```

## 연결 설정

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2s
      lettuce:
        pool:
          max-active: 16
          max-idle: 16
          min-idle: 2
          max-wait: 500ms
```

운영에서는 host, port뿐 아니라 command timeout, connect timeout, pool, retry, circuit breaker를 함께 봅니다.

| 설정 | 의미 | 기준 |
|------|------|------|
| `timeout` | Redis command timeout | API timeout보다 짧게 |
| `max-active` | pool에서 사용할 최대 연결 수 | 인스턴스 수와 Redis `maxclients` 기준 |
| `max-wait` | pool 대기 시간 | 길면 장애 전파, 짧으면 빠른 실패 |
| `min-idle` | 유지할 idle 연결 수 | 급격한 트래픽 대비 |

## RedisTemplate

```java
@Bean
RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
}
```

RedisTemplate은 다양한 자료구조를 세밀하게 다룰 때 좋습니다.

## StringRedisTemplate

```java
stringRedisTemplate.opsForValue().set("auth:code:user-1", "123456", Duration.ofMinutes(3));
String code = stringRedisTemplate.opsForValue().get("auth:code:user-1");
```

문자열 key/value만 다룬다면 StringRedisTemplate이 단순합니다.

## Spring Cache Abstraction

```java
@Cacheable(cacheNames = "product", key = "#productId")
public ProductResponse getProduct(Long productId) {
    return productRepository.findById(productId)
        .map(ProductResponse::from)
        .orElseThrow();
}
```

```java
@CacheEvict(cacheNames = "product", key = "#productId")
public void updateProduct(Long productId, ProductUpdateRequest request) {
    // DB update
}
```

조회 캐시는 Spring Cache로 시작하기 좋지만, TTL, key prefix, null cache, lock 기반 stampede 방지까지 필요하면 설정을 명확히 봐야 합니다.

### CacheManager TTL 설정

cache 이름별로 TTL을 다르게 두면 운영 기준이 명확해집니다.

```java
@Bean
RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
        .disableCachingNullValues()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
        )
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
        );

    Map<String, RedisCacheConfiguration> configs = Map.of(
        "product", defaultConfig.entryTtl(Duration.ofMinutes(5)),
        "category", defaultConfig.entryTtl(Duration.ofHours(1)),
        "notice", defaultConfig.entryTtl(Duration.ofMinutes(30))
    );

    return RedisCacheManager.builder(factory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(configs)
        .build();
}
```

| cache | TTL 기준 |
|-------|----------|
| `product` | 가격·상태 변경 가능성이 있어 짧게 |
| `category` | 변경이 적어 길게 |
| `notice` | 운영자가 수정할 수 있어 중간 정도 |

### Null Cache 주의

Spring Cache에서 null caching을 켜면 cache penetration을 줄일 수 있지만, 존재하지 않는 값도 Redis에 저장됩니다. null cache가 필요하면 짧은 TTL을 따로 두거나, 서비스 코드에서 명시적인 `EMPTY` value를 저장하는 방식을 검토합니다.

## Redis Repository

Redis Repository는 객체를 Redis Hash로 저장하는 추상화입니다. 세션성 객체나 짧게 살아도 되는 상태에는 쓸 수 있지만, RDB repository처럼 원장 데이터를 맡기면 위험합니다.

## Serializer 전략

| Serializer | 특징 | 주의 |
|------------|------|------|
| `StringRedisSerializer` | 문자열 key/value에 적합 | 객체 저장에는 직접 변환 필요 |
| `GenericJackson2JsonRedisSerializer` | 타입 정보 포함 JSON | payload가 커질 수 있음 |
| `Jackson2JsonRedisSerializer` | 특정 타입 JSON | 타입별 설정 필요 |
| `JdkSerializationRedisSerializer` | Java 직렬화 | 사람이 읽기 어렵고 호환성·보안 주의 |

<div class="warning-box" markdown="1">

**주의**: 운영 중 serializer를 바꾸면 기존 Redis 값과 호환되지 않을 수 있다. key version을 나누거나 마이그레이션 계획을 둔다.

</div>

## Lettuce vs Jedis vs Redisson

| Client | 특징 | 언제 |
|--------|------|------|
| Lettuce | 비동기·Netty 기반, Spring Boot 기본 선택지로 자주 사용 | 일반 Redis 연동 |
| Jedis | 단순한 동기 client | 단순 환경, 기존 코드 |
| Redisson | 분산 락, map, queue 같은 고수준 API | 락과 고급 구조가 필요할 때 |

## Connection Pool과 Timeout

| 설정 | 기준 |
|------|------|
| connection pool | 인스턴스 수와 동시 요청량 기준 |
| command timeout | Redis 지연 시 애플리케이션 thread가 오래 묶이지 않게 |
| retry | 짧고 제한적으로, 폭주 방지 |
| circuit breaker | Redis 장애가 전체 서비스 장애로 번지지 않게 |

### Lettuce Client 설정 예시

```java
@Bean
LettuceClientConfigurationBuilderCustomizer lettuceCustomizer() {
    return builder -> builder
        .commandTimeout(Duration.ofSeconds(2))
        .shutdownTimeout(Duration.ofMillis(100));
}
```

Redis timeout은 DB timeout, API timeout과 함께 맞춰야 합니다. Redis가 캐시라면 Redis timeout이 길어서 전체 API가 느려지는 상황을 피하는 것이 보통입니다.

### Retry와 Circuit Breaker

Redis 장애에서 무제한 retry는 장애를 키웁니다.

| 상황 | 권장 |
|------|------|
| 조회 캐시 실패 | 짧게 실패 후 DB fallback |
| 락 획득 실패 | 제한 횟수 retry 후 실패 응답 |
| rate limit Redis 실패 | 보안 요구에 따라 fail-open/fail-closed 결정 |
| Redis 전체 장애 | circuit breaker로 일정 시간 호출 차단 |

## Redis 장애 시 Fallback

| 상황 | 대응 |
|------|------|
| 조회 캐시 Redis 장애 | DB 직접 조회, rate limit 필요 |
| 세션 Redis 장애 | 재로그인 허용 여부 결정 |
| 락 Redis 장애 | 작업 중단 또는 DB unique로 보호 |
| rate limit Redis 장애 | fail-open/fail-closed 정책 결정 |

```java
public ProductResponse getProduct(Long productId) {
    try {
        ProductResponse cached = cacheReader.get(productId);
        if (cached != null) {
            return cached;
        }
    } catch (RedisConnectionFailureException ex) {
        // Redis 장애 시 DB fallback
    }

    ProductResponse response = productRepository.findResponse(productId);
    cacheWriter.put(productId, response);
    return response;
}
```

fallback을 넣을 때는 DB가 갑자기 모든 트래픽을 받게 될 수 있습니다. Redis 장애 시 DB 보호를 위해 rate limit, degraded response, circuit breaker를 함께 고려합니다.

## 테스트 전략

| 방식 | 특징 |
|------|------|
| Local Redis | 빠르고 단순 |
| Docker Redis | 개발 환경 일관성 |
| Testcontainers | 통합 테스트 재현성 |
| Embedded Redis | 환경에 따라 유지보수 이슈 가능 |

### Testcontainers 예시

```java
@Testcontainers
@SpringBootTest
class RedisCacheTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void saveWithTtl() {
        redisTemplate.opsForValue().set("auth:code:user-1", "123456", Duration.ofMinutes(3));

        Long ttl = redisTemplate.getExpire("auth:code:user-1");

        assertThat(ttl).isPositive();
    }
}
```

테스트에서는 단순 저장 성공뿐 아니라 TTL, serializer, key prefix, cache eviction까지 확인하는 것이 좋습니다.

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| key와 TTL 정책을 코드 밖 설정으로 관리 | 운영 변경이 쉬움 |
| serializer를 명시 | 기본 직렬화 의존 방지 |
| timeout을 짧고 명확히 설정 | Redis 장애 전파 방지 |
| fallback 정책 문서화 | 장애 시 동작 예측 |
| Testcontainers로 통합 테스트 | 실제 Redis 명령 검증 |

## 실무에서는?

| 사용처 | 추천 접근 |
|--------|-----------|
| 단순 조회 캐시 | Spring Cache |
| 인증번호·토큰 | StringRedisTemplate |
| 랭킹·카운터 | RedisTemplate |
| 분산 락 | Redisson 또는 `SET NX PX` 직접 구현 |
| 장애 대응 | timeout + fallback + metric |

---

**관련 파일:**
- [캐시 전략과 정합성](./캐시패턴.md)
- [트랜잭션과 동시성](./동시성락.md)
- [실무 유즈케이스](./실무유즈케이스.md)

--8<-- "includes/redis/core.md"
--8<-- "includes/redis/springboot.md"
--8<-- "includes/redis/cache.md"
--8<-- "includes/redis/data-structures.md"
--8<-- "includes/redis/data-management.md"
--8<-- "includes/redis/concurrency-lock.md"
--8<-- "includes/redis/performance.md"
--8<-- "includes/redis/troubleshooting.md"
--8<-- "includes/redis/use-cases.md"
