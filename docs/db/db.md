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

## 학습 목록

| 주제 | 한 줄 설명 |
|------|-----------|
| [ACID](./ACID.md) | 트랜잭션 4대 성질 — Atomicity, Consistency, Isolation, Durability |
| [SQL](./SQL.md) | SELECT 구조, DDL/DML/DCL/TCL 명령어 분류, 실행 순서 |
| [데이터 타입](./데이터타입.md) | 숫자형, 문자형, 날짜형, JSON, BLOB 비교 |
| [제약 조건](./제약조건.md) | NOT NULL, UNIQUE, PK, FK, DEFAULT, CHECK |
| [데이터 가공](./데이터가공.md) | CASE WHEN, COALESCE, 문자열·숫자·날짜 함수, CAST |
| [그룹과 집계](./집계.md) | GROUP BY, HAVING, COUNT·SUM·AVG·MAX·MIN |
| [JOIN](./JOIN.md) | INNER / OUTER / CROSS JOIN, 실행 원리 |
| [서브쿼리](./서브쿼리.md) | 스칼라·인라인 뷰·상관 서브쿼리, IN vs EXISTS |
| [인덱스](./인덱스.md) | B-Tree 구조, 클러스터/논클러스터, 성능 트레이드오프 |
| [정규화](./정규화.md) | 1NF·2NF·3NF·BCNF, 이상(Anomaly), 역정규화 트레이드오프 |
| [트랜잭션](./트랜잭션.md) | BEGIN/COMMIT/ROLLBACK, 전파(Propagation), 동기화 매니저 |
| [격리 수준](./격리수준.md) | READ UNCOMMITTED ~ SERIALIZABLE, Dirty Read / Phantom Read |
