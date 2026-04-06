> [← 홈](/study/) · [Spring](/study/spring/basic/)

## JPA / Spring Data JPA

### 왜 쓰는가?

JDBC로 직접 SQL을 작성하면 반복 코드가 많고, 객체와 테이블 간 변환을 직접 해야 한다. JPA는 **객체 중심으로 DB를 다루게** 해주는 ORM(Object-Relational Mapping)이다.

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
