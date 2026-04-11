# DB (Database)

데이터베이스 핵심 개념 정리.

---

## 데이터베이스란?

<div class="concept-box" markdown="1">

**데이터베이스(Database)**: 데이터의 집합소. 여러 사람이 공유하고 사용할 목적으로 체계적으로 저장·관리하는 데이터 모음.

</div>

| 용어 | 설명 |
|------|------|
| **데이터(Data)** | 개별적인 사실 (예: 27, "김철수", true) |
| **정보(Information)** | 가공·처리된 데이터 — 의미가 부여된 것 (예: "나이 27살인 회원 김철수") |

---

## 데이터베이스를 쓰는 이유

파일 시스템(텍스트 파일, 엑셀 등)에 데이터를 저장하면 생기는 문제를 DB가 해결한다.

| 문제 | DB가 해결하는 방법 |
|------|------------------|
| **보안** | 접근 권한(Role) 관리, 인증 없이는 데이터 접근 불가 |
| **동시성** | 여러 사용자가 동시에 읽고 쓸 때 충돌 방지 (Lock, MVCC) |
| **데이터 회복 & 백업** | 장애 발생 시 트랜잭션 로그로 복구 가능 |
| **ACID 보장** | 데이터 일관성과 무결성을 트랜잭션 수준에서 보장 |

---

## DBMS (DataBase Management System)

<div class="concept-box" markdown="1">

**DBMS**: 데이터베이스를 관리하는 소프트웨어. 사용자가 SQL로 요청하면 DBMS가 실제 데이터를 읽고 쓴다.

대표적인 DBMS: MySQL, PostgreSQL, Oracle, MariaDB, SQLite

</div>

DBMS가 제공하는 핵심 언어:

| 분류 | 이름 | 역할 | 주요 명령어 |
|------|------|------|------------|
| **DDL** | Data Definition Language | 구조(스키마) 정의 | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| **DML** | Data Manipulation Language | 데이터 조작 | `SELECT`, `INSERT`, `UPDATE`, `DELETE` |
| **DCL** | Data Control Language | 권한 제어 | `GRANT`, `REVOKE` |
| **TCL** | Transaction Control Language | 트랜잭션 제어 | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

DBMS의 부가 기능:
- **보안**: 사용자별 접근 권한 제어
- **동시성 제어**: Lock, MVCC로 동시 접근 충돌 방지
- **트랜잭션 관리**: ACID 보장

---

## 학습 목록

| 주제 | 한 줄 설명 |
|------|-----------|
| [JDBC](./JDBC.md) | Java DB 표준 인터페이스 — Connection, Statement, ResultSet |
| [커넥션 풀 & DataSource](./커넥션풀-DataSource.md) | HikariCP, 커넥션 재사용, DataSource 추상화 |
| [트랜잭션](./트랜잭션.md) | ACID, @Transactional, 전파(Propagation), 동기화 매니저 |
| [트랜잭션 격리 수준](./격리수준.md) | READ UNCOMMITTED ~ SERIALIZABLE, Dirty Read / Phantom Read |
| [예외 처리](./예외처리.md) | SQLException 문제, DataAccessException 계층, 예외 변환기 |


---

## ACID

<div class="concept-box" markdown="1">

**ACID**: 트랜잭션이 안전하게 수행되기 위해 보장해야 하는 4가지 성질.

</div>

### 왜 필요한가?

여러 사용자가 동시에 DB를 읽고 쓰면 데이터가 깨질 수 있다. 예를 들어 계좌 이체 중 서버가 죽으면 돈이 빠져나갔는데 입금이 안 된 상태가 될 수 있다. ACID는 이런 상황에서 데이터 정합성을 보장하는 약속이다.

### 4가지 성질

| 성질 | 이름 | 설명 |
|------|------|------|
| **A** | Atomicity (원자성) | 트랜잭션 내 연산은 ==전부 성공하거나 전부 실패==한다. 중간 상태가 없다. |
| **C** | Consistency (일관성) | 트랜잭션 전후로 DB가 정의한 규칙(제약 조건, 무결성)을 항상 만족해야 한다. |
| **I** | Isolation (격리성) | 동시에 실행되는 트랜잭션끼리 서로의 중간 상태를 볼 수 없다. |
| **D** | Durability (지속성) | 커밋된 데이터는 장애가 발생해도 사라지지 않는다. (WAL 로그로 복구 가능) |

