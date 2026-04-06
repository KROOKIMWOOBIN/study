> [← 홈](/study/) · [Spring](/study/spring/basic/)

## JPA 성능 최적화

### N+1 문제

연관 엔티티를 `LAZY`로 로딩할 때, 목록 조회 후 각 엔티티의 연관 데이터를 개별 쿼리로 로딩하는 문제. N개 결과에 대해 N번의 추가 쿼리가 발생한다.

```markdown
// 문제 코드
List<Member> members = memberRepository.findAll();  // 쿼리 1번
for (Member m : members) {
    m.getOrders().size();  // 각 회원마다 쿼리 1번 → N번 추가
}
// 총 1 + N번 쿼리 실행
```

---

### Fetch Join

연관 엔티티를 한 번의 JOIN 쿼리로 함께 조회한다.

```markdown
// Repository
@Query("SELECT DISTINCT m FROM Member m JOIN FETCH m.orders WHERE m.status = :status")
List<Member> findWithOrders(@Param("status") MemberStatus status);

// QueryDSL
queryFactory
    .selectFrom(member)
    .leftJoin(member.orders, order).fetchJoin()
    .where(member.status.eq(status))
    .distinct()
    .fetch();
```

**주의:** 컬렉션 Fetch Join + 페이징을 동시에 사용하면 메모리에서 페이징 처리 → 심각한 성능 문제. 페이징 시에는 `@EntityGraph` 또는 Batch Size를 사용한다.

---

### @EntityGraph

```markdown
@EntityGraph(attributePaths = {"orders"})
@Query("SELECT m FROM Member m WHERE m.status = :status")
List<Member> findWithOrders(@Param("status") MemberStatus status);
```

---

### Batch Size

컬렉션을 IN 절로 한꺼번에 로딩한다. N+1을 1+1로 줄인다.

```markdown
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

또는 엔티티에 직접:
```markdown
@BatchSize(size = 100)
@OneToMany(mappedBy = "member")
private List<Order> orders;
```

```markdown
// 동작 원리
SELECT * FROM orders WHERE member_id IN (1, 2, 3, ..., 100)
// N번 쿼리 → IN 절 1번으로 처리
```

---

### DTO 직접 조회

엔티티 전체 컬럼 대신 필요한 컬럼만 조회한다.

```markdown
// JPQL DTO 조회
@Query("SELECT new com.myapp.dto.MemberSummary(m.id, m.name) FROM Member m")
List<MemberSummary> findAllSummary();

// QueryDSL DTO 조회 (더 권장)
queryFactory
    .select(Projections.constructor(MemberSummary.class, member.id, member.name))
    .from(member)
    .fetch();
```

---

### 읽기 전용 트랜잭션

```markdown
@Transactional(readOnly = true)
public List<MemberResponse> findAll() {
    return memberRepository.findAll().stream()
        .map(MemberResponse::from)
        .toList();
}
```

Dirty Checking 스킵으로 성능 향상. DB 레플리카 분기도 가능.

---

### Batch Insert

JPA의 `save()` 반복은 건별 INSERT. JDBC batch로 한꺼번에 처리한다.

```markdown
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 500
        order_inserts: true
        order_updates: true
```

```markdown
// saveAll()로 배치 처리
memberRepository.saveAll(members);  // batch_size 단위로 bulk insert
```

**주의:** MySQL에서 `GenerationType.IDENTITY`는 batch insert 비활성화됨 → `SEQUENCE` 전략 또는 직접 JDBC 사용 필요.

---

### 최적화 선택 기준

| 상황 | 해결책 |
|------|--------|
| 단건 조회 + 연관 엔티티 필요 | Fetch Join |
| 목록 조회 + 페이징 + 연관 엔티티 | Batch Size |
| 필요한 컬럼만 조회 | DTO 직접 조회 |
| 대량 데이터 저장 | Batch Insert |
| 조회 전용 | `readOnly = true` |

### 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 컬렉션 Fetch Join + 페이징 | OOM 위험 | Batch Size 사용 |
| 여러 컬렉션 Fetch Join | MultipleBagFetchException | 한 번에 하나만, 나머지는 Batch Size |
| 엔티티 반환 후 DTO 변환 | 불필요한 컬럼 조회 | DTO 직접 조회로 최적화 |
