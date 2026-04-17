# DB (Database)

데이터베이스 핵심 개념 정리.

---

## 학습 목록

| 주제 | 한 줄 설명 |
|------|-----------|
| [데이터베이스](./기초/데이터베이스.md) | 데이터베이스 개념, DBMS, DDL/DML/DCL/TCL 분류 |
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