### 각 성질이 깨지면?

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Atomicity 위반**

```sql
-- 계좌 이체 도중 서버 장애
UPDATE account SET balance = balance - 10000 WHERE id = 'A'; -- 성공
-- 서버 죽음 → B에 입금 안 됨
UPDATE account SET balance = balance + 10000 WHERE id = 'B'; -- 미실행
```
→ 돈이 증발
</div>
<div class="after" markdown="1">
**Atomicity 보장**

```sql
BEGIN;
UPDATE account SET balance = balance - 10000 WHERE id = 'A';
UPDATE account SET balance = balance + 10000 WHERE id = 'B';
COMMIT; -- 둘 다 성공해야 커밋, 아니면 ROLLBACK
```
→ 전부 성공 or 전부 취소
</div>
</div>

### 특징

- ACID는 **DBMS가 내부적으로 보장**한다. 개발자가 직접 구현하는 게 아니다.
- Isolation의 강도는 **격리 수준(Isolation Level)** 으로 조절한다. (아래 섹션 참고)
- ACID를 완전히 보장하면 **성능이 낮아진다.** 특히 Isolation이 강할수록 Lock 경합이 많아진다.
- NoSQL은 일반적으로 ACID보다 **가용성·성능을 우선**해 일부 성질을 완화한다. (BASE 모델)

<div class="warning-box" markdown="1">

**주의**: Atomicity는 "전부 or 전부 아님"이지만, 이것만으로는 동시 실행 문제가 해결되지 않는다. 동시성 문제는 **Isolation Level**로 별도 제어해야 한다.

</div>

---

## 격리 수준 (Isolation Level)

<div class="concept-box" markdown="1">

**격리 수준**: 여러 트랜잭션이 동시에 실행될 때 서로 얼마나 영향을 주는지 결정하는 단계.

</div>

### 왜 필요한가?

Isolation을 완벽하게 보장(SERIALIZABLE)하면 모든 트랜잭션이 순서대로 실행되어 성능이 급격히 떨어진다. 반대로 격리를 약하게 하면 성능은 올라가지만 데이터 정합성 문제가 발생할 수 있다. **격리 수준은 이 트레이드오프를 상황에 따라 조절하는 수단**이다.

### 발생할 수 있는 문제 현상

| 현상 | 설명 |
|------|------|
| **Dirty Read** | 아직 커밋되지 않은 데이터를 다른 트랜잭션이 읽는 것 |
| **Non-Repeatable Read** | 같은 트랜잭션 안에서 같은 행을 두 번 읽었는데 결과가 다른 것 (중간에 다른 트랜잭션이 UPDATE·COMMIT) |
| **Phantom Read** | 같은 쿼리를 두 번 실행했는데 결과 행 수가 다른 것 (중간에 다른 트랜잭션이 INSERT·COMMIT) |

### 4단계 격리 수준

| 격리 수준 | Dirty Read | Non-Repeatable Read | Phantom Read | 성능 |
|-----------|:----------:|:-------------------:|:------------:|:----:|
| **READ UNCOMMITTED** | 발생 | 발생 | 발생 | 가장 빠름 |
| **READ COMMITTED** | 방지 | 발생 | 발생 | 빠름 |
| **REPEATABLE READ** | 방지 | 방지 | 발생 | 보통 |
| **SERIALIZABLE** | 방지 | 방지 | 방지 | 가장 느림 |

<div class="tip-box" markdown="1">

**팁**: MySQL InnoDB의 기본값은 `REPEATABLE READ`이고, PostgreSQL·Oracle의 기본값은 `READ COMMITTED`다. 실무에서는 대부분 `READ COMMITTED` 또는 `REPEATABLE READ`를 사용한다.

</div>

### 각 수준 상세

=== "READ UNCOMMITTED"
    - 커밋되지 않은 데이터도 읽는다.
    - <span class="text-red">Dirty Read 발생</span> — 롤백된 데이터를 읽을 수 있어 실무에서 거의 사용 안 함.

=== "READ COMMITTED"
    - 커밋된 데이터만 읽는다.
    - <span class="text-green">Dirty Read 방지</span>
    - 같은 트랜잭션 안에서 같은 행을 다시 읽으면 다른 결과가 나올 수 있다. (Non-Repeatable Read)

