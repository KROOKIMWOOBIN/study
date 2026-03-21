> [← 홈](/README.md) · [DB](/docs/db/db.md)

# 커넥션 풀 & DataSource

---

## 커넥션 풀이 필요한 이유

### 매번 커넥션을 새로 생성할 때의 문제

DB 커넥션을 새로 만드는 과정은 생각보다 매우 무겁다.

```
클라이언트 요청
    ↓
1. 애플리케이션 → DB 드라이버 커넥션 요청
    ↓
2. DB 드라이버 → DB 서버에 TCP/IP 연결 (3-way handshake)
    ↓
3. DB 서버 → 아이디/패스워드 인증
    ↓
4. DB 서버 → 내부 DB 세션 생성
    ↓
5. DB 서버 → 커넥션 생성 완료 응답
    ↓
6. DB 드라이버 → 커넥션 객체 반환
    ↓
애플리케이션이 커넥션 사용
```

이 과정은 **수십~수백 ms**가 소요된다. HTTP 요청마다 이 과정을 반복하면:
- 응답 속도가 느려짐
- DB 서버에 과부하 발생
- 사용자 경험 저하

---

## 커넥션 풀 동작 방식

### 개념

커넥션 풀은 **미리 여러 커넥션을 만들어두고 재사용**하는 방식이다.

```
[초기화 시점]
애플리케이션 시작
    → 커넥션 풀 초기화
    → 미리 커넥션 N개 생성 (기본값: 보통 10개)
    → 풀에 보관

[요청 처리 시점]
클라이언트 요청
    → 풀에서 커넥션 하나 꺼냄 (즉시 획득, 빠름)
    → SQL 실행
    → 커넥션을 풀에 반납 (닫지 않고 반환)

[다음 요청]
    → 같은 커넥션을 재사용
```

### 커넥션 풀 상태

```
[커넥션 풀]
┌─────────────────────────────────┐
│  Con1 [사용 중] ← 스레드 A       │
│  Con2 [사용 중] ← 스레드 B       │
│  Con3 [대기]                    │
│  Con4 [대기]                    │
│  Con5 [대기]                    │
│  ...  (기본 10개)               │
└─────────────────────────────────┘
```

### 커넥션 반납 시 주의점

- `con.close()`를 호출해도 **커넥션이 종료되지 않음** — 풀로 반납됨
- 커넥션 풀 구현체가 `close()`를 오버라이드해서 반납 로직으로 대체

---

## 장점 / 단점 / 주의사항

### 장점

| 장점 | 설명 |
|---|---|
| 응답 속도 향상 | 이미 생성된 커넥션을 즉시 사용 |
| DB 부하 감소 | 커넥션 생성/소멸 반복 제거 |
| 리소스 제한 | 최대 커넥션 수 제한으로 DB 보호 |
| 재사용성 | 커넥션을 반납 후 다른 요청이 재사용 |

### 단점 / 주의사항

| 단점 | 설명 |
|---|---|
| 풀 고갈(Pool Exhaustion) | 모든 커넥션이 사용 중이면 대기 → 타임아웃 |
| 메모리 사용 | 커넥션은 DB 서버 리소스도 점유함 |
| 커넥션 누수 | close()를 안 하면 반납이 안 됨 → 풀 고갈 |
| 설정 난이도 | 적절한 풀 크기 설정이 필요 (너무 크면 낭비, 너무 작으면 병목) |

### 풀 고갈 시나리오

```
동시 요청 100개
풀 크기 10개

→ 10개는 즉시 처리
→ 나머지 90개는 커넥션 대기
→ 대기 타임아웃 (connectionTimeout: 30초 기본)
→ 타임아웃 초과 시 SQLTimeoutException 발생
```

---

## DataSource 인터페이스

### 왜 추상화하는가?

커넥션 획득 방법은 여러 가지가 있다:

1. **DriverManager** - 매번 새 커넥션 생성
2. **HikariCP** - 커넥션 풀 (가장 많이 사용)
3. **DBCP2** - 아파치 커넥션 풀
4. **c3p0** - 오래된 커넥션 풀

만약 `DriverManager`를 직접 사용하다가 `HikariCP`로 교체하면 커넥션을 획득하는 코드를 **전부 수정**해야 한다.

```java
// DriverManager 방식
Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

// HikariCP 방식 (완전히 다른 API)
HikariDataSource ds = new HikariDataSource();
Connection con = ds.getConnection();
```

### DataSource 표준 인터페이스

`javax.sql.DataSource`는 커넥션 획득 방법을 추상화한 표준 인터페이스다.

```java
public interface DataSource {
    Connection getConnection() throws SQLException;
    // ...
}
```

```
[애플리케이션]
    ↓ DataSource (인터페이스)
    ├── DriverManagerDataSource  (테스트용, 매번 새 커넥션)
    ├── HikariDataSource         (실무 표준)
    ├── DBCP2DataSource          (아파치)
    └── ...
```

