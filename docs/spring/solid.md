## SOLID 원칙

<div class="concept-box" markdown="1">

좋은 객체지향 설계의 5가지 기준. Spring은 이 원칙들을 실현하는 도구다.

| 원칙 | 이름 | 한 줄 요약 |
|------|------|-----------|
| **S** | SRP (Single Responsibility Principle) — 단일 책임 원칙 | 하나의 클래스는 변경 이유가 하나뿐이어야 한다 |
| **O** | OCP (Open/Closed Principle) — 개방-폐쇄 원칙 | 확장에는 열려 있고, 변경에는 닫혀 있어야 한다 |
| **L** | LSP (Liskov Substitution Principle) — 리스코프 치환 원칙 | 구현체는 인터페이스의 계약을 반드시 지켜야 한다 |
| **I** | ISP (Interface Segregation Principle) — 인터페이스 분리 원칙 | 범용 인터페이스 하나보다 작은 인터페이스 여러 개가 낫다 |
| **D** | DIP (Dependency Inversion Principle) — 의존관계 역전 원칙 | 구현이 아닌 인터페이스(추상)에 의존해야 한다 |

</div>

---

### SRP (Single Responsibility Principle) — 단일 책임 원칙

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

### OCP (Open/Closed Principle) — 개방-폐쇄 원칙

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

### LSP (Liskov Substitution Principle) — 리스코프 치환 원칙

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

### ISP (Interface Segregation Principle) — 인터페이스 분리 원칙

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

### DIP (Dependency Inversion Principle) — 의존관계 역전 원칙

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

---

## 언제 쓰는지

| 상황 | 적용 원칙 | 이유 |
|------|---------|------|
| 클래스가 너무 많은 역할을 가질 때 | SRP | 변경 이유를 하나로 좁혀야 함 |
| 새 기능 추가 시 기존 코드를 수정해야 할 때 | OCP | 확장은 열려 있어야 함 |
| 구현체 교체 시 동작이 달라질 때 | LSP | 인터페이스 계약을 구현체가 지켜야 함 |
| 구현 클래스가 사용 안 하는 메서드까지 강제 구현될 때 | ISP | 인터페이스를 분리해야 함 |
| 구현 클래스를 직접 `new`로 생성할 때 | DIP | 추상에 의존해야 함 |
| 단위 테스트 작성이 어려울 때 | SRP·DIP | 의존성 역전·단일 책임 위반 신호 |

---

## 장점

| 장점 | 설명 |
|------|------|
| **변경 범위 최소화** | 수정 시 연쇄 영향 감소 — 버그가 퍼지지 않음 |
| **테스트 용이성** | 인터페이스 기반 설계 → 목(Mock) 객체로 단위 테스트 쉬움 |
| **재사용성** | 단일 책임 + 작은 인터페이스 → 컴포넌트 재사용 가능 |
| **확장성** | 새 기능 추가 시 기존 코드 건드리지 않고 확장 |
| **가독성** | 클래스 역할이 명확해 코드 이해·인수인계 쉬움 |

---

## 단점

| 단점 | 설명 |
|------|------|
| **오버엔지니어링 위험** | 단순한 코드에 과도한 인터페이스·추상화 적용 가능 |
| **클래스·파일 수 증가** | ISP 과잉 적용 시 인터페이스 파편화 |
| **초기 설계 비용** | 처음부터 구조를 깊이 고민해야 함 |
| **학습 곡선** | 원칙 이해 없이 적용하면 오히려 복잡도 증가 |

---

## 특징

- SOLID는 **규칙이 아닌 원칙** — 상황에 따라 트레이드오프 판단 필요
- 5가지 원칙이 서로 연결 — DIP를 지키면 OCP도 자연스럽게 지켜짐
- Spring 프레임워크 자체가 IoC·DI·레이어 구조로 SOLID를 실현하는 구조로 설계됨

---

## 주의할 점

<div class="warning-box" markdown="1">

**⚠️ SRP 과잉 적용 — 응집도 저하**

```java
// ❌ 너무 잘게 나눈 클래스 — 사용자 검증 하나에 3개 클래스를 다 알아야 함
class UserNameValidator  { ... }
class UserEmailValidator { ... }
class UserAgeValidator   { ... }

// ✅ 적절히 묶기
class UserValidator {
    void validateName(...)  { ... }
    void validateEmail(...) { ... }
    void validateAge(...)   { ... }
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ SOLID를 목표로 착각**

SOLID는 유지보수성을 높이기 위한 **수단**이다. 원칙 준수 자체가 목표가 되면 불필요한 추상화로 코드가 오히려 복잡해진다. 소규모 프로젝트·프로토타입에서는 유연하게 적용한다.

</div>

---

## 베스트 프랙티스

<div class="success-box" markdown="1">

- **점진적으로 적용** — 처음부터 완벽히 설계하려 하지 말고 리팩토링을 통해 개선
- **Spring DI 활용** — 인터페이스 + 생성자 주입으로 DIP·OCP를 자연스럽게 실현
- **테스트로 검증** — 단위 테스트 작성이 어렵다면 SRP·DIP 위반 신호로 인식
- **추상화 기준** — 변경 이유가 2가지 이상 생길 때 인터페이스 도입을 검토

</div>

---

## 실무에서는?

| 실무 패턴 | 관련 원칙 | 설명 |
|---------|---------|------|
| `@Controller` / `@Service` / `@Repository` 레이어 분리 | SRP | 각 레이어가 단일 책임을 가짐 |
| `MemberRepository` 인터페이스 → `JpaMemberRepository` 구현체 | DIP·OCP | 구현체 교체 시 서비스 코드 수정 불필요 |
| 새 할인 정책 추가 시 기존 `OrderService` 수정 없이 구현체만 추가 | OCP | Spring DI로 주입만 변경 |
| `@MockBean`으로 의존성 교체 가능 | DIP | 인터페이스에 의존하기 때문에 Mock 교체 가능 |
