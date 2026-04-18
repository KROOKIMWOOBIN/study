## VIEW (가상 테이블)

### 왜 쓰는지

<div class="concept-box" markdown="1">

**VIEW**: 실제 데이터를 저장하지 않는 ==가상 테이블==. `SELECT` 쿼리를 이름 붙여 저장해두고, 테이블처럼 조회·재사용한다.

</div>

| 문제 | VIEW가 해결하는 방법 |
|------|---------------------|
| **복잡한 쿼리 반복** | JOIN·서브쿼리를 매번 작성 → VIEW 이름 하나로 재사용 |
| **민감 데이터 노출** | 테이블 전체 대신 필요한 컬럼만 노출 (보안) |
| **쿼리 로직 분산** | 비즈니스 로직이 애플리케이션·쿼리에 중복 → VIEW로 중앙화 |

### 어떻게 쓰는지

#### 기본 생성 / 수정 / 삭제

```sql
-- 1️⃣ VIEW 생성
CREATE VIEW view_name AS
SELECT col1, col2
FROM table_name
WHERE 조건;

-- 2️⃣ VIEW 수정 (기존 VIEW를 교체)
CREATE OR REPLACE VIEW view_name AS
SELECT col1, col2, col3
FROM table_name;

-- 3️⃣ VIEW 삭제
DROP VIEW view_name;

-- 4️⃣ VIEW 목록 확인
SHOW FULL TABLES WHERE TABLE_TYPE = 'VIEW';

-- 5️⃣ VIEW 정의 확인
SHOW CREATE VIEW view_name;
```

#### JOIN 재사용 VIEW

```sql
-- 매번 작성하던 복잡한 JOIN
CREATE VIEW order_summary AS
SELECT
    o.id         AS order_id,
    m.name       AS member_name,
    o.total      AS total_amount,
    o.created_at AS ordered_at
FROM orders o
JOIN members m ON o.member_id = m.id;

-- 이후 단순 조회로 재사용
SELECT * FROM order_summary WHERE total_amount > 100000;
```

#### 보안 VIEW (컬럼·행 제한)

```sql
-- 민감 컬럼(salary, ssn) 제외하고 노출
CREATE VIEW public_employee AS
SELECT id, name, dept_id, hire_date
FROM employees;

-- 특정 부서 행만 노출
CREATE VIEW dept10_employee AS
SELECT id, name, salary
FROM employees
WHERE dept_id = 10;
```

#### 통계·리포트 VIEW

```sql
-- 부서별 평균 급여 VIEW
CREATE VIEW dept_salary_summary AS
SELECT
    dept_id,
    COUNT(*)        AS headcount,
    AVG(salary)     AS avg_salary,
    MAX(salary)     AS max_salary
FROM employees
GROUP BY dept_id;

SELECT * FROM dept_salary_summary ORDER BY avg_salary DESC;
```

#### Updatable VIEW (DML 가능)

```sql
-- 단순 구조면 INSERT / UPDATE / DELETE 가능
CREATE VIEW active_members AS
SELECT id, name, email
FROM members
WHERE status = 'ACTIVE';

-- VIEW를 통한 UPDATE (원본 테이블에 반영됨)
UPDATE active_members SET email = 'new@example.com' WHERE id = 1;
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **복잡한 JOIN을 자주 재사용** | VIEW | 쿼리 중복 제거 |
| **민감 컬럼 은닉** | VIEW | 테이블 직접 접근 차단 |
| **리포트·통계 쿼리 고정** | VIEW | 로직 중앙화 |
| **단순 SELECT 재사용** | VIEW | 코드 가독성 향상 |
| **대용량 집계 반복 조회** | Materialized View (또는 캐시) | VIEW는 매번 실행 → 성능 부족 |
| **쿼리가 단발성** | 직접 쿼리 | VIEW 생성 오버헤드 불필요 |

### 장점

| 장점 | 설명 |
|------|------|
| **재사용성** | 복잡한 쿼리를 이름으로 추상화 |
| **보안** | 컬럼·행 단위 접근 제어 가능 |
| **가독성** | 복잡한 쿼리를 단순한 이름으로 대체 |
| **유지보수** | 쿼리 로직 변경 시 VIEW만 수정하면 전체 반영 |
| **논리적 독립성** | 테이블 구조 변경 시 VIEW만 수정하면 앱 코드 영향 최소화 |

### 단점

| 단점 | 설명 |
|------|------|
| **성능 오버헤드** | 매 조회마다 내부 쿼리 실행 — 캐싱 없음 |
| **옵티마이저 제한** | 복잡한 VIEW는 최적화 어려움 (TEMPTABLE 알고리즘 시) |
| **DML 제한** | 집계·DISTINCT·GROUP BY 포함 VIEW는 INSERT/UPDATE 불가 |
| **디버깅 어려움** | VIEW 위에 VIEW 중첩 시 실행 경로 파악 어려움 |
| **Materialized View 미지원** | MySQL은 기본적으로 결과를 물리 저장하지 않음 |

### 특징

#### 1. ALGORITHM 옵션 (MySQL)

```sql
-- MERGE: VIEW 쿼리를 외부 쿼리에 합쳐 단일 쿼리로 실행 (기본값, 권장)
CREATE ALGORITHM = MERGE VIEW v_simple AS
SELECT id, name FROM employees WHERE dept_id = 10;

