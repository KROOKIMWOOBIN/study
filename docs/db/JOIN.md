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

## 관계 유형과 JOIN

JOIN을 올바르게 사용하려면 테이블 간 **카디널리티(Cardinality)**, 즉 관계의 방향과 수량을 먼저 파악해야 한다. 관계 유형에 따라 JOIN 결과 행 수가 달라진다.

### 1:1 관계 (to-one)

- 한 행이 상대 테이블의 **정확히 한 행**에 대응
- JOIN 결과 행 수가 원본과 **동일**
- 예: `user` ↔ `user_profile` (회원 1명당 프로필 1개)

```sql
SELECT u.name, p.bio
FROM user u
LEFT JOIN user_profile p ON u.id = p.user_id;
```

```text
user:              user_profile:          결과:
id | name          user_id | bio          name  | bio
1  | 김철수    →   1       | "개발자"     김철수 | 개발자
2  | 이영희    →   2       | "디자이너"   이영희 | 디자이너
                                          ← 행 수 동일
```

### 1:N 관계 (to-many)

- 한 행이 상대 테이블의 **여러 행**에 대응
- JOIN 결과 행 수가 **N쪽 행 수만큼 증가**
- 예: `member` ↔ `orders` (회원 1명이 주문 여러 개)

```sql
SELECT m.name, o.amount
FROM member m
LEFT JOIN orders o ON m.id = o.member_id;
```

```text
member:         orders:                      결과:
id | name       id | member_id | amount      name  | amount
1  | 김철수  →  1  | 1         | 10000       김철수 | 10000   ← 행 증가!
                2  | 1         | 5000        김철수 | 5000    ← 행 증가!
2  | 이영희  →  3  | 2         | 20000       이영희 | 20000
                                             ← member 2행 → 결과 3행
```

<div class="warning-box" markdown="1">

**to-many JOIN 후 집계 시 주의** — 1쪽 테이블 기준 집계가 필요하면 반드시 `GROUP BY`를 사용한다.

```sql
-- ❌ 잘못된 예: 주문 건수가 아닌 전체 결과 행 수를 셈
SELECT COUNT(*) FROM member m JOIN orders o ON m.id = o.member_id;
-- member가 2명인데 주문이 3건이면 → 3 반환 (회원 수 아님)

-- ✅ 올바른 예: 회원별 주문 건수
SELECT m.name, COUNT(o.id) AS order_count
FROM member m
LEFT JOIN orders o ON m.id = o.member_id
GROUP BY m.id, m.name;
```

</div>

### N:M 관계

- 양쪽 모두 상대 테이블의 **여러 행**에 대응
- **중간 매핑 테이블**을 거쳐 두 번 JOIN
- 예: `student` ↔ `student_course` ↔ `course`

```sql
SELECT s.name, c.title
FROM student s
INNER JOIN student_course sc ON s.id = sc.student_id
INNER JOIN course c ON sc.course_id = c.id;
```

```text
student:          student_course:          course:
id | name         student_id | course_id   id | title
1  | 김철수        1          | 10          10 | DB
                  1          | 20          20 | Java
2  | 이영희        2          | 10

결과:
name  | title
김철수 | DB
김철수 | Java
이영희 | DB
← student 2행 → 결과 3행
```

<div class="danger-box" markdown="1">

**N:M을 두 개 이상 동시에 JOIN하면 행이 기하급수적으로 증가한다.**

```sql
-- ❌ 위험: student가 course도 N:M, tag도 N:M이면
SELECT s.name, c.title, t.name
FROM student s
JOIN student_course sc ON s.id = sc.student_id
JOIN course c ON sc.course_id = c.id
JOIN student_tag st ON s.id = st.student_id   -- 두 번째 to-many
JOIN tag t ON st.tag_id = t.id;
-- 수강 3건 × 태그 4개 = 12행 (카테시안 곱에 가까워짐)
```

이 경우 서브쿼리 또는 별도 쿼리로 분리하는 것이 안전하다.

</div>

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

## to-many JOIN 실무 주의사항

### 행 폭발(Row Multiplication)

1:N 또는 N:M JOIN 시 1쪽 행이 N개만큼 복제된다. 집계 없이 사용하면 중복 행이 결과에 포함된다.

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad — 중복 행 인지 못함**

```sql
-- 회원별 총 주문 금액을 구하려 했지만
SELECT m.name, SUM(o.amount) AS total
FROM member m
LEFT JOIN orders o ON m.id = o.member_id;
-- GROUP BY 없음 → 전체를 하나로 합산
-- 회원별 금액이 아닌 전체 합계가 나옴
```
</div>
<div class="after" markdown="1">
**Good — GROUP BY로 묶기**

```sql
SELECT m.name, SUM(o.amount) AS total
FROM member m
LEFT JOIN orders o ON m.id = o.member_id
GROUP BY m.id, m.name;
-- 회원별로 올바르게 집계됨
```
</div>
</div>

### COUNT 오류

to-many JOIN 후 `COUNT(*)`는 원본 테이블 행 수가 아닌 **JOIN 결과 행 수**를 센다.

```sql
-- ❌ 회원 수를 세려 했지만 주문 건수가 반환됨
SELECT COUNT(*) FROM member m JOIN orders o ON m.id = o.member_id;

-- ✅ 회원 수 (중복 제거)
SELECT COUNT(DISTINCT m.id) FROM member m JOIN orders o ON m.id = o.member_id;

-- ✅ 또는 서브쿼리로 분리
SELECT COUNT(*) FROM member WHERE id IN (SELECT DISTINCT member_id FROM orders);
```

### N+1 문제

ORM(JPA, MyBatis 등)에서 to-many 연관 관계를 **루프 쿼리**로 처리할 때 발생한다.

```text
1번 쿼리: 회원 100명 조회
N번 쿼리: 각 회원의 주문 목록 조회 (100번 추가 실행)
→ 총 101번 = N+1 문제
```

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad — N+1 발생**

```java
// JPA: LAZY 로딩 시
List<Member> members = memberRepo.findAll(); // 쿼리 1회
for (Member m : members) {
    m.getOrders().size(); // 회원마다 쿼리 1회 → N회
}
```
</div>
<div class="after" markdown="1">
**Good — JOIN FETCH로 한 번에**

```java
// JPQL: JOIN FETCH
List<Member> members = em.createQuery(
  "SELECT DISTINCT m FROM Member m " +
  "JOIN FETCH m.orders", Member.class)
  .getResultList(); // 쿼리 1회로 해결
```
</div>
</div>

SQL 레벨에서도 서브쿼리로 해결 가능하다.

```sql
-- IN 서브쿼리로 한 번에 조회
SELECT * FROM orders
WHERE member_id IN (SELECT id FROM member WHERE grade = 'VIP');
```

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
- to-many JOIN 시 **드라이빙 테이블을 1쪽(소량)** 으로 유지한다. N쪽이 드라이빙 테이블이 되면 루프 횟수가 폭증한다.
- JOIN 대상 테이블을 줄이려면 `WHERE`로 먼저 필터링한 서브쿼리 / CTE를 활용한다.
- 3개 이상 테이블을 JOIN할 때는 실행 계획(`EXPLAIN`)으로 순서를 확인한다.
- N:M to-many를 여러 개 동시에 JOIN해야 한다면, 쿼리를 분리하거나 서브쿼리로 대체한다.