=== "REPEATABLE READ"
    - 트랜잭션이 시작된 시점의 스냅샷을 기준으로 읽는다. (MVCC)
    - <span class="text-green">Non-Repeatable Read 방지</span>
    - MySQL InnoDB는 갭 락(Gap Lock)으로 Phantom Read도 어느 정도 방지한다.

=== "SERIALIZABLE"
    - 트랜잭션들이 순서대로 실행되는 것처럼 동작한다.
    - 모든 이상 현상 방지.
    - <span class="text-orange">Lock 경합이 극심해 실무에서는 거의 사용 안 함.</span>

### 주의할 점

<div class="warning-box" markdown="1">

**주의**: 격리 수준은 DB 전체 기본값과 트랜잭션 단위 설정 모두 가능하다. `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;`처럼 트랜잭션마다 다르게 줄 수 있다. Spring에서는 `@Transactional(isolation = Isolation.READ_COMMITTED)`로 지정한다.

</div>

---

## SQL

<div class="concept-box" markdown="1">

**SQL(Structured Query Language)**: 관계형 데이터베이스와 통신하기 위한 표준 언어. 데이터를 정의·조작·제어하는 명령어 집합이다.

</div>

### 왜 쓰는가?

DBMS마다 내부 구현이 다르지만, SQL이라는 표준 언어 덕분에 **MySQL, PostgreSQL, Oracle 등 어떤 RDBMS든 같은 방식으로 데이터를 다룰 수 있다.** 개발자가 DBMS 내부 파일 구조를 몰라도 선언적(무엇을 원하는지)으로 데이터를 요청할 수 있다.

### 특징

- **선언형 언어**: "어떻게"가 아니라 "무엇을" 원하는지 기술한다. 실행 계획은 DBMS의 쿼리 옵티마이저가 결정한다.
- **집합 기반**: 행 하나가 아니라 결과 집합(테이블) 단위로 동작한다.
- **ANSI/ISO 표준**: 표준이 있지만 DBMS마다 방언(Dialect)이 존재한다. (예: MySQL의 `LIMIT` vs Oracle의 `ROWNUM`)

### 기본 SELECT 구조

```sql
SELECT   컬럼1, 컬럼2          -- 3. 조회할 컬럼 선택
FROM     테이블명               -- 1. 대상 테이블
WHERE    조건                   -- 2. 행 필터링
GROUP BY 그룹 기준 컬럼         -- 4. 그룹화
HAVING   그룹 조건              -- 5. 그룹 필터링
ORDER BY 정렬 기준 컬럼         -- 6. 정렬
LIMIT    n;                     -- 7. 행 수 제한
```

!!! note "실행 순서"
    SQL 작성 순서와 실제 실행 순서는 다르다. 실행 순서: `FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT`

---

## DDL · DML · DCL · TCL

SQL 명령어는 역할에 따라 4가지로 분류된다.

### DDL (Data Definition Language) — 구조 정의

| 명령어 | 설명 |
|--------|------|
| `CREATE` | 테이블·뷰·인덱스 등 객체 생성 |
| `ALTER` | 객체 구조 변경 (컬럼 추가·삭제·타입 변경) |
| `DROP` | 객체 완전 삭제 |
| `TRUNCATE` | 테이블의 모든 데이터 삭제 (구조는 유지) |
| `RENAME` | 객체 이름 변경 |

```sql
CREATE TABLE member (
    id   BIGINT      PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age  INT
);

ALTER TABLE member ADD COLUMN email VARCHAR(100);
DROP TABLE member;
TRUNCATE TABLE member; -- DELETE보다 빠르지만 롤백 불가 (MySQL 기준)
```

<div class="danger-box" markdown="1">

**위험**: DDL은 대부분 DBMS에서 **자동 커밋(Auto-Commit)** 된다. `DROP`, `TRUNCATE` 실행 후 ROLLBACK이 불가능한 경우가 많다. (PostgreSQL은 트랜잭션 내 DDL 롤백 가능)

</div>

### DML (Data Manipulation Language) — 데이터 조작

| 명령어 | 설명 |
|--------|------|
| `SELECT` | 데이터 조회 |
| `INSERT` | 데이터 삽입 |
| `UPDATE` | 데이터 수정 |
| `DELETE` | 데이터 삭제 |

```sql
INSERT INTO member (name, age) VALUES ('김철수', 27);

SELECT * FROM member WHERE age > 20 ORDER BY name;

UPDATE member SET age = 28 WHERE id = 1;

DELETE FROM member WHERE id = 1;
```

<div class="warning-box" markdown="1">

