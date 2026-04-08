# DB (Database)

데이터베이스 핵심 개념 정리.

---

| 주제 | 한 줄 설명 |
| --- | --- |
| [JDBC](./JDBC.md) | Java DB 표준 인터페이스 — Connection, Statement, ResultSet |
| [커넥션 풀 & DataSource](./커넥션풀-DataSource.md) | HikariCP, 커넥션 재사용, DataSource 추상화 |
| [트랜잭션](./트랜잭션.md) | ACID, @Transactional, 전파(Propagation), 동기화 매니저 |
| [트랜잭션 격리 수준](./격리수준.md) | READ UNCOMMITTED ~ SERIALIZABLE, Dirty Read / Phantom Read |
| [예외 처리](./예외처리.md) | SQLException 문제, DataAccessException 계층, 예외 변환기 |


1. 데이터베이스란?
    - 데이터 집합소
    - 정보(가공되어 사용자에게 정보를 알려주는 데이터)
    - 데이터(조각)
2. 데이터베이스를 쓰는 이유
    - 보안
    - 동시성
    - 데이터 회복 & 백업
    - ACID

