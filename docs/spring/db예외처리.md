# 예외 처리 (DB 관점)

---

## 체크 예외 vs 언체크 예외

### 자바 예외 계층 구조

```text
Throwable
├── Error                          (시스템 오류, 처리 불가)
│   └── OutOfMemoryError 등
└── Exception                      (체크 예외)
    ├── IOException
    ├── SQLException               ← DB 관련 체크 예외
    └── RuntimeException           (언체크 예외)
        ├── NullPointerException
        ├── IllegalArgumentException
        └── ...
```

### 체크 예외 (Checked Exception)

- `Exception`을 상속한 예외 (RuntimeException 제외)
- **컴파일러가 예외 처리를 강제**함
- `throws`로 선언하거나 `try-catch`로 처리해야 함

```java
// 체크 예외 - 반드시 처리해야 함
public void method() throws SQLException { // 또는 try-catch
    Connection con = DriverManager.getConnection(url, user, pw);
}
```

### 언체크 예외 (Unchecked Exception)

- `RuntimeException`을 상속한 예외
- 처리하지 않아도 컴파일 오류 없음
- 예외 처리 여부를 개발자가 결정

```java
// 언체크 예외 - 처리 안 해도 됨
public void method() {
    throw new IllegalArgumentException("잘못된 인수");
}
```

### 비교

| 구분 | 체크 예외 | 언체크 예외 |
|---|---|---|
| 상속 | Exception | RuntimeException |
| 처리 강제 | O (컴파일 오류) | X |
| 대표 예 | IOException, SQLException | NPE, IllegalArgumentException |
| 용도 | 복구 가능한 예외 (이론적) | 복구 불가능하거나 프로그래밍 오류 |

---

## SQLException의 문제점

### 문제 1: 서비스 계층이 JDBC에 의존

```java
// 서비스 계층
public class MemberService {
    // SQLException은 JDBC 기술에 종속된 예외
    public void accountTransfer(String fromId, String toId, int money)
            throws SQLException { // 서비스가 JDBC를 알게 됨!
        // ...
    }
}
```

나중에 JPA로 교체하면 JPA 예외가 발생하는데, 서비스 코드의 `throws SQLException`을 전부 수정해야 한다.

### 문제 2: 체크 예외라서 throws 전파

```java
// 모든 계층에서 throws를 달아야 함
public class Controller {
    public void request() throws SQLException { // 컨트롤러도 JDBC를 알게 됨!
        service.method();
    }
}
public class Service {
    public void method() throws SQLException {
        repository.query();
    }
}
public class Repository {
    public void query() throws SQLException {
        // JDBC 코드
    }
}
```

### 문제 3: DB 오류 코드가 DB마다 다름

```java
// MySQL에서 중복 키 오류
// e.getErrorCode() == 1062

// Oracle에서 중복 키 오류
// e.getErrorCode() == 1

// DB마다 에러 코드가 달라 DB 종류가 바뀌면 예외 처리 코드도 수정 필요
```

---

## 스프링의 예외 추상화

### DataAccessException 계층

스프링은 DB 접근 관련 예외를 **언체크 예외**로 추상화한 계층을 제공한다.

```text
RuntimeException
└── DataAccessException                    (스프링 최상위 DB 예외)
    ├── NonTransientDataAccessException    (재시도해도 실패)
    │   ├── DataIntegrityViolationException  (무결성 제약 위반)
    │   │   └── DuplicateKeyException        (중복 키)
    │   ├── BadSqlGrammarException           (SQL 문법 오류)
    │   └── DataAccessResourceFailureException
    └── TransientDataAccessException       (재시도 시 성공 가능)
        ├── QueryTimeoutException            (쿼리 타임아웃)
        ├── OptimisticLockingFailureException
        └── CannotAcquireLockException       (잠금 획득 실패)
```

### 장점

1. **DB 독립**: MySQL이든 Oracle이든 같은 예외 클래스 (`DuplicateKeyException`)
2. **언체크 예외**: throws 선언 불필요, 서비스 계층이 깔끔해짐
3. **기술 독립**: JDBC든 JPA든 같은 계층의 예외 발생

```java
// 스프링 추상화 후 서비스 계층
public class MemberService {
    public void accountTransfer(String fromId, String toId, int money) {
        // throws 없음! 서비스가 JDBC 모름!
    }
}
```

---

## 예외 변환기 (SQLExceptionTranslator)

### 역할

`SQLException`의 에러 코드를 스프링 `DataAccessException` 계층으로 변환한다.

