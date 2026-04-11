# JOIN

<div class="concept-box" markdown="1">

**JOIN**: 두 개 이상의 테이블을 공통 컬럼(주로 PK-FK 관계)을 기준으로 합쳐 하나의 결과 집합으로 만드는 연산.

</div>

## 왜 쓰는지

관계형 DB는 데이터를 여러 테이블로 분리해 중복을 줄인다 (정규화). 하지만 실제 조회할 때는 여러 테이블의 데이터가 함께 필요하다. JOIN이 그 연결 역할을 한다.

```sql
-- member 테이블: id, name
-- orders 테이블: id, member_id, amount
-- member와 orders를 연결해야 "김철수가 주문한 내역" 조회 가능
```

## JOIN 종류

### INNER JOIN

```sql
SELECT m.name, o.amount
FROM member m
INNER JOIN orders o ON m.id = o.member_id;
```

- **두 테이블 모두에 일치하는 행만** 반환한다.
- 주문이 없는 회원은 결과에 포함되지 않는다.
- `JOIN`만 쓰면 기본값이 `INNER JOIN`이다.

```text
member:          orders:           결과:
id | name        id | member_id    name  | amount
1  | 김철수       1  | 1    ← 매칭  김철수 | 10000
2  | 이영희       2  | 1    ← 매칭  김철수 | 5000
3  | 박민준       (이영희·박민준 주문 없음 → 제외)
```

### LEFT (OUTER) JOIN

```sql
SELECT m.name, o.amount
FROM member m
LEFT JOIN orders o ON m.id = o.member_id;
```

- **왼쪽 테이블(member)의 모든 행**을 반환한다.
- 오른쪽 테이블(orders)에 매칭되는 행이 없으면 `NULL`로 채운다.
- "주문이 없는 회원도 포함해서 보고 싶을 때" 사용한다.

```text
결과:
name  | amount
김철수 | 10000
김철수 | 5000
이영희 | NULL    ← 주문 없음
박민준 | NULL    ← 주문 없음
```

### RIGHT (OUTER) JOIN

```sql
SELECT m.name, o.amount
FROM member m
RIGHT JOIN orders o ON m.id = o.member_id;
```

- **오른쪽 테이블(orders)의 모든 행**을 반환한다.
- LEFT JOIN과 방향만 반대. 실무에서는 LEFT JOIN으로 테이블 순서를 바꿔 표현하는 것이 일반적이다.

### FULL OUTER JOIN

```sql
-- MySQL에서는 직접 지원 안 함 → UNION으로 대체
SELECT m.name, o.amount FROM member m LEFT JOIN orders o ON m.id = o.member_id
UNION
SELECT m.name, o.amount FROM member m RIGHT JOIN orders o ON m.id = o.member_id;

-- PostgreSQL
SELECT m.name, o.amount
FROM member m
FULL OUTER JOIN orders o ON m.id = o.member_id;
```

- 양쪽 테이블 모두의 행을 반환. 매칭 안 되는 쪽은 NULL.

### CROSS JOIN

```sql
SELECT m.name, p.name AS product
FROM member m
CROSS JOIN product p;
```

- 두 테이블의 **모든 조합(카테시안 곱)** 을 반환한다.
- member 3행 × product 5개 = 15행
- 조건 없이 모든 경우의 수를 만들 때 사용한다. (경우의 수 테이블 생성 등)

### SELF JOIN

```sql
SELECT e.name AS 직원, m.name AS 상사
FROM employee e
LEFT JOIN employee m ON e.manager_id = m.id;
```

- **같은 테이블을 두 번** 참조한다. 계층 구조(조직도, 카테고리) 표현에 사용한다.

## 언제 어떤 JOIN을 쓰는지

| 상황 | 사용 JOIN |
|------|----------|
| 두 테이블 모두에 존재하는 데이터만 필요 | `INNER JOIN` |
| 기준 테이블의 모든 행 + 연관 데이터 | `LEFT JOIN` |
| 두 테이블 모두의 데이터 (매칭 안 되는 것 포함) | `FULL OUTER JOIN` |
| 모든 조합이 필요 | `CROSS JOIN` |
| 계층 구조 표현 | `SELF JOIN` |

## 주의할 점

<div class="warning-box" markdown="1">

**ON 조건 vs WHERE 조건 차이 (OUTER JOIN 한정)**

```sql
-- ⚠️ WHERE로 필터링하면 OUTER JOIN이 사실상 INNER JOIN이 됨
SELECT m.name, o.amount
FROM member m
LEFT JOIN orders o ON m.id = o.member_id
WHERE o.amount > 5000;  -- NULL인 행(주문 없는 회원)이 제거됨

-- ✅ ON 절에 조건을 포함하면 주문 없는 회원도 유지
SELECT m.name, o.amount
FROM member m
LEFT JOIN orders o ON m.id = o.member_id AND o.amount > 5000;
```

</div>

<div class="danger-box" markdown="1">

**카테시안 곱 주의** — ON 조건을 빠뜨리면 CROSS JOIN과 동일한 결과가 나온다.

```sql
-- ❌ ON 조건 누락 → 100만 × 50만 = 5천억 행
SELECT * FROM member, orders;
```

</div>

## 성능 팁

- JOIN 조건이 되는 컬럼(FK)에 **인덱스**를 반드시 생성한다.
- JOIN 대상 테이블을 줄이려면 `WHERE`로 먼저 필터링한 서브쿼리 / CTE를 활용한다.
- 3개 이상 테이블을 JOIN할 때는 실행 계획(`EXPLAIN`)으로 순서를 확인한다.
