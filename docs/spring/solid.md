## SOLID 원칙

<div class="concept-box" markdown="1">

좋은 객체지향 설계의 5가지 기준. Spring은 이 원칙들을 실현하는 도구다.

| 원칙 | 이름 | 한 줄 요약 |
|------|------|-----------|
| **S** | SRP — 단일 책임 원칙 | 하나의 클래스는 변경 이유가 하나뿐이어야 한다 |
| **O** | OCP — 개방-폐쇄 원칙 | 확장에는 열려 있고, 변경에는 닫혀 있어야 한다 |
| **L** | LSP — 리스코프 치환 원칙 | 구현체는 인터페이스의 계약을 반드시 지켜야 한다 |
| **I** | ISP — 인터페이스 분리 원칙 | 범용 인터페이스 하나보다 작은 인터페이스 여러 개가 낫다 |
| **D** | DIP — 의존관계 역전 원칙 | 구현이 아닌 인터페이스(추상)에 의존해야 한다 |

</div>

---

### SRP — 단일 책임 원칙

> **하나의 클래스는 하나의 책임만 가진다.**

"책임"이란 "변경의 이유"다. 변경해야 할 이유가 하나여야 한다는 뜻.

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad**

```java
// 위반: 주문 서비스가 너무 많은 일을 한다
public class OrderService {
    public void order(...) { ... }          // 주문 처리
    public void sendEmail(...) { ... }      // 이메일 발송
    public void saveToFile(...) { ... }     // 파일 저장
    public void validateMember(...) { ... } // 회원 검증
    // 이메일 발송 로직이 바뀌면 OrderService를 수정해야 함 → SRP 위반
}
```

</div>
<div class="after" markdown="1">
**Good**

```java
// 준수: 역할을 분리
public class OrderService   { public void order(...) { ... } }
public class EmailService   { public void send(...) { ... } }
public class MemberValidator { public void validate(...) { ... } }
// 이메일 로직이 바뀌면 EmailService만 수정하면 됨
```

</div>
</div>

SRP를 지키면 변경 범위가 좁아져서 버그가 퍼지지 않는다.

---

### OCP — 개방-폐쇄 원칙

> **확장에는 열려 있고, 변경에는 닫혀 있어야 한다.**
> 새 기능을 추가할 때 기존 코드를 수정하면 안 된다.

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad**

```java
// 위반: 구현체를 직접 선택하므로 변경 시 OrderServiceImpl 코드를 수정해야 함
public class OrderServiceImpl {
    private DiscountPolicy discountPolicy = new RateDiscountPolicy(); // 변경하려면 이 줄을 건드려야 함
}
```

</div>
<div class="after" markdown="1">
**Good**

```java
// 준수: 설정 코드(AppConfig)만 바꾸면 OrderServiceImpl은 그대로
public class AppConfig {
    public DiscountPolicy discountPolicy() {
        // return new FixDiscountPolicy();
        return new RateDiscountPolicy(); // 이 한 줄만 바꾸면 됨
    }
}
public class OrderServiceImpl {
    private final DiscountPolicy discountPolicy; // 인터페이스에만 의존
    public OrderServiceImpl(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy; // 외부에서 주입받음
    }
}
```

</div>
</div>

**Spring DI가 OCP를 실현해준다.** 설정만 바꾸면 코드 로직은 건드리지 않아도 된다.

---

### LSP — 리스코프 치환 원칙

> **구현체는 인터페이스가 약속한 동작을 반드시 지켜야 한다.**
> 부모 타입이 들어가는 자리에 자식 타입이 들어가도 프로그램이 정상 동작해야 한다.

```java
public interface DiscountPolicy {
    int discount(Member member, int price); // "할인 금액을 반환"한다는 계약
}

// LSP 위반: 계약을 어김
public class BadDiscountPolicy implements DiscountPolicy {
    @Override
    public int discount(Member member, int price) {
        return price * 2; // 할인인데 오히려 가격을 올림 → 계약 위반
    }
}

// LSP 준수: 계약을 지킴
public class RateDiscountPolicy implements DiscountPolicy {
    @Override
    public int discount(Member member, int price) {
        return price / 10; // 10% 할인 금액 반환 → 계약 준수
    }
}
```

LSP를 지켜야 다형성이 의도대로 동작한다.

---

### ISP — 인터페이스 분리 원칙

> **범용 인터페이스 하나보다 작은 인터페이스 여러 개가 낫다.**

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad**

```java
// 위반: 모든 기능이 한 인터페이스에
public interface UserInterface {
    void login();
    void logout();
    void changePassword();
    void viewAdminPanel(); // 일반 사용자는 필요 없는 기능
    void deleteUser();     // 일반 사용자는 필요 없는 기능
    // 일반 사용자가 이 인터페이스를 구현하면 불필요한 메서드까지 구현 강제됨
}
```

</div>
<div class="after" markdown="1">
**Good**

```java
// 준수: 역할별로 분리
public interface UserAuth  { void login(); void logout(); void changePassword(); }
public interface AdminOps  { void viewAdminPanel(); void deleteUser(); }

// 일반 사용자
public class RegularUser implements UserAuth { ... }

// 관리자
public class AdminUser implements UserAuth, AdminOps { ... }
```

</div>
</div>

인터페이스가 명확해지고, 구현 클래스에서 불필요한 메서드를 강제로 구현하지 않아도 된다.

---

### DIP — 의존관계 역전 원칙

> **구현 클래스가 아닌 인터페이스(추상)에 의존해야 한다.**

==Spring DI==의 핵심 원칙이다.

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">
**Bad**

```java
// 위반: 구현 클래스에 직접 의존
public class OrderServiceImpl {
    private MemoryMemberRepository memberRepository = new MemoryMemberRepository(); // 구체 클래스!
    private FixDiscountPolicy discountPolicy = new FixDiscountPolicy();              // 구체 클래스!
    // MemoryMemberRepository → JdbcMemberRepository로 바꾸면 이 코드도 수정 필요
}
```

</div>
<div class="after" markdown="1">
**Good**

```java
// 준수: 인터페이스에만 의존
public class OrderServiceImpl {
    private MemberRepository memberRepository;  // 인터페이스만 앎
    private DiscountPolicy discountPolicy;       // 인터페이스만 앎
    // 어떤 구현체가 들어오는지는 외부(Spring)가 결정 — 이 클래스는 모름
}
```

</div>
</div>

그런데 문제가 있다:

```java
// 딜레마: 인터페이스에만 의존하면 구현체를 어디서 만드나?
private MemberRepository memberRepository; // 타입만 있고
// memberRepository = new ??? → 누군가는 구현체를 new 해야 함
```

<div class="success-box" markdown="1">

**이 문제를 Spring이 해결한다.** Spring이 구현체를 만들어서 주입해준다.

</div>

---

## Spring이 SOLID를 실현하는 방법

```text
[개발자]
  OrderServiceImpl은 MemberRepository, DiscountPolicy 인터페이스에만 의존하여 작성

[Spring]
  1. MemoryMemberRepository 객체 생성 (new)
  2. RateDiscountPolicy 객체 생성 (new)
  3. OrderServiceImpl 생성자에 1, 2번 주입
  → OrderServiceImpl은 어떤 구현체가 들어왔는지 모름
```

`OrderServiceImpl` 입장에서는 "누가 만들어서 줬는지"를 전혀 모른다.
인터페이스만 알면 된다. 이것이 DIP 준수다.

구현체를 교체할 때 `OrderServiceImpl`을 건드리지 않아도 된다. 이것이 OCP 준수다.
