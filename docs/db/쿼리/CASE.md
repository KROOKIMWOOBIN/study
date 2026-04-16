## CASE (조건부 데이터 변환)

### 왜 쓰는지

데이터를 조회할 때 **조건에 따라 다른 값으로 표현**해야 합니다:
- 숫자 코드를 텍스트로 변환 (1 → "활성", 0 → "비활성")
- 나이대별 분류 (0~20 → "청소년", 20~65 → "성인", 65~ → "노년")
- 급여등급 (50000 이상 → "고급", 30000~50000 → "중급", 이하 → "초급")
- NULL을 다른 값으로 변환

WHERE 조건처럼 **SELECT 결과를 조건부로 변환**할 때 CASE를 사용합니다.

<div class="concept-box" markdown="1">

**핵심**: CASE는 **SQL에서 if-else 문법처럼 동작**하여, 조건에 따라 다른 값을 반환하는 표현식입니다.

</div>

### 어떻게 쓰는지

#### Searched CASE (권장)

```sql
-- 문법: CASE WHEN 조건 THEN 값 ... ELSE 기본값 END
SELECT 
    name,
    salary,
    CASE 
        WHEN salary >= 60000 THEN '고급'
        WHEN salary >= 40000 THEN '중급'
        ELSE '초급'
    END AS grade
FROM employees;

-- 결과:
-- name  | salary | grade
-- -------|--------|-------
-- Alice | 50000  | 중급
-- Bob   | 45000  | 중급
-- Carol | 70000  | 고급
```

#### Simple CASE (축약형)

```sql
-- 특정 컬럼 값을 비교할 때 간결함
SELECT 
    emp_id,
    dept_id,
    CASE dept_id
        WHEN 10 THEN '영업'
        WHEN 20 THEN '개발'
        WHEN 30 THEN '인사'
        ELSE '기타'
    END AS dept_name
FROM employees;

-- Searched CASE로 동등하게 표현:
CASE 
    WHEN dept_id = 10 THEN '영업'
    WHEN dept_id = 20 THEN '개발'
    WHEN dept_id = 30 THEN '인사'
    ELSE '기타'
END
```

#### 복잡한 조건

```sql
-- 여러 조건 조합
SELECT 
    name,
    age,
    salary,
    CASE 
        WHEN age < 20 AND salary < 30000 THEN '신입저급'
        WHEN age < 20 THEN '신입'
        WHEN salary >= 60000 THEN '경력고급'
        WHEN salary >= 40000 THEN '경력중급'
        ELSE '저급'
    END AS category
FROM employees;
```

#### NULL 처리

```sql
-- 1️⃣ CASE로 NULL 처리
SELECT 
    name,
    phone,
    CASE 
        WHEN phone IS NULL THEN '미등록'
        ELSE phone
    END AS phone_display
FROM employees;

-- 2️⃣ COALESCE (NULL 처리 전용)
SELECT 
    name,
    COALESCE(phone, '미등록') AS phone_display
FROM employees;
```

#### COUNT와 CASE 조합

```sql
-- 조건별 집계
SELECT 
    COUNT(*) AS total,
    COUNT(CASE WHEN dept_id = 10 THEN 1 END) AS sales_count,
    COUNT(CASE WHEN dept_id = 20 THEN 1 END) AS dev_count,
    COUNT(CASE WHEN salary >= 50000 THEN 1 END) AS high_salary
FROM employees;

-- 결과:
-- total | sales_count | dev_count | high_salary
-- ------|-------------|-----------|-------------
--  100  |      30     |      40   |      35
```

#### SUM/AVG와 CASE 조합

