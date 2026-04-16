## UNION (합집합)

### 왜 쓰는지

SQL 쿼리는 **한 번에 한 테이블에서만 조회**하는 것이 기본입니다. 하지만 상황에 따라:
- 서로 다른 테이블의 데이터를 합쳐서 보여줘야 함
- 같은 테이블에서 조건에 따라 다르게 조회한 결과를 합쳐야 함
- 여러 조회 결과를 하나의 결과셋으로 표현해야 함

이때 **여러 SELECT의 결과를 세로로 합치는** UNION이 필요합니다.

<div class="concept-box" markdown="1">

**핵심**: UNION은 **두 개 이상의 SELECT 결과 행들을 합쳐서 하나의 결과셋으로 반환**합니다. 중복을 제거하거나 유지할 수 있습니다.

</div>

### 어떻게 쓰는지

#### UNION 기본 문법

```sql
SELECT col1, col2 FROM table1
UNION
SELECT col1, col2 FROM table2;
```

#### UNION vs UNION ALL

```sql
-- 1️⃣ UNION: 중복 제거
SELECT name, salary FROM employees WHERE dept_id = 10
UNION
SELECT name, salary FROM employees WHERE dept_id = 20;
-- 결과: 중복된 행이 있으면 1개만 유지 (정렬 후 비교)

-- 2️⃣ UNION ALL: 중복 유지 (더 빠름)
SELECT name, salary FROM employees WHERE dept_id = 10
UNION ALL
SELECT name, salary FROM employees WHERE dept_id = 20;
-- 결과: 모든 행을 그대로 반환 (정렬 없음)
```

#### 실제 사용 사례

```sql
-- 직원과 고객을 합쳐서 모든 사람 이름 조회
SELECT emp_name AS name, 'Employee' AS type FROM employees
UNION
SELECT cust_name AS name, 'Customer' AS type FROM customers;

-- 결과:
-- name      | type
-- ---------------------
-- Alice     | Employee
-- Bob       | Customer
-- Carol     | Employee
-- Dave      | Customer
```

#### 여러 테이블 합치기

```sql
-- 3개 이상도 가능
SELECT id, name, 'Sales' AS dept FROM sales_staff
UNION
SELECT id, name, 'Marketing' AS dept FROM marketing_staff
UNION
SELECT id, name, 'Engineering' AS dept FROM engineering_staff;
```

#### 정렬과 LIMIT

```sql
-- UNION 전체 결과를 정렬하려면 마지막에 ORDER BY
SELECT name FROM employees WHERE dept_id = 10
UNION
SELECT name FROM employees WHERE dept_id = 20
ORDER BY name;

-- LIMIT도 마지막에 적용
SELECT name FROM employees WHERE dept_id = 10
UNION ALL
SELECT name FROM employees WHERE dept_id = 20
LIMIT 10;
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **여러 테이블 데이터 합치기** | ✅ UNION | 테이블 구조 유사할 때 |
| **조건부 조회 결과 합치기** | ✅ UNION | OR로 처리 곤란할 때 |
| **중복 제거 필요** | ✅ UNION | 정확한 결과 필요 |
| **중복 상관없음** | ✅ UNION ALL | 성능 중시 |
| **단순 합치기** | ✅ UNION ALL | 데이터 특성상 중복 없을 때 |
| **좌우 합치기** | ❌ JOIN | 행을 가로로 합칠 때 |

### 장점

| 장점 | 설명 |
|------|------|
| **간결성** | OR로 복잡한 WHERE를 피할 수 있음 |
| **가독성** | 각 조회가 의도가 명확 |
| **유연성** | 서로 다른 테이블 합치기 가능 |
| **데이터 정제** | UNION으로 중복 자동 제거 |

### 단점

| 단점 | 설명 |
|------|------|
| **성능 비용** | UNION은 정렬 작업으로 느림 (UNION ALL은 빠름) |
| **메모리 사용** | 결과를 메모리에 모아야 함 |
| **컬럼 개수 일치** | SELECT 컬럼 수가 같아야 함 |
| **타입 일치 필요** | 같은 위치의 컬럼 타입이 호환되어야 함 |

### 특징

#### 1. 컬럼 일치 규칙

```sql
-- ❌ 잘못된 예: 컬럼 개수 다름
SELECT id, name FROM employees
UNION
SELECT id FROM customers;  -- 컬럼 2개 vs 1개 → 오류

