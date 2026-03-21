> [← 홈](/README.md) · [DB](/docs/db/db.md)

# JDBC (Java Database Connectivity)

---

## JDBC란? 왜 필요한가

### 문제 배경: DB마다 다른 접근법

과거에는 데이터베이스 벤더(MySQL, Oracle, PostgreSQL 등)마다 커넥션 연결, SQL 전달, 결과 수신 방법이 모두 달랐다.

```
MySQL 사용 시    → MySQL 전용 API 학습 + 코드 작성
Oracle로 변경   → Oracle 전용 API 재학습 + 코드 전면 수정
PostgreSQL로 변경 → 또 전부 재작성...
```

이렇게 되면 **DB를 바꿀 때마다 애플리케이션 코드 대부분을 수정**해야 하는 심각한 문제가 발생한다.

### JDBC의 등장 (표준화)

JDBC는 자바에서 데이터베이스에 접속할 수 있도록 하는 **자바 표준 API**다. (Java SE에 포함)

```
[애플리케이션] → [JDBC 표준 인터페이스] → [JDBC 드라이버] → [데이터베이스]
                    (java.sql.*)            (DB 벤더 구현체)
```

- **JDBC 인터페이스**: 자바가 정의한 표준 (java.sql.Connection, Statement, ResultSet)
- **JDBC 드라이버**: 각 DB 벤더가 JDBC 인터페이스를 구현한 라이브러리

덕분에 애플리케이션은 JDBC 표준 인터페이스에만 의존하고, DB가 바뀌어도 **드라이버만 교체**하면 된다.

---

## JDBC 표준 인터페이스 3가지

| 인터페이스 | 역할 | 주요 메서드 |
|---|---|---|
| `java.sql.Connection` | DB 연결(커넥션) | `getConnection()`, `close()`, `setAutoCommit()` |
| `java.sql.Statement` | SQL 전달 (PreparedStatement 포함) | `executeQuery()`, `executeUpdate()` |
| `java.sql.ResultSet` | SQL 실행 결과 수신 | `next()`, `getString()`, `getInt()` |

```
Connection (연결)
   └── Statement / PreparedStatement (SQL 실행)
              └── ResultSet (조회 결과)
```

---

## 장점 / 단점 / 특이점

### 장점

| 장점 | 설명 |
|---|---|
| DB 독립성 | DB가 바뀌어도 드라이버만 교체하면 코드 변경 최소화 |
| 표준화 | 한 번 배우면 어떤 DB든 동일한 방식으로 접근 가능 |
| 검증된 안정성 | 수십 년간 사용된 검증된 기술 |
| 저수준 제어 | SQL을 직접 다루므로 세밀한 최적화 가능 |

### 단점

| 단점 | 설명 |
|---|---|
| 반복 코드 | 커넥션 획득, 예외 처리, 자원 해제 코드가 매번 반복됨 |
| SQL 직접 작성 | 모든 SQL을 직접 작성해야 함 |
| 객체-관계 불일치 | 자바 객체와 DB 테이블 사이 매핑 코드를 직접 작성 |
| 예외 처리 복잡 | `SQLException`이 체크 예외라서 throws 처리 필수 |
| 자원 누수 위험 | Connection, Statement, ResultSet을 모두 직접 닫아야 함 |

### 특이점

- JDBC는 기술 그 자체보다는 **JPA, MyBatis, Spring JdbcTemplate의 기반 기술**로 동작
- 최신 기술들도 내부적으로는 JDBC를 사용함 (추상화 계층이 다를 뿐)
- `DriverManager.getConnection()`은 매번 새 커넥션을 생성함 (비용 큼)

---

## 기본 사용법

### 드라이버 등록 및 커넥션 획득

```java
// 최신 JDBC에서는 자동 등록 (META-INF/services)
// 예전 방식 (명시적 등록)
Class.forName("com.mysql.cj.jdbc.Driver");

Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/testdb",
    "root",
    "password"
);
```

### 데이터 등록 (INSERT)

```java
public void save(Member member) throws SQLException {
    String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, member.getMemberId());
        pstmt.setInt(2, member.getMoney());
        pstmt.executeUpdate(); // INSERT/UPDATE/DELETE → executeUpdate()
    } catch (SQLException e) {
        throw e;
    } finally {
        // 반드시 역순으로 닫아야 함
        close(con, pstmt, null);
    }
}
```

### 데이터 조회 (SELECT)

```java
public Member findById(String memberId) throws SQLException {
    String sql = "SELECT * FROM member WHERE member_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, memberId);
        rs = pstmt.executeQuery(); // SELECT → executeQuery()

        if (rs.next()) {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        } else {
            throw new NoSuchElementException("member not found memberId=" + memberId);
        }
    } catch (SQLException e) {
        throw e;
    } finally {
        close(con, pstmt, rs);
    }
}
```

### 데이터 수정 (UPDATE)

