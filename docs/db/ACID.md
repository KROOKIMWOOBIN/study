# ACID

<div class="concept-box" markdown="1">

**ACID**: 트랜잭션이 안전하게 수행되기 위해 보장해야 하는 4가지 성질.

</div>

## 왜 필요한가?

여러 사용자가 동시에 DB를 읽고 쓰면 데이터가 깨질 수 있다. 예를 들어 계좌 이체 중 서버가 죽으면 돈이 빠져나갔는데 입금이 안 된 상태가 될 수 있다. ACID는 이런 상황에서 데이터 정합성을 보장하는 약속이다.

## 4가지 성질

| 성질 | 이름 | 설명 |
|------|------|------|
| **A** | Atomicity (원자성) | 트랜잭션 내 연산은 ==전부 성공하거나 전부 실패==한다. 중간 상태가 없다. |
| **C** | Consistency (일관성) | 트랜잭션 전후로 DB가 정의한 규칙(제약 조건, 무결성)을 항상 만족해야 한다. |
| **I** | Isolation (격리성) | 동시에 실행되는 트랜잭션끼리 서로의 중간 상태를 볼 수 없다. |
| **D** | Durability (지속성) | 커밋된 데이터는 장애가 발생해도 사라지지 않는다. (WAL 로그로 복구 가능) |

## 각 성질이 깨지면?

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Atomicity 위반**

```sql
-- 계좌 이체 도중 서버 장애
UPDATE account SET balance = balance - 10000 WHERE id = 'A'; -- 성공
-- 서버 죽음 → B에 입금 안 됨
UPDATE account SET balance = balance + 10000 WHERE id = 'B'; -- 미실행
```
→ 돈이 증발
</div>
<div class="after" markdown="1">
**Atomicity 보장**

```sql
BEGIN;
UPDATE account SET balance = balance - 10000 WHERE id = 'A';
UPDATE account SET balance = balance + 10000 WHERE id = 'B';
COMMIT; -- 둘 다 성공해야 커밋, 아니면 ROLLBACK
```
→ 전부 성공 or 전부 취소
</div>
</div>

## 어떻게 쓰는지

개발자는 트랜잭션 경계를 선언하는 것으로 ACID를 활용한다.

```sql
-- SQL 직접 사용
BEGIN;
UPDATE account SET balance = balance - 10000 WHERE id = 'A';
UPDATE account SET balance = balance + 10000 WHERE id = 'B';
COMMIT; -- 모두 성공 시 확정
-- 중간에 오류 발생 시 ROLLBACK으로 전체 취소
```

```java
// Spring @Transactional 사용
@Service
public class AccountService {

    @Transactional  // 메서드 전체가 하나의 트랜잭션으로 묶임
    public void transfer(String from, String to, int amount) {
        accountRepository.decrease(from, amount);
        accountRepository.increase(to, amount); // 여기서 예외 → 전체 ROLLBACK
    }
}
```

## 언제 쓰는지

- 금융 거래, 주문/결제처럼 **여러 테이블에 걸친 데이터 변경이 원자적으로 처리되어야 할 때**
- 단순 조회(`SELECT`)만 하는 경우에는 트랜잭션이 불필요하거나 읽기 전용으로 선언한다.

## 특징

- ACID는 **DBMS가 내부적으로 보장**한다. 개발자가 직접 구현하는 게 아니다.
- Isolation의 강도는 **격리 수준(Isolation Level)** 으로 조절한다. → [격리 수준](./격리수준.md) 참고
- ACID를 완전히 보장하면 **성능이 낮아진다.** 특히 Isolation이 강할수록 Lock 경합이 많아진다.
- NoSQL은 일반적으로 ACID보다 **가용성·성능을 우선**해 일부 성질을 완화한다. (BASE 모델)

<div class="warning-box" markdown="1">

**주의**: Atomicity는 "전부 or 전부 아님"이지만, 이것만으로는 동시 실행 문제가 해결되지 않는다. 동시성 문제는 **Isolation Level**로 별도 제어해야 한다.

</div>
