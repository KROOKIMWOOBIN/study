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

| 분류 | 이름 | 역할 | 주요 명령어 |
|------|------|------|------------|
| **DDL** | Data Definition Language | 구조(스키마) 정의 | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| **DML** | Data Manipulation Language | 데이터 조작 | `SELECT`, `INSERT`, `UPDATE`, `DELETE` |
| **DCL** | Data Control Language | 권한 제어 | `GRANT`, `REVOKE` |
| **TCL** | Transaction Control Language | 트랜잭션 제어 | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

---

## 어떻게 쓰는지

### 간단한 예시

```sql
-- 1. 테이블 생성 (DDL)
CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 데이터 삽입 (DML)
INSERT INTO member (name, email) VALUES ('김철수', 'kim@example.com');

-- 3. 데이터 조회
SELECT * FROM member WHERE name = '김철수';

-- 4. 데이터 수정
UPDATE member SET email = 'kim2@example.com' WHERE id = 1;

-- 5. 트랜잭션 처리 (TCL)
BEGIN;
DELETE FROM member WHERE id = 1;
COMMIT;
```

---

## 언제 쓰는지

| 상황 | RDBMS | NoSQL |
|------|-------|--------|
| **구조화된 데이터** | ✅ RDBMS | ❌ |
| **관계형 조인** | ✅ RDBMS | ❌ |
| **ACID 보장 필요** | ✅ RDBMS | ❌ |
| **트랜잭션** | ✅ RDBMS | ⚠️ 제한적 |
| **스키마 변경 빈번** | ❌ | ✅ NoSQL |
| **대규모 비정형 데이터** | ❌ | ✅ NoSQL |
| **높은 쓰기 성능** | ⚠️ | ✅ NoSQL |
| **복잡한 쿼리** | ✅ RDBMS | ❌ |

---

## 장점

| 장점 | 설명 |
|------|------|
| **데이터 무결성** | 제약 조건과 트랜잭션으로 일관성 보장 |
| **ACID 보장** | Atomicity, Consistency, Isolation, Durability |
| **복잡한 쿼리** | JOIN, 서브쿼리로 강력한 데이터 검색 |
| **표준 언어** | SQL은 DBMS 간 호환 가능 |
| **정규화** | 데이터 중복 제거로 저장 효율성 |
| **보안** | 권한 관리(DCL)로 접근 제어 |
| **확장성** | 대규모 데이터 처리 최적화 |

---

## 단점

| 단점 | 설명 |
|------|------|
| **스키마 고정** | 구조 변경 시 마이그레이션 비용 |
| **수평 확장 어려움** | 샤딩 없이는 단일 서버 확장성 제한 |
| **높은 쓰기 비용** | 트랜잭션, 정규화로 인한 오버헤드 |
| **복잡한 쿼리** | 성능 튜닝 필요 |
| **유연성 부족** | 형식이 정해진 데이터만 저장 가능 |

---

## 주의할 점

<div class="danger-box" markdown="1">

**❌ 과도한 정규화**

```sql
-- 과도하게 정규화되어 JOIN이 많아지면 오히려 성능 악화 가능
-- 필요하면 역정규화(Denormalization) 고려
```

</div>

<div class="warning-box" markdown="1">

**⚠️ DDL 자동 커밋**

```sql
-- DROP, TRUNCATE, ALTER는 자동 COMMIT됨
-- ROLLBACK 불가능 (MySQL 기준)
-- 실행 전 반드시 백업 후 신중하게 처리
```

</div>

<div class="warning-box" markdown="1">

**⚠️ N+1 쿼리 문제**

```sql
-- ❌ 나쁜 예: 부모 1개 + 자식 N개 쿼리
-- 부모 SELECT (1) → 각 부모 행마다 자식 SELECT (N)
-- 결과: 1 + N번의 쿼리

-- ✅ 좋은 방식: JOIN으로 한 번에 조회
SELECT parent.*, child.* FROM parent
JOIN child ON parent.id = child.parent_id;
```

</div>

---

## 학습 목록

| 주제 | 한 줄 설명 |
|------|-----------|
| [ACID](./기초/ACID.md) | 트랜잭션 4대 성질 — Atomicity, Consistency, Isolation, Durability |
| [SQL](./기초/SQL.md) | SELECT 구조, DDL/DML/DCL/TCL 명령어 분류, 실행 순서 |
| [데이터 타입](./기초/데이터타입.md) | 숫자형, 문자형, 날짜형, JSON, BLOB 비교 |
| [제약 조건](./기초/제약조건.md) | NOT NULL, UNIQUE, PK, FK, DEFAULT, CHECK |
| [데이터 가공](./쿼리/데이터가공.md) | CASE WHEN, COALESCE, 문자열·숫자·날짜 함수, CAST |
| [그룹과 집계](./쿼리/집계.md) | GROUP BY, HAVING, COUNT·SUM·AVG·MAX·MIN |
| [JOIN](./쿼리/JOIN.md) | INNER / OUTER / CROSS JOIN, 실행 원리 |
| [서브쿼리](./쿼리/서브쿼리.md) | 스칼라·인라인 뷰·상관 서브쿼리, IN vs EXISTS |
| [UNION](./쿼리/UNION.md) | 여러 SELECT 결과 합치기, UNION vs UNION ALL, 중복 제거 |
| [CASE](./쿼리/CASE.md) | 조건부 데이터 변환, Searched/Simple CASE, 집계 조합 |
| [인덱스](./성능운영/인덱스.md) | B-Tree 구조, 클러스터/논클러스터, 성능 트레이드오프 |
| [정규화](./기초/정규화.md) | 1NF·2NF·3NF·BCNF, 이상(Anomaly), 역정규화 트레이드오프 |
| [트랜잭션](./성능운영/트랜잭션.md) | BEGIN/COMMIT/ROLLBACK, 전파(Propagation), 동기화 매니저 |
| [격리 수준](./성능운영/격리수준.md) | READ UNCOMMITTED ~ SERIALIZABLE, Dirty Read / Phantom Read |
