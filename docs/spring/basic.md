# Spring

스프링의 핵심 원리 정리.

---

| 주제 | 한 줄 설명 |
| --- | --- |
| [SOLID 원칙](#solid) | 객체지향 설계 5원칙 — SRP, OCP, LSP, ISP, DIP |

---

## SOLID

객체지향 설계의 5가지 핵심 원칙. 스프링은 이 원칙들을 지키기 위한 도구를 제공한다.

| 원칙 | 이름 | 핵심 |
| --- | --- | --- |
| SRP | 단일 책임 원칙 | 한 클래스는 하나의 책임만 가져야 한다 |
| OCP | 개방-폐쇄 원칙 | 확장에는 열려 있으나, 변경에는 닫혀 있어야 한다 |
| LSP | 리스코프 치환 원칙 | 인터페이스로 설계한 기능을 구현체가 위반하면 안 된다 |
| ISP | 인터페이스 분리 원칙 | 범용 인터페이스 하나보다 여러 개의 작은 인터페이스가 낫다 |
| DIP | 의존관계 역전 원칙 | 구현 클래스에 의존하지 않고 인터페이스(추상)에 의존해야 한다 |

### SRP (Single Responsibility Principle)
- 단일 책임 원칙
- 한 클래스는 하나의 책임만 가져야 한다
- 변경이 있을 때 파급 효과가 적으면 단일 책임 원칙을 잘 따른 것

### OCP (Open/Closed Principle)
- 개방-폐쇄 원칙
- 확장에는 열려 있으나, 변경에는 닫혀 있어야 한다
- 다형성을 활용: 인터페이스를 구현한 새로운 클래스를 하나 만들어서 새로운 기능을 구현
- 스프링 DI 컨테이너가 이 원칙을 지킬 수 있도록 도와줌

### LSP (Liskov Substitution Principle)
- 리스코프 치환 원칙
- 인터페이스로 설계한 기능을 위반하지 않아야 한다
- 예) 자동차를 앞으로 가는 기능을 만들었으면 느리더라도 앞으로 가야 한다 (뒤로 가면 LSP 위반)

### ISP (Interface Segregation Principle)
- 인터페이스 분리 원칙
- 클라이언트를 위한 범용 인터페이스보다 여러 개의 작은 인터페이스가 낫다
- 인터페이스가 명확해지고, 대체 가능성이 높아짐

### DIP (Dependency Inversion Principle)
- 의존관계 역전 원칙
- 구현 클래스에 의존하지 않고 인터페이스를 의존해야 한다
- 역할에 의존하게 해야 한다
- 스프링의 DI(Dependency Injection)가 이 원칙을 실현

```java
// DIP 위반
private MemberRepository memberRepository = new MemoryMemberRepository();

// DIP 준수 (스프링 DI 활용)
private MemberRepository memberRepository; // 인터페이스에만 의존
```