**주의**: `UPDATE`와 `DELETE`에서 `WHERE` 절을 빠뜨리면 **전체 행이 영향을 받는다.** 실행 전 반드시 `SELECT`로 대상 확인을 권장한다.

</div>

### DCL (Data Control Language) — 권한 제어

| 명령어 | 설명 |
|--------|------|
| `GRANT` | 사용자에게 권한 부여 |
| `REVOKE` | 사용자의 권한 회수 |

```sql
GRANT SELECT, INSERT ON member TO 'app_user'@'%';
REVOKE INSERT ON member FROM 'app_user'@'%';
```

### TCL (Transaction Control Language) — 트랜잭션 제어

| 명령어 | 설명 |
|--------|------|
| `COMMIT` | 트랜잭션 내 변경사항 영구 저장 |
| `ROLLBACK` | 트랜잭션 내 변경사항 취소 |
| `SAVEPOINT` | 트랜잭션 내 중간 저장 지점 설정 |

```sql
BEGIN;
UPDATE account SET balance = balance - 10000 WHERE id = 'A';
SAVEPOINT before_transfer;
UPDATE account SET balance = balance + 10000 WHERE id = 'B';
-- 문제 발생 시
ROLLBACK TO before_transfer;
COMMIT;
```

---

## 데이터 타입

테이블 컬럼을 만들 때 저장할 데이터의 종류에 맞는 타입을 지정해야 한다. 잘못된 타입 선택은 저장 공간 낭비, 성능 저하, 정합성 문제를 일으킨다.

### 숫자형

| 타입 | 크기 | 범위 | 사용 예 |
|------|------|------|---------|
| `TINYINT` | 1 byte | -128 ~ 127 (UNSIGNED: 0~255) | boolean 대용, 상태값 |
| `SMALLINT` | 2 byte | -32,768 ~ 32,767 | |
| `INT` / `INTEGER` | 4 byte | 약 ±21억 | 일반 정수, PK |
| `BIGINT` | 8 byte | 약 ±922경 | 대용량 PK, 타임스탬프 |
| `DECIMAL(p, s)` | 가변 | 정밀한 소수 | <span class="text-green">금액·통화 (정확도 우선)</span> |
| `FLOAT` / `DOUBLE` | 4 / 8 byte | 부동소수점 | <span class="text-orange">과학 계산 (근사치)</span> |

<div class="danger-box" markdown="1">

**위험**: 금액에 `FLOAT`/`DOUBLE`을 쓰면 부동소수점 오차로 계산이 틀릴 수 있다. 반드시 `DECIMAL`을 사용한다.

</div>

### 문자형

| 타입 | 특징 | 사용 예 |
|------|------|---------|
| `CHAR(n)` | 고정 길이, 빈 공간은 공백 패딩 | 주민번호, 국가코드 등 길이가 항상 일정한 값 |
| `VARCHAR(n)` | 가변 길이, 실제 길이만큼만 저장 | 이름, 이메일 등 길이가 다양한 문자열 |
| `TEXT` | 최대 65,535 byte | 게시글 본문 등 긴 텍스트 |
| `LONGTEXT` | 최대 4GB | 매우 큰 텍스트 |

<div class="tip-box" markdown="1">

**팁**: `CHAR(10)`은 항상 10바이트를 차지하지만 읽기 속도가 빠르다. `VARCHAR(10)`은 저장 공간을 아끼지만 길이 정보를 별도로 관리한다. 길이가 고정된 값이라면 `CHAR`가 유리하다.

</div>

### 날짜·시간형

| 타입 | 형식 | 범위 | 특징 |
|------|------|------|------|
| `DATE` | `YYYY-MM-DD` | 1000-01-01 ~ 9999-12-31 | 날짜만 저장 |
| `TIME` | `HH:MM:SS` | | 시간만 저장 |
| `DATETIME` | `YYYY-MM-DD HH:MM:SS` | 1000-01-01 ~ 9999 | 시간대 변환 없음 |
| `TIMESTAMP` | `YYYY-MM-DD HH:MM:SS` | 1970 ~ 2038 | UTC 저장, 조회 시 시간대 변환 |

<div class="warning-box" markdown="1">

**주의**: `TIMESTAMP`는 2038년 문제(Unix 32bit 오버플로)가 있다. 장기 보존 데이터는 `DATETIME`을 쓰되 애플리케이션 레벨에서 UTC 관리를 권장한다.

