## @Transactional

### 왜 쓰는가?

결제, 주문, 포인트 같은 여러 DB 작업은 **모두 성공하거나 모두 실패**해야 한다. `@Transactional`은 AOP 기반으로 트랜잭션 시작/커밋/롤백을 자동 처리한다.

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
