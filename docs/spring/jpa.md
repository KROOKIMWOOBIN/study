## JPA / Spring Data JPA

### 왜 쓰는가?

<div class="concept-box" markdown="1">

JDBC로 직접 SQL을 작성하면 반복 코드가 많고, 객체와 테이블 간 변환을 직접 해야 한다. ==JPA==는 **객체 중심으로 DB를 다루게** 해주는 ==ORM(Object-Relational Mapping)==이다.

</div>

| 구분 | JDBC | JPA |
|------|------|-----|
| SQL 작성 | 직접 작성 | 자동 생성 |
| 결과 매핑 | 직접 매핑 | 자동 매핑 |
| 변경 감지 | 직접 UPDATE | Dirty Checking 자동 |
| 연관관계 | JOIN 직접 | 객체 참조로 표현 |

### Entity

```markdown
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
```

### Spring Data JPA — Repository

```markdown
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 메서드 이름으로 쿼리 자동 생성
    Optional<Member> findByEmail(String email);
    List<Member> findByStatusOrderByCreatedAtDesc(MemberStatus status);
    boolean existsByEmail(String email);

    // JPQL 직접 작성
    @Query("SELECT m FROM Member m WHERE m.name LIKE %:name%")
    List<Member> searchByName(@Param("name") String name);

    // 수정 쿼리
    @Modifying
    @Query("UPDATE Member m SET m.status = :status WHERE m.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") MemberStatus status);
}
```

### 영속성 컨텍스트

JPA가 엔티티를 관리하는 1차 캐시 공간. 같은 트랜잭션 내에서 같은 id 조회 시 DB를 다시 조회하지 않는다.

```markdown
// 같은 트랜잭션 내
Member m1 = memberRepository.findById(1L).get();
Member m2 = memberRepository.findById(1L).get();
System.out.println(m1 == m2); // true (동일 객체)
```

### Dirty Checking (변경 감지)

트랜잭션 내에서 엔티티 필드를 변경하면 커밋 시점에 자동으로 UPDATE가 실행된다.

```markdown
@Transactional
public void updateName(Long id, String name) {
    Member member = memberRepository.findById(id).orElseThrow();
    member.setName(name);  // UPDATE 쿼리 자동 실행, save() 불필요
}
```

### 연관관계

```markdown
// 다대일 (N:1) — 외래키를 가진 쪽 (연관관계의 주인)
@Entity
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩 권장
    @JoinColumn(name = "member_id")
    private Member member;
}

// 일대다 (1:N) — 조회 전용 (mappedBy)
@Entity
public class Member {
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
```

### FetchType

| 전략 | 설명 | 권장 |
|------|------|------|
| `LAZY` | 실제 사용 시점에 쿼리 | 기본 권장 |
| `EAGER` | 연관 엔티티 즉시 로딩 | N+1 문제 유발, 지양 |

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| `EAGER` 전략 | 예상치 못한 쿼리 + N+1 | 모두 `LAZY`로 변경 후 필요시 Fetch Join |
| 트랜잭션 밖에서 LAZY 로딩 | `LazyInitializationException` | 트랜잭션 내에서 로딩하거나 DTO 변환 |
| 영속성 컨텍스트 밖에서 엔티티 수정 | Dirty Checking 미작동 | `@Transactional` 내에서 수정 |
| 엔티티 직접 반환 | 순환 참조, 불필요 데이터 노출 | DTO로 변환 후 반환 |
| `ddl-auto: create` 운영 사용 | 테이블 삭제됨 | 운영은 `validate` 또는 `none` |

---

## 내부 동작 원리

### 영속성 컨텍스트 — JPA의 심장

> 영속성 컨텍스트(Persistence Context)는 엔티티를 **관리하는 메모리 공간**이다.
> "영속(永續)"은 "계속 유지된다"는 뜻 — 트랜잭션이 끝날 때까지 엔티티를 메모리에 보관하고 변경을 추적한다.