-- ✅ 올바른 예: 같은 개수
SELECT id, name FROM employees
UNION
SELECT cust_id, cust_name FROM customers;  -- 모두 2개
```

#### 2. 컬럼 이름은 첫 번째 SELECT에서 정해짐

```sql
SELECT emp_id AS id, emp_name AS name FROM employees
UNION
SELECT cust_id, cust_name FROM customers;

-- 결과 컬럼명: id, name (첫 SELECT 기준)
```

#### 3. UNION의 성능 원리

```text
UNION 실행 과정:

1️⃣ 첫 번째 SELECT 실행 → 결과 로드
   id | name
   ---------
    1 | Alice
    2 | Bob

2️⃣ 두 번째 SELECT 실행 → 결과 로드
   id | name
   ---------
    2 | Bob     (중복)
    3 | Carol

3️⃣ 정렬 후 중복 제거
   정렬: Alice(1), Bob(2), Bob(2), Carol(3)
   중복제거: Alice(1), Bob(2), Carol(3)

4️⃣ 최종 반환 → 3행
```

UNION ALL은 3️⃣ 단계 스킵 → 더 빠름

#### 4. 타입 호환성

```sql
-- ✅ 정수 ↔ 정수: OK
SELECT emp_id FROM employees
UNION
SELECT cust_id FROM customers;

-- ✅ VARCHAR ↔ VARCHAR: OK
SELECT emp_name FROM employees
UNION
SELECT cust_name FROM customers;

-- ⚠️ 정수 ↔ VARCHAR: 암묵적 형변환 (위험)
SELECT emp_id FROM employees        -- INT
UNION
SELECT emp_name FROM employees;     -- VARCHAR → 문자로 변환됨

-- ✅ 명시적 형변환: 안전
SELECT CAST(emp_id AS VARCHAR) FROM employees
UNION
SELECT emp_name FROM employees;
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ UNION 남용으로 성능 저하**

```sql
-- ❌ 나쁜 예: 10개 테이블을 모두 UNION
SELECT id FROM table1
UNION
SELECT id FROM table2
UNION
SELECT id FROM table3
UNION
... (10개)
-- 각 테이블 조회 + 정렬 + 중복 제거 반복 → 매우 느림

-- ✅ 올바른 방식: OR 조건 또는 UNION ALL 사용
SELECT id FROM table1
WHERE dept_id IN (10, 20, 30)  -- 필요하면 OR 사용
UNION ALL
SELECT id FROM table2
WHERE status = 'active';
```

</div>

<div class="warning-box" markdown="1">

**⚠️ UNION vs UNION ALL 선택 실수**

```sql
-- ❌ 불필요하게 UNION 사용 (중복 없는데 정렬 비용 발생)
SELECT name FROM employees WHERE dept_id = 10
UNION
SELECT name FROM employees WHERE dept_id = 20;
-- 각 부서가 겹치지 않으므로 중복 불가능하지만 UNION 사용 → 비효율

-- ✅ 정렬 불필요하면 UNION ALL 사용
SELECT name FROM employees WHERE dept_id = 10
UNION ALL
SELECT name FROM employees WHERE dept_id = 20;
-- 중복이 없으므로 정렬 비용 절약
```

</div>

<div class="warning-box" markdown="1">

**⚠️ NULL 처리 주의**

```sql
-- UNION은 NULL을 같은 값으로 취급
SELECT name, phone FROM employees
UNION
SELECT name, phone FROM customers;

-- 만약 phone이 NULL인 행이 여러 개면 1개로 통합됨
-- (NULL == NULL 취급)
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 대소문자 민감도**

```sql
-- UNION은 중복 비교 시 대소문자 구분
SELECT name FROM employees WHERE name = 'alice'
UNION
SELECT name FROM customers WHERE name = 'Alice';

-- 결과: 2행 (alice, Alice 둘 다)
-- MySQL의 기본 Collation(utf8mb4_unicode_ci)에서는 같음으로 판단
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **UNION** | 중복 제거, 정렬 비용 발생 |
| **UNION ALL** | 중복 유지, 더 빠름 |
| **사용처** | 여러 SELECT 결과를 세로로 합치기 |
| **성능** | 가능하면 UNION ALL 사용 |
| **주의** | 컬럼 개수·타입 일치 필수 |

---

**관련 파일:**
- [JOIN](JOIN.md) — 가로로 데이터 합치기
- [서브쿼리](서브쿼리.md) — 중첩된 SELECT 쿼리
- [데이터 가공](데이터가공.md) — CASE, COALESCE 등 데이터 변환