덕분에 커넥션 풀 구현체가 바뀌어도 **애플리케이션 코드는 변경 없음** (설정만 변경).

---

## HikariCP

### 특징

- Spring Boot 2.x 이상의 **기본 커넥션 풀**
- 가장 빠르고 가벼운 커넥션 풀 라이브러리
- 2012년부터 개발, 현재 사실상 표준

### 주요 설정 파라미터

| 파라미터 | 기본값 | 설명 |
|---|---|---|
| `maximumPoolSize` | 10 | 풀의 최대 커넥션 수 |
| `minimumIdle` | maximumPoolSize와 동일 | 유휴 커넥션 최소 수 |
| `connectionTimeout` | 30000 (30초) | 커넥션 획득 대기 최대 시간 (ms) |
| `idleTimeout` | 600000 (10분) | 유휴 커넥션 유지 시간 (minimumIdle < maximumPoolSize일 때만 동작) |
| `maxLifetime` | 1800000 (30분) | 커넥션 최대 수명 (DB 타임아웃보다 짧게 설정 권장) |
| `keepaliveTime` | 0 (비활성) | 유휴 커넥션 keepalive 쿼리 주기 |
| `connectionTestQuery` | null | 커넥션 유효성 확인 쿼리 |
| `poolName` | auto | 풀 이름 (로그 식별용) |

### application.yml 설정 예시

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/testdb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: MyHikariPool
```

---

## 코드 예제

### DriverManagerDataSource (테스트/학습용)

```java
@Test
void driverManager() throws SQLException {
    // 매번 새 커넥션 생성
    Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
    // 서로 다른 객체
}

@Test
void dataSourceDriverManager() throws SQLException {
    // DataSource 인터페이스를 통한 DriverManager 사용
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

    useDataSource(dataSource);
}

private void useDataSource(DataSource dataSource) throws SQLException {
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();

    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
}
```

### HikariDataSource (실무용)

```java
@Test
void dataSourceConnectionPool() throws SQLException, InterruptedException {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setMaximumPoolSize(10);
    dataSource.setPoolName("MyPool");

    useDataSource(dataSource);
    Thread.sleep(1000); // 풀에 커넥션 채워지는 것 확인 (별도 스레드)
}
```

### Repository에서 DataSource 주입받기

```java
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    // DataSource를 주입받으므로 DriverManager든 HikariCP든 코드 변경 없음
    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }

    public void save(Member member) throws SQLException {
        String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    // ... 나머지 CRUD 메서드
}
```

### Spring Bean으로 등록

```java
@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/testdb");
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepositoryV1(dataSource());
    }
}
```

---

## 커넥션 풀 크기 결정 기준

### 공식 (Hikari 권장)

```
최적 풀 크기 = CPU 코어 수 * 2 + 효과적인 스핀들 수
```

실제로는 다음 기준으로 튜닝:

| 고려사항 | 설명 |
|---|---|
| 동시 요청 수 | 최대 동시 요청 수 기준으로 설정 |
| DB 서버 스펙 | DB가 허용하는 최대 커넥션 수 확인 |
| 응답 시간 요구사항 | connectionTimeout 내에 처리 가능해야 함 |
| WAS 스레드 수 | 보통 WAS 스레드 수 = DB 풀 크기 정도 |

> 풀 크기를 무한정 늘린다고 성능이 좋아지지 않는다.
> DB도 CPU/메모리가 한정되어 있고, 컨텍스트 스위칭 비용이 오히려 성능 저하를 유발한다.

---

## 어떨 때 문제가 생기는가

| 문제 상황 | 원인 | 해결 방법 |
|---|---|---|
| 커넥션 풀 고갈 | 커넥션을 반납하지 않음 (누수) | try-finally / try-with-resources로 반드시 close() |
| 오래된 커넥션 에러 | DB 서버가 먼저 타임아웃으로 커넥션 종료 | `maxLifetime`을 DB wait_timeout보다 짧게 설정 |
| 느린 초기 응답 | 애플리케이션 시작 시 커넥션 생성 시간 | `minimumIdle` 설정으로 미리 확보 |
| 동시 요청 폭증 | 풀 크기 부족 | `maximumPoolSize` 늘리거나 DB 스케일업 |
| 배포 시 커넥션 끊김 | DB 재시작 또는 네트워크 문제 | `keepaliveTime` 및 `connectionTestQuery` 설정 |

```java
// 커넥션 누수 예시 - 절대 하면 안 됨
public void badExample() throws SQLException {
    Connection con = dataSource.getConnection();
    // ... 예외 발생 시 con.close()가 호출되지 않음
    // 풀로 반납되지 않아 풀 고갈 발생!
}

// 올바른 방법
public void goodExample() throws SQLException {
    Connection con = null;
    try {
        con = dataSource.getConnection();
        // ... 작업
    } finally {
        if (con != null) con.close(); // 항상 반납
    }
}
```
