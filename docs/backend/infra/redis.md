## Redis

### 왜 쓰는가?

<div class="concept-box" markdown="1">

DB는 디스크 기반이라 반복 조회 시 느리다. ==Redis==는 **인메모리 저장소**로 DB보다 10~100배 빠른 응답을 제공한다. 캐시, 세션, 분산 락 등 다양한 용도로 활용한다.

</div>

### 의존성

```markdown
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

```markdown
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Spring Cache — @Cacheable

가장 간단한 캐시 적용 방법. 메서드 결과를 자동으로 Redis에 저장하고 재사용한다.

```markdown
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))  // TTL 10분
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.create(factory);
    }
}
```

```markdown
@Service
public class ProductService {

    @Cacheable(value = "products", key = "#id")
    public ProductResponse findById(Long id) {
        // 캐시 미스 시에만 실행
        return productRepository.findById(id).map(ProductResponse::from).orElseThrow();
    }

    @CacheEvict(value = "products", key = "#id")
    public void update(Long id, ProductUpdateRequest request) {
        // 수정 시 캐시 삭제
    }

    @CachePut(value = "products", key = "#result.id")
    public ProductResponse save(ProductCreateRequest request) {
        // 저장 후 캐시 갱신
    }
}
```

### RedisTemplate — 직접 제어

```markdown
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(Long userId, String token) {
        redisTemplate.opsForValue()
            .set("refresh:" + userId, token, Duration.ofDays(7));
    }

    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("refresh:" + userId);
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("refresh:" + userId);
    }
}
```

### 주요 자료구조

| 자료구조 | 메서드 | 사용 사례 |
|---------|--------|----------|
| String | `opsForValue()` | 단순 캐시, 카운터, 토큰 |
| Hash | `opsForHash()` | 객체 필드별 저장 |
| List | `opsForList()` | 메시지 큐, 최근 목록 |
| Set | `opsForSet()` | 중복 없는 목록, 태그 |
| ZSet | `opsForZSet()` | 랭킹, 점수 기반 정렬 |

### 인기 게시글 캐시 전략

```markdown
// 조회 요청 시: Cache-Aside 패턴
public PostResponse findPost(Long id) {
    String key = "post:" + id;
    String cached = redisTemplate.opsForValue().get(key);

    if (cached != null) {
        return objectMapper.readValue(cached, PostResponse.class);  // 캐시 히트
    }

    PostResponse response = postRepository.findById(id)...;  // DB 조회
    redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(response),
        Duration.ofMinutes(30));  // 캐시 저장
    return response;
}
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 캐시 무효화 시점 | 수정 후 캐시 미삭제 → 오래된 데이터 반환 | `@CacheEvict` 또는 수동 삭제 |
| 캐시 스탬피드 | 캐시 만료 시 동시 DB 요청 폭주 | 분산 락으로 한 건만 DB 조회 |
| Redis 장애 | 서비스 전체 장애 가능성 | Circuit Breaker, fallback 처리 |
| 직렬화 불일치 | 객체 변경 시 역직렬화 실패 | TTL 짧게 설정 또는 캐시 키 버전 관리 |
| 민감 데이터 캐시 | Redis는 암호화 없으므로 위험 | 민감 정보는 캐시하지 않음 |
