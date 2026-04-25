## 아키텍처 패턴

### 왜 쓰는가?

<div class="concept-box" markdown="1">

코드 규모가 커질수록 **어디에 무엇을 두어야 하는지** 기준이 필요하다. 아키텍처 패턴은 역할을 명확히 분리해 유지보수성과 테스트 용이성을 높인다.

</div>

---

### Layered Architecture (계층형)

가장 일반적인 구조. 각 계층은 아래 계층만 의존한다.

```markdown
[Presentation Layer]  — Controller, DTO
        ↓
[Application Layer]   — Service
        ↓
[Domain Layer]        — Entity, Domain Logic
        ↓
[Infrastructure Layer]— Repository, DB, 외부 API
```

```markdown
// 전형적인 패키지 구조
com.myapp
├── controller    // HTTP 요청/응답
├── service       // 비즈니스 로직 조율
├── domain        // 엔티티, 도메인 로직
├── repository    // DB 접근
└── dto           // 계층 간 데이터 전달
```

**장점:** 구조가 단순하고 익히기 쉽다.
**단점:** 규모가 커지면 Service 계층이 비대해지고 도메인 로직이 분산된다.

---

### DDD (Domain-Driven Design) 기반 패키지 구조

기술 중심이 아닌 **도메인(업무) 중심**으로 패키지를 구성한다.

```markdown
com.myapp
├── member
│   ├── controller    MemberController
│   ├── service       MemberService
│   ├── domain        Member, MemberStatus
│   ├── repository    MemberRepository
│   └── dto           MemberCreateRequest, MemberResponse
├── order
│   ├── controller    OrderController
│   ├── service       OrderService
│   ├── domain        Order, OrderItem
│   └── repository    OrderRepository
└── payment
    ├── service       PaymentService
    └── domain        Payment
```

**장점:** 관련 코드가 모여 있어 이해하기 쉽고 MSA로의 전환이 쉽다.
**단점:** 팀 간 도메인 경계 합의가 필요하다.

---

### 핵심 설계 원칙

**단방향 의존성**

```markdown
// Good: Controller → Service → Repository
@Service
public class MemberService {
    private final MemberRepository repository;  // 아래 계층만 의존
}

// Bad: Repository → Service (역방향)
@Repository
public class MemberRepository {
    private final MemberService service;  // 순환 의존 발생
}
```

**도메인 로직은 엔티티에**

```markdown
// Bad: 서비스에 도메인 로직
public class OrderService {
    public void cancel(Order order) {
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("배송 중인 주문은 취소 불가");
        }
        order.setStatus(OrderStatus.CANCELLED);
    }
}

// Good: 엔티티에 도메인 로직
public class Order {
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("배송 중인 주문은 취소 불가");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
```

**DTO로 계층 경계**

```markdown
// 외부 → 컨트롤러 → 서비스: Request DTO
// 서비스 → 컨트롤러 → 외부: Response DTO
// 엔티티는 서비스/도메인 계층 내부에서만 사용
```

---

### 어떤 아키텍처를 선택할까?

| 상황 | 추천 |
|------|------|
| 스타트업, 소규모 팀 | Layered (단순, 빠른 개발) |
| 복잡한 도메인, 중규모 이상 | DDD 패키지 구조 |
| MSA 준비 중 | DDD + 도메인별 모듈 분리 |

### 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 과도한 추상화 | 간단한 CRUD에도 복잡한 구조 | 실제 복잡도에 맞는 구조 선택 |
| 도메인 로직이 서비스에 | 테스트 어렵고 재사용 불가 | 엔티티에 도메인 로직 위치 |
| 순환 의존성 | 컴파일 오류, 설계 오류 | 단방향 의존 규칙 준수 |