</div>

### 기타

| 타입 | 설명 |
|------|------|
| `BOOLEAN` / `BOOL` | MySQL에서는 내부적으로 `TINYINT(1)` |
| `JSON` | MySQL 5.7+, PostgreSQL 9.2+ 지원. JSON 형식 데이터 저장 및 경로 조회 가능 |
| `BLOB` | 이진 데이터 (이미지·파일). 실무에서는 파일 자체는 S3 등 외부 저장소에 두고 DB에는 URL만 저장하는 방식을 선호 |

---

## 제약 조건 (Constraint)

<div class="concept-box" markdown="1">

**제약 조건**: 테이블에 저장되는 데이터의 **무결성(Integrity)** 을 보장하기 위해 컬럼이나 테이블에 걸어두는 규칙. 규칙을 어기는 데이터는 DBMS가 거부한다.

</div>

### 왜 쓰는가?

애플리케이션 코드로 유효성 검사를 하더라도 여러 서비스가 하나의 DB를 공유하거나 직접 SQL로 데이터를 수정하는 경우에는 코드가 우회될 수 있다. 제약 조건을 DB 레벨에서 걸면 어떤 경로로 데이터가 들어오든 **무결성이 보장**된다.

### 종류

#### NOT NULL

```sql
CREATE TABLE member (
    name VARCHAR(50) NOT NULL  -- NULL 저장 불가
);
```

- `NULL`을 허용하지 않는다.
- <span class="text-orange">주의</span>: `NULL`과 빈 문자열(`''`)은 다르다. `NOT NULL`은 빈 문자열을 허용한다.

#### UNIQUE

```sql
CREATE TABLE member (
    email VARCHAR(100) UNIQUE  -- 중복 불가, NULL은 허용
);
```

- 컬럼 내 값이 유일해야 한다.
- `NULL`은 중복 체크에서 제외된다. (NULL은 비교 불가)

#### PRIMARY KEY (기본 키)

```sql
CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT
);
```

- `NOT NULL + UNIQUE`를 합친 것. 행을 유일하게 식별한다.
- 테이블당 하나만 존재한다.
- 복합 기본 키: `PRIMARY KEY (order_id, product_id)`

#### FOREIGN KEY (외래 키)

```sql
CREATE TABLE orders (
    id        BIGINT PRIMARY KEY,
    member_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES member(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

- 다른 테이블의 기본 키를 참조한다. **참조 무결성** 보장.
- `ON DELETE` / `ON UPDATE` 옵션:

| 옵션 | 동작 |
|------|------|
| `CASCADE` | 부모 행 삭제/수정 시 자식 행도 함께 처리 |
| `SET NULL` | 부모 행 삭제 시 자식 컬럼을 NULL로 |
| `RESTRICT` | 자식 행이 있으면 부모 행 삭제 거부 (기본값) |
| `NO ACTION` | `RESTRICT`와 유사 |

<div class="warning-box" markdown="1">

**주의**: 외래 키 제약은 강력하지만 **대규모 서비스에서는 성능·운영 부담**으로 인해 DB 외래 키를 사용하지 않고 애플리케이션 레벨에서 참조 무결성을 관리하는 경우도 많다. (트레이드오프 판단 필요)

</div>

#### DEFAULT

```sql
CREATE TABLE member (
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active  BOOLEAN  DEFAULT TRUE
);
```

- 값을 지정하지 않으면 기본값이 자동으로 들어간다.

#### CHECK

```sql
CREATE TABLE member (
    age INT CHECK (age >= 0 AND age <= 150)
);
```

- 컬럼에 저장되는 값의 범위나 조건을 검사한다.
- MySQL 8.0.16 이상에서 실제 강제 적용 (이전 버전은 문법은 허용되나 무시됨).

### 제약 조건 이름 지정

```sql
-- 이름 없는 제약 조건 (DBMS가 자동 생성)
email VARCHAR(100) UNIQUE

-- 이름 있는 제약 조건 (에러 메시지 명확, 나중에 삭제 용이)
CONSTRAINT uq_member_email UNIQUE (email),
CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member(id)
```

<div class="tip-box" markdown="1">

**팁**: 제약 조건에 이름을 붙이면 위반 시 에러 메시지에 이름이 표시되어 디버깅이 쉽고, `ALTER TABLE ... DROP CONSTRAINT` 로 명시적으로 제거할 수 있다.

</div>