```sql
-- 부서별 평균 급여
SELECT 
    SUM(CASE WHEN dept_id = 10 THEN salary ELSE 0 END) AS sales_total,
    SUM(CASE WHEN dept_id = 20 THEN salary ELSE 0 END) AS dev_total,
    AVG(CASE WHEN dept_id = 10 THEN salary END) AS sales_avg
FROM employees;
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **코드를 텍스트로 변환** | ✅ CASE | 가독성 향상 |
| **범위별 분류** | ✅ CASE | WHERE로 불가능 |
| **조건별 집계** | ✅ CASE + COUNT | 행을 그룹으로 변환 |
| **NULL 변환** | ✅ COALESCE | 더 간단 |
| **단순 NULL 처리** | ✅ IFNULL/COALESCE | 더 빠름 |
| **WHERE 조건** | ❌ CASE | WHERE에서는 직접 조건 사용 |

### 장점

| 장점 | 설명 |
|------|------|
| **가독성** | 조건과 값이 명확하게 매핑됨 |
| **유연성** | 복잡한 조건 표현 가능 |
| **성능** | 데이터 추출 후 애플리케이션에서 변환하는 것보다 빠름 |
| **일관성** | DB 레벨에서 일괄 변환 |
| **집계 활용** | COUNT/SUM/AVG와 조합으로 강력한 분석 |

### 단점

| 단점 | 설명 |
|------|------|
| **복잡한 쿼리** | CASE가 많으면 SQL이 길어짐 |
| **타입 일치** | THEN 값들의 타입이 호환되어야 함 |
| **성능 비용** | 각 WHEN 조건 평가 비용 발생 |
| **유지보수** | 분류 기준이 자주 바뀌면 쿼리 수정 필요 |
| **가독성 한계** | 조건이 10개 이상이면 VIEW로 분리 권장 |

### 특징

#### 1. CASE는 표현식 (SELECT, WHERE 모두 사용 가능)

```sql
-- 1️⃣ SELECT에서 사용
SELECT 
    name,
    CASE WHEN salary > 50000 THEN 'High' ELSE 'Low' END
FROM employees;

-- 2️⃣ WHERE에서 사용
SELECT name, salary FROM employees
WHERE CASE 
    WHEN dept_id = 10 THEN salary > 45000
    WHEN dept_id = 20 THEN salary > 55000
    ELSE salary > 40000
END;

-- 3️⃣ GROUP BY에 사용
SELECT 
    CASE WHEN age < 30 THEN 'Young' ELSE 'Old' END AS age_group,
    COUNT(*) AS cnt
FROM employees
GROUP BY CASE WHEN age < 30 THEN 'Young' ELSE 'Old' END;

-- 4️⃣ ORDER BY에 사용
SELECT name, salary FROM employees
ORDER BY CASE WHEN dept_id = 10 THEN 1 ELSE 2 END;
```

#### 2. THEN 값 타입이 통일되어야 함

```sql
-- ❌ 잘못된 예: INT와 VARCHAR 혼합
SELECT CASE 
    WHEN salary > 50000 THEN 1
    WHEN salary > 40000 THEN '중급'  -- 타입 불일치
    ELSE salary                      -- 다시 INT
END;

-- ✅ 올바른 예: 모두 VARCHAR로
SELECT CASE 
    WHEN salary > 50000 THEN '고급'
    WHEN salary > 40000 THEN '중급'
    ELSE '초급'
END;

-- ✅ 명시적 형변환
SELECT CASE 
    WHEN salary > 50000 THEN CAST(1 AS CHAR)
    WHEN salary > 40000 THEN '중급'
    ELSE '초급'
END;
```

#### 3. ELSE는 선택사항 (생략 시 NULL)

```sql
-- ELSE 없으면 조건 모두 false일 때 NULL 반환
SELECT CASE 
    WHEN salary > 60000 THEN '고급'
    WHEN salary > 40000 THEN '중급'
    -- ELSE 없음 → salary <= 40000이면 NULL
END AS grade;

-- ✅ 안전하게: 항상 ELSE 포함
SELECT CASE 
    WHEN salary > 60000 THEN '고급'
    WHEN salary > 40000 THEN '중급'
    ELSE '초급'
END AS grade;
```

#### 4. CASE 실행 순서

```sql
-- WHEN 조건을 위에서부터 순서대로 평가 → 첫 TRUE에서 멈춤
SELECT CASE 
    WHEN salary > 40000 THEN 'High'        -- 1️⃣ 먼저 평가
    WHEN salary > 50000 THEN 'VeryHigh'    -- 2️⃣ 위가 TRUE면 평가 안됨
    ELSE 'Low'                             -- 3️⃣ 모두 FALSE면 ELSE
