## QueryDSL

### 왜 쓰는가?

<div class="concept-box" markdown="1">

JPQL은 문자열이라 **컴파일 타임에 오류를 잡을 수 없고**, 동적 쿼리 작성이 복잡하다. ==QueryDSL==은 Java 코드로 쿼리를 작성해 **타입 안전성**과 **IDE 자동완성**을 제공한다.

</div>

| 구분 | JPQL | QueryDSL |
|------|------|----------|
| 문법 오류 발견 | 런타임 | 컴파일 타임 |
| 동적 쿼리 | 문자열 조합 (복잡) | BooleanExpression 조합 (간결) |
| 타입 안전성 | X | O (Q클래스) |
| IDE 자동완성 | X | O |

### 설정

```markdown
// build.gradle
dependencies {
    implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
    annotationProcessor 'com.querydsl:querydsl-apt:5.0.0:jakarta'
    annotationProcessor 'jakarta.annotation:jakarta.annotation-api'
    annotationProcessor 'jakarta.persistence:jakarta.persistence-api'
}
```

빌드 후 Q클래스가 자동 생성된다: `QMember`, `QOrder` 등

### 기본 사용

```markdown
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Member> findByName(String name) {
        return queryFactory
            .selectFrom(member)
            .where(member.name.eq(name))
            .orderBy(member.createdAt.desc())
            .fetch();
    }
}
```

```markdown
// JPAQueryFactory Bean 등록
@Bean
public JPAQueryFactory jpaQueryFactory(EntityManager em) {
    return new JPAQueryFactory(em);
}
```

### 동적 쿼리 — BooleanExpression

null을 반환하면 자동으로 조건에서 제외된다. 이를 활용해 동적 검색을 구현한다.

```markdown
public List<Member> search(String name, MemberStatus status, Integer minAge) {
    return queryFactory
        .selectFrom(member)
        .where(
            nameContains(name),
            statusEq(status),
            ageGoe(minAge)
        )
        .fetch();
}

private BooleanExpression nameContains(String name) {
    return name != null ? member.name.contains(name) : null;
}

private BooleanExpression statusEq(MemberStatus status) {
    return status != null ? member.status.eq(status) : null;
}

private BooleanExpression ageGoe(Integer age) {
    return age != null ? member.age.goe(age) : null;
}
```

### 페이징

```markdown
public Page<MemberResponse> searchWithPaging(MemberSearchRequest request, Pageable pageable) {
    List<MemberResponse> content = queryFactory
        .select(new QMemberResponse(member.id, member.name, member.email))
        .from(member)
        .where(nameContains(request.getName()))
        .orderBy(member.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(member.count())
        .from(member)
        .where(nameContains(request.getName()))
        .fetchOne();

    return new PageImpl<>(content, pageable, total);
}
```

### Join

```markdown
// 회원 + 주문 조인
queryFactory
    .selectFrom(member)
    .leftJoin(member.orders, order).fetchJoin()
    .where(order.status.eq(OrderStatus.PENDING))
    .distinct()
    .fetch();
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| Q클래스 미생성 | 빌드 안 하면 컴파일 오류 | `./gradlew compileJava` 실행 |
| `fetchJoin()`과 페이징 동시 사용 | 메모리에서 페이징 처리 (성능 위험) | 페이징은 fetchJoin 없이, 별도 쿼리로 |
| 복잡한 DTO 프로젝션 | `@QueryProjection`은 DTO가 QueryDSL 의존 | `Projections.constructor()` 사용 고려 |
| 단순 쿼리까지 QueryDSL 사용 | 불필요한 복잡성 | 단순 조회는 JPA 메서드 쿼리로 |
