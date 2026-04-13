# SQL

<div class="concept-box" markdown="1">

**SQL(Structured Query Language)**: 관계형 데이터베이스와 통신하기 위한 표준 언어. 데이터를 정의·조작·제어하는 명령어 집합이다.

</div>

## 왜 쓰는가?

DBMS마다 내부 구현이 다르지만, SQL이라는 표준 언어 덕분에 **MySQL, PostgreSQL, Oracle 등 어떤 RDBMS든 같은 방식으로 데이터를 다룰 수 있다.** 개발자가 DBMS 내부 파일 구조를 몰라도 선언적(무엇을 원하는지)으로 데이터를 요청할 수 있다.

## 특징

- **선언형 언어**: "어떻게"가 아니라 "무엇을" 원하는지 기술한다. 실행 계획은 DBMS의 쿼리 옵티마이저가 결정한다.
- **집합 기반**: 행 하나가 아니라 결과 집합(테이블) 단위로 동작한다.
- **ANSI/ISO 표준**: 표준이 있지만 DBMS마다 방언(Dialect)이 존재한다. (예: MySQL의 `LIMIT` vs Oracle의 `ROWNUM`)

## SELECT 기본 구조

```sql
SELECT   컬럼1, 컬럼2          -- 3. 조회할 컬럼 선택
FROM     테이블명               -- 1. 대상 테이블
WHERE    조건                   -- 2. 행 필터링
GROUP BY 그룹 기준 컬럼         -- 4. 그룹화
HAVING   그룹 조건              -- 5. 그룹 필터링
ORDER BY 정렬 기준 컬럼         -- 6. 정렬
LIMIT    n;                     -- 7. 행 수 제한
```

---

## SQL 실행 순서

SQL은 **작성 순서**와 **실제 실행 순서**가 다르다. 옵티마이저가 아래 순서로 처리한다.

| 실행 순서 | 절 | 역할 |
|----------|----|------|
| 1 | `FROM` | 대상 테이블 결정 (JOIN 포함) |
| 2 | `WHERE` | 행 단위 필터링 (집계 전) |
| 3 | `GROUP BY` | 지정 컬럼 기준으로 그룹화 |
| 4 | `HAVING` | 그룹 단위 필터링 (집계 후) |
| 5 | `SELECT` | 출력할 컬럼·별칭 확정 |
| 6 | `ORDER BY` | 정렬 |
| 7 | `LIMIT` | 행 수 제한 |

<div class="warning-box" markdown="1">

**SELECT 별칭(alias)은 WHERE·HAVING에서 쓸 수 없다** — `SELECT price * 0.9 AS 할인가`로 정의한 별칭은 `WHERE 할인가 > 1000` 에서 사용 불가. SELECT(5번)가 WHERE(2번)보다 늦게 실행되기 때문이다.

```sql
-- 잘못된 예 (MySQL에서 오류)
SELECT price * 0.9 AS 할인가 FROM product WHERE 할인가 > 1000; -- ❌

-- 올바른 예
SELECT price * 0.9 AS 할인가 FROM product WHERE price * 0.9 > 1000; -- ✅
```

단, `ORDER BY`는 예외적으로 SELECT 별칭 사용 가능 (MySQL 기준).

</div>

<div class="tip-box" markdown="1">

**성능 포인트**: `WHERE`이 `GROUP BY` 전에 실행되므로, 집계 전에 최대한 많은 행을 걸러낼수록 빠르다. `HAVING` 대신 `WHERE`로 필터링 가능한 조건은 `WHERE`에 두는 것이 좋다.

</div>

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