END;

-- salary = 60000인 경우:
-- 60000 > 40000 = TRUE → 'High' 반환 (두 번째 조건은 평가 안함)
```

#### 5. CASE vs IF 함수

```sql
-- MySQL의 IF 함수 (간단한 경우만 사용)
SELECT 
    name,
    IF(salary > 50000, '고급', '저급') AS grade
FROM employees;

-- CASE (권장, 표준 SQL)
SELECT 
    name,
    CASE WHEN salary > 50000 THEN '고급' ELSE '저급' END AS grade
FROM employees;

-- 3개 이상 조건은 CASE 필수
SELECT 
    name,
    CASE 
        WHEN salary >= 60000 THEN '고급'
        WHEN salary >= 40000 THEN '중급'
        ELSE '초급'
    END AS grade
FROM employees;
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ CASE를 WHERE 조건으로 사용하기**

```sql
-- ❌ 비효율: 전체 행을 조회 후 필터링
SELECT * FROM employees
WHERE CASE 
    WHEN dept_id = 10 THEN salary > 45000
    WHEN dept_id = 20 THEN salary > 55000
    ELSE salary > 40000
END = TRUE;

-- ✅ 올바른 방식: OR 조건 또는 IN
SELECT * FROM employees
WHERE 
    (dept_id = 10 AND salary > 45000) OR
    (dept_id = 20 AND salary > 55000) OR
    (dept_id NOT IN (10, 20) AND salary > 40000);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ CASE 순서 실수**

```sql
-- ❌ 넓은 조건이 먼저 오면 뒤의 조건은 평가 안됨
SELECT CASE 
    WHEN salary > 40000 THEN '상위 50%'
    WHEN salary > 50000 THEN '상위 30%'  -- 절대 도달 불가
    ELSE '하위'
END;

-- ✅ 좁은 조건부터 시작
SELECT CASE 
    WHEN salary > 60000 THEN '상위 10%'
    WHEN salary > 50000 THEN '상위 20%'
    WHEN salary > 40000 THEN '상위 50%'
    ELSE '하위'
END;
```

</div>

<div class="warning-box" markdown="1">

**⚠️ NULL 비교 주의**

```sql
-- ❌ 이렇게는 NULL을 못 잡음
SELECT CASE 
    WHEN phone = NULL THEN '미등록'  -- NULL = NULL은 항상 FALSE
    ELSE phone
END;

-- ✅ IS NULL 사용
SELECT CASE 
    WHEN phone IS NULL THEN '미등록'
    ELSE phone
END;

-- ✅ 또는 COALESCE (더 간단)
SELECT COALESCE(phone, '미등록');
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 과도한 CASE 사용**

```sql
-- ❌ CASE가 너무 많으면 VIEW로 분리
SELECT 
    name,
    CASE ... END AS col1,
    CASE ... END AS col2,
    CASE ... END AS col3,
    CASE ... END AS col4,
    CASE ... END AS col5
FROM employees;

-- ✅ VIEW로 분리
CREATE VIEW employee_classified AS
SELECT 
    name,
    CASE ... END AS grade,
    CASE ... END AS level
FROM employees;

SELECT * FROM employee_classified;
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **Searched CASE** | 일반적인 방식, 다양한 조건 가능 |
| **Simple CASE** | 한 컬럼 값으로 분기할 때 축약 |
| **집계와 조합** | COUNT/SUM/AVG로 강력한 분석 |
| **순서 중요** | 첫 TRUE에서 멈춤, 포함범위 주의 |
| **NULL 처리** | IS NULL 필수, COALESCE 고려 |
| **WHERE vs CASE** | WHERE는 직접 조건, CASE는 변환용 |

---

**관련 파일:**
- [데이터 가공](데이터가공.md) — COALESCE, 문자열 함수
- [그룹과 집계](집계.md) — GROUP BY, COUNT, SUM
- [JOIN](JOIN.md) — 데이터 결합 후 변환