-- TEMPTABLE: 내부적으로 임시 테이블 생성 후 결과 반환
-- → GROUP BY, DISTINCT, 집계 함수 포함 VIEW는 자동으로 TEMPTABLE
CREATE ALGORITHM = TEMPTABLE VIEW v_summary AS
SELECT dept_id, COUNT(*) AS cnt FROM employees GROUP BY dept_id;
```

| 알고리즘 | 동작 방식 | 사용 시점 |
|----------|---------|---------|
| **MERGE** | VIEW 쿼리 + 외부 쿼리를 병합해 실행 | 단순 SELECT·JOIN, 인덱스 활용 가능 |
| **TEMPTABLE** | 임시 테이블 생성 후 외부 쿼리 적용 | GROUP BY·DISTINCT 포함 VIEW, Updatable 불가 |

#### 2. Updatable VIEW 조건

DML(INSERT/UPDATE/DELETE)이 가능하려면 아래 조건을 **모두** 만족해야 한다.

```sql
-- ✅ Updatable — 단일 테이블, 조건만 있음
CREATE VIEW v_active AS
SELECT id, name, email FROM members WHERE status = 'ACTIVE';

-- ❌ Not Updatable — GROUP BY 포함
CREATE VIEW v_count AS
SELECT dept_id, COUNT(*) AS cnt FROM employees GROUP BY dept_id;

-- ❌ Not Updatable — DISTINCT 포함
CREATE VIEW v_distinct AS
SELECT DISTINCT dept_id FROM employees;
```

Updatable VIEW 불가 조건: `GROUP BY`, `HAVING`, `DISTINCT`, `UNION`, 집계 함수, 서브쿼리(SELECT 절), `JOIN`(일부 제한)

#### 3. 일반 VIEW vs Materialized View

| 구분 | 일반 VIEW | Materialized View |
|------|-----------|------------------|
| **데이터 저장** | 저장 안 함 — 매번 쿼리 실행 | 결과를 물리 테이블에 저장 |
| **성능** | 대용량 집계 시 느림 | 미리 계산 → 빠름 |
| **최신성** | 항상 최신 | 갱신 주기만큼 지연 |
| **MySQL 지원** | 지원 | 기본 미지원 (스케줄러·테이블로 직접 구현) |
| **PostgreSQL 지원** | 지원 | `MATERIALIZED VIEW` 키워드로 지원 |

### 주의할 점

<div class="danger-box" markdown="1">

**❌ VIEW 위에 VIEW 중첩**

```sql
-- ❌ VIEW → VIEW → VIEW 체인
CREATE VIEW v1 AS SELECT * FROM employees WHERE dept_id = 10;
CREATE VIEW v2 AS SELECT * FROM v1 WHERE salary > 50000;
CREATE VIEW v3 AS SELECT name FROM v2;  -- 추적·디버깅 어려움

-- ✅ 필요하다면 하나의 VIEW로 합치거나 직접 쿼리 사용
CREATE VIEW v_final AS
SELECT name FROM employees
WHERE dept_id = 10 AND salary > 50000;
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 복잡한 집계 VIEW 성능 문제**

`GROUP BY`, `COUNT`, `SUM` 등이 포함된 VIEW는 TEMPTABLE 알고리즘으로 동작해 대용량에서 느려진다.

```sql
-- ❌ 대용량 테이블에 매번 전체 집계
CREATE VIEW v_daily_summary AS
SELECT DATE(created_at) AS day, COUNT(*) AS cnt
FROM orders
GROUP BY DATE(created_at);

-- ✅ 대안: 배치로 집계 테이블 유지 또는 인덱스 최적화
```

</div>

<div class="warning-box" markdown="1">

**⚠️ Updatable VIEW 조건 미확인**

```sql
-- ❌ 집계 VIEW에 UPDATE 시도 → 에러
UPDATE v_dept_summary SET headcount = 5 WHERE dept_id = 10;
-- ERROR 1288 (HY000): The target table v_dept_summary of the UPDATE is not updatable

-- ✅ DML 전에 SHOW CREATE VIEW로 구조 확인
SHOW CREATE VIEW v_dept_summary;
```

</div>

<div class="warning-box" markdown="1">

**⚠️ WITH CHECK OPTION 누락**

```sql
-- ❌ CHECK OPTION 없으면 VIEW 조건 밖 데이터 INSERT 가능
CREATE VIEW v_active AS
SELECT id, name, status FROM members WHERE status = 'ACTIVE';

INSERT INTO v_active VALUES (99, 'Kim', 'INACTIVE');  -- 삽입됨 (조건 무시)

-- ✅ WITH CHECK OPTION으로 VIEW 조건 강제
CREATE VIEW v_active AS
SELECT id, name, status FROM members WHERE status = 'ACTIVE'
WITH CHECK OPTION;

-- INSERT INTO v_active VALUES (99, 'Kim', 'INACTIVE');
-- ERROR: CHECK OPTION failed
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **생성** | `CREATE VIEW name AS SELECT ...` |
| **수정** | `CREATE OR REPLACE VIEW` |
| **삭제** | `DROP VIEW name` |
| **ALGORITHM** | MERGE (기본·권장) / TEMPTABLE (집계 시 자동) |
| **Updatable 조건** | 단일 테이블, GROUP BY·DISTINCT·집계 없음 |
| **WITH CHECK OPTION** | VIEW 조건 벗어난 DML 차단 |
| **성능 주의** | 집계 VIEW 대용량 → 느림, Materialized View 고려 |

---

**관련 파일:**

- [JOIN](JOIN.md) — VIEW에서 자주 활용되는 테이블 결합
- [서브쿼리](서브쿼리.md) — VIEW 내부에서 사용 가능한 서브쿼리
- [인덱스](../성능운영/인덱스.md) — MERGE VIEW에서 인덱스 활용 방법
