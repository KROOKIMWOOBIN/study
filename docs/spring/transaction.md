## @Transactional

### 왜 쓰는가?

<div class="concept-box" markdown="1">

결제, 주문, 포인트 같은 여러 DB 작업은 **모두 성공하거나 모두 실패**해야 한다. ==@Transactional==은 AOP 기반으로 트랜잭션 시작/커밋/롤백을 자동 처리한다.

</div>

### 기본 사용

```markdown
@Service
public class OrderService {

    @Transactional
    public void placeOrder(OrderRequest request) {
        Order order = orderRepository.save(new Order(request));
        inventoryService.decrease(request.getItemId(), request.getQuantity());
        paymentService.pay(request.getAmount());
        // 위 중 하나라도 예외 발생 시 전체 롤백
    }
}
```

### 전파(Propagation)

부모 트랜잭션이 있을 때 자식 메서드의 트랜잭션 참여 방식을 결정한다.

| 전파 옵션 | 설명 | 사용 시나리오 |
|----------|------|--------------|
| `REQUIRED` (기본) | 있으면 참여, 없으면 새로 생성 | 대부분의 경우 |
| `REQUIRES_NEW` | 항상 새 트랜잭션 생성 (부모와 분리) | 로그 저장, 알림 (실패해도 본 트랜잭션 유지) |
| `NESTED` | 부모 안에 중첩 트랜잭션 (savepoint) | 일부만 롤백 가능 |
| `SUPPORTS` | 있으면 참여, 없으면 트랜잭션 없이 실행 | 읽기 전용 조회 |
| `NOT_SUPPORTED` | 트랜잭션 없이 실행 | 트랜잭션 불필요한 연산 |
| `NEVER` | 트랜잭션 있으면 예외 | 트랜잭션 없음을 강제 |
| `MANDATORY` | 트랜잭션 없으면 예외 | 반드시 트랜잭션 필요 |

```markdown
@Transactional
public void placeOrder(OrderRequest request) {
    orderRepository.save(order);
    notificationService.sendEmail(request);  // 실패해도 주문은 유지됨
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void sendEmail(OrderRequest request) {
    // 독립 트랜잭션으로 실행
}
```

### 격리 수준(Isolation)

| 격리 수준 | 팬텀 읽기 | 반복 불가능 읽기 | 더티 읽기 |
|----------|----------|----------------|---------|
| `READ_UNCOMMITTED` | 발생 | 발생 | 발생 |
| `READ_COMMITTED` | 발생 | 발생 | 방지 |
| `REPEATABLE_READ` | 발생 | 방지 | 방지 |
| `SERIALIZABLE` | 방지 | 방지 | 방지 |

기본값은 DB 설정을 따른다 (MySQL InnoDB: `REPEATABLE_READ`).

### 롤백 규칙

```markdown
// 기본: RuntimeException, Error → 롤백
//       CheckedException → 커밋

// CheckedException도 롤백하려면
@Transactional(rollbackFor = Exception.class)

// 특정 예외는 롤백 제외
@Transactional(noRollbackFor = BusinessException.class)
```

### readOnly 최적화

```markdown
@Transactional(readOnly = true)
public Member findById(Long id) {
    return memberRepository.findById(id).orElseThrow();
}
```

`readOnly = true`로 지정하면:
- Dirty Checking 스킵 → 성능 향상
- DB에 읽기 전용 커넥션 사용 가능 (레플리카 분기)
- 실수로 수정하는 것을 방지

### 주의할 점

**Self-Invocation (내부 호출)**

같은 클래스 내에서 `@Transactional` 메서드를 호출하면 프록시를 거치지 않아 트랜잭션이 적용되지 않는다.

```markdown
@Service
public class MemberService {

    public void outer() {
        inner();  // 프록시를 거치지 않음 → @Transactional 무시됨!
    }

    @Transactional
    public void inner() { ... }
}

// 해결: 별도 클래스로 분리 또는 ApplicationContext에서 Self 주입
```

**트랜잭션 밖에서 LAZY 로딩**

```markdown
// 컨트롤러에서 직접 엔티티의 LAZY 필드 접근 시 예외
Member member = memberService.findById(1L);
member.getOrders().size();  // LazyInitializationException!

// 해결: 서비스에서 DTO로 변환 후 반환
```

| 상황 | 문제 | 해결 |
|------|------|------|
| Self-Invocation | 트랜잭션 미적용 | 별도 클래스로 분리 |
| 너무 긴 트랜잭션 | 커넥션 점유, 락 경합 | 트랜잭션 범위 최소화 |
| 조회에 `@Transactional` 없음 | LAZY 로딩 오류 | `readOnly = true` 추가 |
| CheckedException 롤백 누락 | 데이터 불일치 | `rollbackFor = Exception.class` |