```java
public interface SQLExceptionTranslator {
    DataAccessException translate(String task, String sql, SQLException ex);
}
```

### SQLErrorCodeSQLExceptionTranslator

스프링이 기본으로 제공하는 구현체. DB별 에러 코드 매핑 파일을 가지고 있다.

```text
spring-jdbc.jar 내부
└── org/springframework/jdbc/support/sql-error-codes.xml
    ├── MySQL: 1062 → DuplicateKeyException
    ├── H2: 23001, 23505 → DuplicateKeyException
    └── Oracle: 1 → DuplicateKeyException
```

### 사용 예시

```java
@Slf4j
public class MemberRepositoryV4 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exceptionTranslator;

    public MemberRepositoryV4(DataSource dataSource) {
        this.dataSource = dataSource;
        // 생성자에 DataSource 전달 → DB 종류 자동 감지
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            // SQLException을 스프링 예외로 변환!
            throw exceptionTranslator.translate("save", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }
}
```

### 중복 키 예외 처리 예시

```java
public void save(Member member) {
    try {
        memberRepository.save(member);
    } catch (DuplicateKeyException e) {
        // DB가 MySQL이든 Oracle이든 같은 예외 클래스!
        log.error("중복 키 에러", e);
        throw new MyDuplicateMemberException(e);
    }
}
```

---

## Spring JdbcTemplate과 예외 처리

Spring JdbcTemplate은 내부적으로 예외 변환을 자동으로 처리한다.

```java
@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Member save(Member member) {
        String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)";
        // JdbcTemplate이 SQLException → DataAccessException 자동 변환
        // try-catch, 커넥션 관리, 자원 정리 모두 JdbcTemplate이 처리
        jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    public Member findById(String memberId) {
        String sql = "SELECT member_id, money FROM member WHERE member_id = ?";
        return jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }
}
```

---

## 예외 처리 전략 정리

### 레포지토리 계층 권장 패턴

```java
// 체크 예외를 언체크 예외로 변환해서 던지기
public Member findById(String memberId) {
    String sql = "SELECT * FROM member WHERE member_id = ?";
    try {
        // ...JDBC 코드
    } catch (SQLException e) {
        throw new RuntimeSQLException(e); // 체크 → 언체크 변환
    }
}

// 커스텀 런타임 예외
public class RuntimeSQLException extends RuntimeException {
    public RuntimeSQLException(Throwable cause) {
        super(cause);
    }
}
```

### 스프링 사용 시 권장 패턴

```java
// 1. JdbcTemplate 사용 → 자동으로 DataAccessException으로 변환
// 2. 또는 exceptionTranslator.translate() 직접 사용
// 3. 또는 @Repository 어노테이션 + 스프링 AOP → 자동 변환

@Repository  // ← 이 어노테이션이 예외 변환 AOP를 적용
public class MemberRepository {
    // JPA 사용 시, @Repository가 있으면 JPA 예외도 DataAccessException으로 자동 변환!
}
```

### @Repository의 예외 변환 기능

```text
[Repository 메서드 호출]
    ↓ 예외 발생
[@Repository + AOP 프록시]
    ↓ PersistenceExceptionTranslationPostProcessor 동작
[DataAccessException으로 변환]
    ↓
[서비스 계층으로 전달]
```

JPA, Hibernate 예외도 이 방식으로 스프링 예외로 변환된다.

---

## 체크 예외를 언체크로 변환해야 하는 이유

| 이유 | 설명 |
|---|---|
| 서비스 계층 순수성 | 서비스가 JDBC/JPA 같은 구현 기술을 몰라야 함 |
| 유연한 기술 교체 | DB 기술 교체 시 서비스 코드 변경 불필요 |
| throws 전파 방지 | 모든 계층에 throws를 달 필요 없음 |
| 복구 불가 예외 처리 | 대부분의 DB 예외는 애플리케이션 레벨에서 복구 불가 |

> 결론: DB 예외는 거의 대부분 복구가 불가능하다.
> 따라서 체크 예외로 강제 처리시키기보다 언체크 예외로 변환해서 공통 처리하는 것이 좋다.

---

## 언제 쓰는지

| 상황 | 적용 방법 |
|------|---------|
| **중복 키 감지** | `DuplicateKeyException` 캐치 → 비즈니스 예외로 변환 |
| **DB 기술 교체 가능성** | `DataAccessException`으로 추상화하여 JPA·JDBC 모두 동일하게 처리 |
| **체크 예외를 언체크로 변환** | Repository에서 `SQLException` → `RuntimeException` 래핑 후 전파 |
| **JdbcTemplate 사용 시** | 자동으로 `DataAccessException`으로 변환됨 — 별도 변환 불필요 |

