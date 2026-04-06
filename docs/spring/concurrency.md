> [← 홈](/study/) · [Spring](/study/spring/basic/)

## 동시성 제어

### 왜 쓰는가?

선착순 이벤트, 재고 차감, 결제 처리처럼 **여러 요청이 동시에 같은 데이터를 수정**할 때 데이터 정합성 문제가 발생한다. 동시성 제어로 레이스 컨디션을 방지한다.

### 문제 상황

```markdown
// 재고 100개, 동시에 100명이 차감 요청
public void decrease(Long itemId) {
    Item item = itemRepository.findById(itemId).get();
    item.decrease(1);          // 동시 접근 시 Lost Update 발생
    itemRepository.save(item); // 재고가 음수가 될 수 있음
}
```

---

### 낙관적 락 (Optimistic Lock)

충돌이 드물 것이라고 가정. 실제 충돌 시 예외를 던지고 재시도한다. DB 락을 걸지 않아 성능이 좋다.

```markdown
@Entity
public class Item {
    @Id private Long id;
    private int stock;

    @Version   // 낙관적 락 버전 컬럼
    private Long version;
}

// Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Item> findById(Long id);
}
```

```markdown
// 충돌 시 ObjectOptimisticLockingFailureException 발생 → 재시도
@Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3)
@Transactional
public void decrease(Long itemId) {
    Item item = itemRepository.findById(itemId).orElseThrow();
    item.decrease(1);
}
```

**언제 사용:** 읽기가 많고 쓰기 충돌이 드문 경우. (상품 좋아요, 조회수)

---

### 비관적 락 (Pessimistic Lock)

충돌이 많을 것이라고 가정. 조회 시점에 DB 락을 걸어 다른 트랜잭션의 접근을 차단한다.

```markdown
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)  // SELECT ... FOR UPDATE
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdWithLock(@Param("id") Long id);
}

@Transactional
public void decrease(Long itemId) {
    Item item = itemRepository.findByIdWithLock(itemId).orElseThrow();
    item.decrease(1);  // 락을 걸고 수정 → 다른 트랜잭션은 대기
}
```

**언제 사용:** 쓰기 충돌이 자주 발생하는 경우. (재고 차감, 선착순 쿠폰)

---

### Redis 분산 락 (Distributed Lock)

여러 서버 인스턴스에서 동시에 접근하는 경우, DB 락만으로 부족하다. Redis로 애플리케이션 레벨 락을 구현한다. Redisson 라이브러리를 주로 사용한다.

```markdown
implementation 'org.redisson:redisson-spring-boot-starter:3.27.0'
```

```markdown
@Service
@RequiredArgsConstructor
public class StockService {

    private final RedissonClient redissonClient;

    public void decrease(Long itemId) {
        RLock lock = redissonClient.getLock("lock:item:" + itemId);

        try {
            boolean acquired = lock.tryLock(5, 3, TimeUnit.SECONDS);
            // waitTime: 락 획득 대기 시간, leaseTime: 락 유지 시간

            if (!acquired) {
                throw new RuntimeException("락 획득 실패");
            }

            // 락 획득 성공 → 안전하게 수정
            Item item = itemRepository.findById(itemId).orElseThrow();
            item.decrease(1);
            itemRepository.save(item);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();  // 반드시 해제
            }
        }
    }
}
```

**언제 사용:** 다중 서버 환경, 외부 API 중복 호출 방지, 선착순 이벤트

---

### 비교

| 구분 | 낙관적 락 | 비관적 락 | Redis 분산 락 |
|------|---------|---------|--------------|
| 락 방식 | 버전 비교 (충돌 시 예외) | DB SELECT FOR UPDATE | Redis SETNX |
| 성능 | 높음 (락 없음) | 낮음 (대기 발생) | 중간 |
| 충돌 처리 | 재시도 필요 | 자동 대기 | 실패 처리 |
| 다중 서버 | 가능 | 가능 | 가능 (Redis 공유) |
| 적합 상황 | 충돌 드문 경우 | 충돌 잦은 경우 | MSA, 외부 API 제어 |

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 락 해제 누락 | 데드락 또는 영구 락 | `finally`에서 반드시 해제, TTL 설정 |
| 비관적 락 과다 | 전체 성능 저하 | 필요한 구간에만 적용 |
| 낙관적 락 재시도 무한 반복 | 계속 충돌 시 무한 루프 | 최대 재시도 횟수 제한 |