```java
public void update(String memberId, int money) throws SQLException {
    String sql = "UPDATE member SET money=? WHERE member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, money);
        pstmt.setString(2, memberId);
        int resultSize = pstmt.executeUpdate();
        log.info("resultSize={}", resultSize); // 영향받은 row 수 반환
    } catch (SQLException e) {
        throw e;
    } finally {
        close(con, pstmt, null);
    }
}
```

### 데이터 삭제 (DELETE)

```java
public void delete(String memberId) throws SQLException {
    String sql = "DELETE FROM member WHERE member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, memberId);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        throw e;
    } finally {
        close(con, pstmt, null);
    }
}
```

### 자원 정리 헬퍼

```java
private void close(Connection con, Statement stmt, ResultSet rs) {
    if (rs != null) {
        try {
            rs.close();
        } catch (SQLException e) {
            log.info("error", e);
        }
    }
    if (stmt != null) {
        try {
            stmt.close();
        } catch (SQLException e) {
            log.info("error", e);
        }
    }
    if (con != null) {
        try {
            con.close();
        } catch (SQLException e) {
            log.info("error", e);
        }
    }
}
```

> 자원을 닫을 때는 **역순**(ResultSet → Statement → Connection)으로 닫아야 한다.
> try-with-resources를 사용하면 더 깔끔하게 처리할 수 있다.

### try-with-resources 방식

```java
public Member findById(String memberId) throws SQLException {
    String sql = "SELECT * FROM member WHERE member_id = ?";

    try (Connection con = getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setString(1, memberId);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            throw new NoSuchElementException("member not found");
        }
    }
}
```

---

## Statement vs PreparedStatement

| 구분 | Statement | PreparedStatement |
|---|---|---|
| SQL 작성 | SQL에 값을 직접 문자열로 삽입 | `?` 파라미터 사용 |
| SQL Injection | 취약 | 방어됨 |
| 성능 | 매 실행마다 SQL 파싱 | SQL 미리 파싱 후 재사용 (캐싱) |
| 가독성 | 낮음 | 높음 |
| **권장** | 사용하지 않음 | **항상 PreparedStatement 사용** |

```java
// Statement - SQL Injection 위험
String sql = "SELECT * FROM member WHERE member_id = '" + memberId + "'";
// memberId에 "' OR '1'='1" 입력 시 전체 데이터 노출!

// PreparedStatement - 안전
String sql = "SELECT * FROM member WHERE member_id = ?";
pstmt.setString(1, memberId); // 값을 파라미터로 바인딩
```

---

## JDBC vs 최신 기술 비교

| 기술 | 특징 | SQL 작성 | 객체 매핑 | 학습 난이도 |
|---|---|---|---|---|
| **순수 JDBC** | 가장 저수준, 반복 코드 많음 | 직접 | 직접 | 낮음 |
| **Spring JdbcTemplate** | JDBC 반복 코드 제거, SQL은 직접 | 직접 | 일부 자동 | 낮음 |
| **MyBatis** | SQL을 XML 또는 어노테이션으로 관리 | 직접 | 자동 | 중간 |
| **JPA (Hibernate)** | 객체 중심, SQL 자동 생성 | 자동 생성 | 자동 | 높음 |
| **Spring Data JPA** | JPA 위에 추상화, 메서드 이름으로 쿼리 | 대부분 자동 | 자동 | 중간 |

```
[순수 JDBC] → [Spring JdbcTemplate] → [MyBatis] → [JPA] → [Spring Data JPA]
  낮은 추상화                                              높은 추상화
  높은 제어권                                              낮은 제어권
```

### 기술 선택 기준

- **동적 SQL이 많고 복잡한 쿼리**: MyBatis
- **객체 중심 개발, CRUD 자동화**: JPA + Spring Data JPA
- **복잡한 통계/집계 쿼리**: JPA + JPQL 또는 QueryDSL
- **레거시 시스템, 최소 의존성**: 순수 JDBC 또는 Spring JdbcTemplate

---

## 어떨 때 많이 쓰는가

| 상황 | 이유 |
|---|---|
| 레거시 프로젝트 유지보수 | 기존 코드가 JDBC 기반일 때 |
| DB 로우레벨 접근 필요 시 | 벤더 특화 기능, 배치 처리 등 |
| Spring JdbcTemplate 학습 전 기초 | 내부 동작 이해 목적 |
| 매우 가벼운 환경 | 의존성 최소화가 중요한 경우 |
| 다른 기술(JPA, MyBatis)의 내부 이해 | 모두 JDBC 기반이므로 |

> 실무에서는 순수 JDBC보다는 **Spring JdbcTemplate** 또는 **JPA + Spring Data JPA**를 주로 사용한다.
> 그러나 JDBC의 동작 원리를 알아야 커넥션 풀, 트랜잭션 등 상위 기술을 제대로 이해할 수 있다.
