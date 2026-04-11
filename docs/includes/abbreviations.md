<!-- 전역 용어집: 이 파일에 정의된 약어/용어는 모든 페이지에 자동으로 툴팁이 적용됩니다 -->

<!-- ===== DB / SQL ===== -->
*[WAL]: Write-Ahead Log. 데이터를 변경하기 전에 로그를 먼저 기록하는 방식. 장애 발생 시 이 로그로 데이터를 복구한다.
*[ACID]: Atomicity(원자성)·Consistency(일관성)·Isolation(격리성)·Durability(지속성). 트랜잭션이 안전하게 수행되기 위한 4가지 성질.
*[MVCC]: Multi-Version Concurrency Control. 데이터의 여러 버전을 유지해 Lock 없이 읽기 일관성을 보장하는 동시성 제어 방식.
*[DBMS]: DataBase Management System. 데이터베이스를 관리하는 소프트웨어. MySQL, PostgreSQL, Oracle 등.
*[DDL]: Data Definition Language. 테이블·인덱스 등 DB 구조를 정의하는 SQL 분류. CREATE, ALTER, DROP.
*[DML]: Data Manipulation Language. 데이터를 조작하는 SQL 분류. SELECT, INSERT, UPDATE, DELETE.
*[DCL]: Data Control Language. 사용자 접근 권한을 제어하는 SQL 분류. GRANT, REVOKE.
*[TCL]: Transaction Control Language. 트랜잭션을 제어하는 SQL 분류. COMMIT, ROLLBACK, SAVEPOINT.
*[PK]: Primary Key. 테이블에서 각 행을 유일하게 식별하는 기본 키. NOT NULL + UNIQUE.
*[FK]: Foreign Key. 다른 테이블의 PK를 참조하는 외래 키. 참조 무결성을 보장한다.
*[ORM]: Object-Relational Mapping. 객체와 관계형 DB 테이블을 자동으로 매핑해주는 기술. Java에서는 JPA/Hibernate가 대표적.
*[JDBC]: Java DataBase Connectivity. Java에서 DB에 접근하기 위한 표준 API 인터페이스.
*[JPA]: Java Persistence API. Java ORM 표준 명세. 구현체로 Hibernate가 주로 사용된다.
*[HikariCP]: 고성능 JDBC 커넥션 풀 라이브러리. Spring Boot 기본 커넥션 풀로 채택되어 있다.
*[B-Tree]: Balanced Tree. 모든 리프 노드까지 깊이가 동일한 자가 균형 탐색 트리. DB 인덱스의 기본 자료구조.
*[CTE]: Common Table Expression. WITH 절로 정의하는 임시 결과 집합. 복잡한 쿼리를 단계적으로 분리할 때 사용.

<!-- ===== JVM / Java ===== -->
*[JVM]: Java Virtual Machine. Java 바이트코드를 OS에 맞게 실행하는 가상 머신. 플랫폼 독립성을 제공한다.
*[JDK]: Java Development Kit. Java 개발에 필요한 도구 모음. JRE + 컴파일러(javac) + 디버거 포함.
*[JRE]: Java Runtime Environment. Java 프로그램 실행에 필요한 환경. JVM + 표준 라이브러리.
*[GC]: Garbage Collection. JVM이 더 이상 참조되지 않는 객체를 자동으로 메모리에서 해제하는 과정.
*[OOP]: Object-Oriented Programming. 객체 지향 프로그래밍. 캡슐화·상속·다형성·추상화를 핵심 개념으로 한다.
*[STW]: Stop-The-World. GC 실행 중 JVM이 애플리케이션 스레드를 일시 정지시키는 현상. GC 튜닝의 주요 목표.

<!-- ===== Spring ===== -->
*[IoC]: Inversion of Control. 제어의 역전. 객체 생성·관리 권한을 개발자가 아닌 프레임워크(Spring Container)가 가지는 원칙.
*[DI]: Dependency Injection. 의존성 주입. 객체가 직접 의존 객체를 생성하지 않고 외부에서 주입받는 방식.
*[AOP]: Aspect-Oriented Programming. 관점 지향 프로그래밍. 로깅·트랜잭션 등 횡단 관심사를 핵심 로직과 분리해 모듈화한다.
*[MVC]: Model-View-Controller. UI와 비즈니스 로직을 분리하는 아키텍처 패턴. Spring Web MVC의 기반.
*[JWT]: JSON Web Token. 사용자 인증 정보를 JSON 형식으로 인코딩해 서명한 토큰. 서버 세션 없이 인증 상태를 유지할 수 있다.
*[MSA]: MicroService Architecture. 애플리케이션을 독립적으로 배포 가능한 작은 서비스 단위로 분리하는 아키텍처 방식.

<!-- ===== 네트워크 / CS ===== -->
*[API]: Application Programming Interface. 소프트웨어 간 통신을 위한 인터페이스 규약.
*[REST]: Representational State Transfer. HTTP를 기반으로 자원을 URI로 표현하고 메서드로 행위를 나타내는 아키텍처 스타일.
*[HTTP]: HyperText Transfer Protocol. 웹에서 클라이언트-서버 간 데이터를 주고받는 애플리케이션 계층 프로토콜.
*[TCP]: Transmission Control Protocol. 연결 지향, 신뢰성 있는 데이터 전송을 보장하는 전송 계층 프로토콜.
*[UDP]: User Datagram Protocol. 비연결형, 빠르지만 신뢰성을 보장하지 않는 전송 계층 프로토콜.