---

## 내부 동작 원리

### @Transactional은 사실 AOP다

> `@Transactional`은 마법이 아니다. 내부적으로 **AOP 프록시**가 트랜잭션 시작/커밋/롤백을 자동으로 처리할 뿐이다.

```
@Transactional이 붙은 OrderService 빈을 요청
  → 스프링 컨테이너가 실제 빈 대신 프록시 반환
  → orderService.placeOrder() 호출

프록시 내부 실행 (TransactionInterceptor.invoke()):
  ① TransactionManager.getTransaction()   ← 트랜잭션 시작
      → DataSource에서 Connection 획득
      → connection.setAutoCommit(false)    ← DB 자동 커밋 끔
  ② 실제 OrderService.placeOrder() 실행
  ③ 예외 없이 완료 → connection.commit()  ← 커밋
     RuntimeException 발생 → connection.rollback() ← 롤백
  ④ connection 반환 (커넥션 풀로)
```

### TransactionSynchronizationManager — ThreadLocal로 커넥션 공유

> 한 트랜잭션 안에서 여러 Repository가 같은 커넥션을 써야 한다. 어떻게 같은 커넥션을 공유할까?

```
@Transactional
public void placeOrder(OrderRequest request) {
    orderRepository.save(order);         // DB 연산 1
    inventoryService.decrease(...);      // DB 연산 2 (InventoryRepository 호출)
    paymentService.pay(...);             // DB 연산 3 (PaymentRepository 호출)
}
```

```
TransactionSynchronizationManager (내부: ThreadLocal<Map<DataSource, Connection>>)

① 트랜잭션 시작 시
   → DataSource에서 Connection 획득
   → ThreadLocal에 {dataSource → connection} 저장

② orderRepository.save() 실행
   → JdbcTemplate이 DataSourceUtils.getConnection(dataSource) 호출
   → ThreadLocal에서 현재 스레드의 Connection 조회 → 같은 Connection 반환

③ inventoryRepository, paymentRepository도 동일하게 같은 Connection 사용
   → 모두 같은 트랜잭션 안에서 실행

④ 트랜잭션 종료 시 ThreadLocal에서 Connection 제거
```

<div class="concept-box" markdown="1">

**ThreadLocal**: 스레드마다 독립적인 변수 공간을 제공하는 Java 기능.
같은 `TransactionSynchronizationManager` 인스턴스를 여러 스레드가 공유해도, 각 스레드는 자기 자신의 Connection을 독립적으로 가진다.
→ 동시에 여러 사용자 요청이 와도 커넥션이 섞이지 않는다.

</div>

### PlatformTransactionManager — DB 기술에 독립적인 트랜잭션

스프링은 트랜잭션 처리를 `PlatformTransactionManager` 인터페이스로 추상화한다.
`@Transactional` 코드는 그대로 두고 DB 기술만 교체할 수 있다.

| 구현체 | 사용 시 |
|--------|--------|
| `DataSourceTransactionManager` | JDBC, MyBatis |
| `JpaTransactionManager` | JPA, Hibernate |
| `JtaTransactionManager` | 분산 트랜잭션 (XA) |

```
@Transactional
  → TransactionInterceptor
  → PlatformTransactionManager.getTransaction()
       ↓
  [JPA 사용 시] JpaTransactionManager
       → EntityManager의 트랜잭션 시작
       → JDBC Connection도 함께 바인딩 (JPA와 JDBC 공존 가능)
```

### REQUIRES_NEW 동작 원리 — 왜 커넥션이 2개 필요한가?

```
@Transactional                          ← 외부 트랜잭션 (Connection A)
public void placeOrder() {
    orderRepository.save(order);

    notificationService.sendEmail();    ← REQUIRES_NEW (Connection B 새로 획득)
    // sendEmail() 실패해도 placeOrder()는 롤백 안 됨
    // Connection A, B가 독립적이기 때문
}

@Transactional(propagation = REQUIRES_NEW)
public void sendEmail() { ... }         ← 별도 트랜잭션 (Connection B)
```

<div class="warning-box" markdown="1">

**REQUIRES_NEW 주의**: 커넥션을 2개 동시에 점유한다. 커넥션 풀 크기가 작으면 데드락 위험.
최대 커넥션 수보다 많은 중첩 REQUIRES_NEW가 발생하면 모든 스레드가 커넥션을 기다리다 교착 상태에 빠질 수 있다.

</div>