```java
EntityManager em;  // 영속성 컨텍스트를 감싸는 인터페이스

트랜잭션 시작
  → 영속성 컨텍스트 생성 (HashMap<EntityKey, Entity> 형태의 1차 캐시)

em.find(Member.class, 1L)
  → 1차 캐시 조회 (key: {Member, 1L})
  → 없으면 SELECT 쿼리 발행 → DB에서 데이터 가져옴
  → 1차 캐시에 저장
  → 스냅샷(원본 복사본)도 함께 저장 ← Dirty Checking에 사용

em.find(Member.class, 1L)  // 두 번째 호출
  → 1차 캐시에 이미 있음 → DB 쿼리 없이 캐시에서 반환
  → 동일 객체 참조 (member1 == member2 → true)

트랜잭션 커밋
  → flush() 자동 호출
  → 1차 캐시의 모든 엔티티와 스냅샷 비교
  → 변경된 필드가 있으면 UPDATE 쿼리 생성
  → 영속성 컨텍스트 종료
```

### Dirty Checking — 자동 UPDATE 원리

> "Dirty"는 "변경됨"을 의미한다. JPA가 엔티티의 변경을 감지해 자동으로 UPDATE 쿼리를 만드는 것이 Dirty Checking이다.

```java
@Transactional
public void updateName(Long id, String newName) {
    Member member = memberRepository.findById(id).orElseThrow();
    // member: {id=1, name="김철수"} ← 이때 스냅샷도 {id=1, name="김철수"} 저장

    member.setName(newName);
    // member: {id=1, name="이영희"} ← 스냅샷과 다름!
    // save() 호출 없음

    // 트랜잭션 커밋 시:
    // 엔티티 vs 스냅샷 비교 → name 필드가 다름 감지
    // → UPDATE members SET name='이영희' WHERE id=1 자동 실행
}
```

<div class="tip-box" markdown="1">

**readOnly = true를 쓰면 왜 빠른가?** `@Transactional(readOnly = true)`를 지정하면 JPA가 스냅샷을 저장하지 않는다. 비교할 필요가 없으니 메모리도 절약되고, flush()도 스킵된다. 조회 전용 서비스 메서드에는 항상 붙이는 것이 좋다.

</div>

### 엔티티 생명주기 4단계

```text
비영속 (new/transient)
  → 영속성 컨텍스트와 무관, 그냥 자바 객체
  Member member = new Member("김철수");

영속 (managed)
  → 영속성 컨텍스트가 관리 중, 변경 감지 대상
  em.persist(member);  또는  memberRepository.findById(1L)

준영속 (detached)
  → 영속성 컨텍스트에서 분리됨, 변경 감지 안 됨
  em.detach(member);  또는  트랜잭션 종료 후

삭제 (removed)
  → 삭제 예약됨, 커밋 시 DELETE 실행
  em.remove(member);  또는  memberRepository.delete(member)
```

### OSIV — 트랜잭션 밖에서 LAZY 로딩 문제

> OSIV(Open Session In View): HTTP 요청 시작부터 끝까지 영속성 컨텍스트를 열어두는 방식.

```text
Spring Boot 기본값: spring.jpa.open-in-view=true (OSIV 활성화)

장점: 컨트롤러, 뷰에서도 LAZY 로딩 가능
단점: DB 커넥션을 HTTP 요청 전체 기간 동안 점유
      → 트래픽이 많으면 커넥션 부족 → 성능 저하
```

```yaml
# 실무 권장 설정
spring:
  jpa:
    open-in-view: false  # OSIV 끔

# 결과: 트랜잭션 밖에서 LAZY 로딩 시 LazyInitializationException 발생
# 해결: 트랜잭션 내에서 필요한 연관 데이터 모두 로딩 후 DTO 변환
```

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">

**OSIV=true (기본값, 위험)**

```java
// 컨트롤러에서 LAZY 로딩
@GetMapping("/members/{id}")
public MemberDto find(@PathVariable Long id) {
    Member member = memberService.findById(id);
    // 트랜잭션 종료 후에도 DB 조회 가능
    // → 커넥션 장시간 점유
    member.getOrders().size(); // LAZY 로딩
    return MemberDto.from(member);
}
```

</div>
<div class="after" markdown="1">

**OSIV=false (권장)**

```java
// 서비스에서 DTO로 변환
@Transactional(readOnly = true)
public MemberDto findById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow();
    // 트랜잭션 안에서 필요한 것 모두 로딩
    member.getOrders().size(); // 여기서 로딩
    return MemberDto.from(member); // DTO 변환 후 반환
}
```

</div>
</div>