## 장점

| 장점 | 설명 |
|------|------|
| **서비스 계층 순수성 유지** | `throws SQLException` 없이 비즈니스 로직만 작성 |
| **DB 기술 독립** | MySQL이든 Oracle이든 동일한 예외 계층(`DuplicateKeyException` 등) |
| **throws 전파 제거** | 체크 예외가 모든 계층을 오염시키는 문제 해결 |
| **공통 예외 처리** | `@ControllerAdvice`에서 `DataAccessException` 계층으로 일괄 처리 가능 |

## 단점

| 단점 | 설명 |
|------|------|
| **놓치기 쉬운 예외** | 언체크 예외라서 컴파일러가 강제하지 않음 → 누락 가능 |
| **추상화 레이어 복잡도** | `DataAccessException` 하위 계층을 알아야 세밀한 처리 가능 |
| **스택 트레이스 중첩** | 원인 예외가 래핑되어 디버깅이 다소 복잡 |

## 특징

- **`DataAccessException`은 언체크 예외**: 서비스·컨트롤러 계층에서 `throws` 없이 사용 가능
- **DB 벤더 에러 코드 매핑**: `sql-error-codes.xml`이 MySQL·Oracle·H2 등의 에러 코드를 `DataAccessException` 하위 클래스로 자동 변환
- **`@Repository` AOP**: `@Repository`가 붙은 클래스는 JPA·Hibernate 예외도 자동으로 `DataAccessException`으로 변환

## 주의할 점

<div class="warning-box" markdown="1">

**`@Transactional`의 롤백 기본 동작**

`@Transactional`은 기본적으로 **런타임 예외만 롤백**한다. 체크 예외(`Exception` 하위)는 롤백하지 않는다.

```java
// ❌ 체크 예외는 기본적으로 롤백 안 됨
@Transactional
public void method() throws Exception {
    throw new Exception("체크 예외");  // 롤백 X
}

// ✅ rollbackFor 명시
@Transactional(rollbackFor = Exception.class)
public void method() throws Exception {
    throw new Exception("이제 롤백됨");
}
```

</div>

<div class="warning-box" markdown="1">

**`DuplicateKeyException` 발생 전 검증으로 대체하지 말 것**

중복 키를 SELECT로 먼저 확인한 후 INSERT하는 방식은 동시성 문제(race condition)에 취약하다. DB 제약 조건(`UNIQUE`)을 신뢰하고 `DuplicateKeyException`을 처리하는 것이 더 안전하다.

```java
// ❌ SELECT 후 INSERT — 동시성 문제
if (!memberRepository.existsByEmail(email)) {
    memberRepository.save(member); // 동시에 같은 email이 INSERT될 수 있음
}

// ✅ UNIQUE 제약 + 예외 처리
try {
    memberRepository.save(member);
} catch (DuplicateKeyException e) {
    throw new EmailAlreadyExistsException();
}
```

</div>

## 베스트 프랙티스

<div class="success-box" markdown="1">

- **JdbcTemplate 사용** — 예외 변환이 자동으로 처리되어 `SQLExceptionTranslator` 직접 구현 불필요
- **`@Repository` 어노테이션** — JPA 사용 시 `@Repository`를 붙이면 JPA 예외도 `DataAccessException`으로 자동 변환
- **커스텀 비즈니스 예외로 래핑** — `DuplicateKeyException` 등을 그대로 서비스에 노출하지 말고 의미 있는 비즈니스 예외로 변환

```java
// ✅ 비즈니스 예외로 래핑
try {
    memberRepository.save(member);
} catch (DuplicateKeyException e) {
    throw new MemberEmailDuplicatedException(member.getEmail());
}
```

</div>

## 실무에서는?

| 실무 패턴 | 설명 |
|---------|------|
| **JdbcTemplate** | 예외 자동 변환, 별도 `SQLExceptionTranslator` 불필요 |
| **JPA + `@Repository`** | `@Repository` AOP가 JPA 예외 → `DataAccessException` 자동 변환 |
| **`@ControllerAdvice`** | `DataAccessException` 계층을 글로벌 핸들러에서 일괄 처리 |
| **중복 키 처리** | `DuplicateKeyException` 캐치 → 서비스 레이어에서 비즈니스 예외로 변환 후 응답 |
