## AOP (Aspect-Oriented Programming)

### 왜 쓰는가?

<div class="concept-box" markdown="1">

로깅, 트랜잭션, 보안, 성능 측정 같은 **횡단 관심사(Cross-Cutting Concern)**는 여러 클래스에 반복 등장한다. ==AOP==는 이를 핵심 비즈니스 로직과 분리해 **코드 중복을 제거**하고 관심사를 분리한다.

</div>

```markdown
// Before AOP: 모든 메서드에 중복
public class MemberService {
    public void save(Member member) {
        log.info("시작");           // 중복
        long start = System.currentTimeMillis();  // 중복
        // 핵심 로직
        memberRepository.save(member);
        log.info("완료: {}ms", System.currentTimeMillis() - start);  // 중복
    }
}

// After AOP: 핵심 로직만 남김
public class MemberService {
    public void save(Member member) {
        memberRepository.save(member);  // 핵심 로직만
    }
}
```

### 핵심 개념

| 용어 | 설명 |
|------|------|
| `Aspect` | 횡단 관심사를 모듈화한 클래스 |
| `Advice` | 실제로 실행되는 부가 로직 |
| `Pointcut` | Advice를 적용할 지점 표현식 |
| `JoinPoint` | Advice가 적용될 수 있는 실행 지점 |
| `Weaving` | Aspect를 대상 객체에 적용하는 과정 |

### Advice 종류

| 어노테이션 | 실행 시점 |
|-----------|----------|
| `@Before` | 메서드 실행 전 |
| `@After` | 메서드 실행 후 (성공/실패 무관) |
| `@AfterReturning` | 메서드 정상 반환 후 |
| `@AfterThrowing` | 예외 발생 후 |
| `@Around` | 메서드 실행 전/후 모두 제어 (가장 강력) |

### 실행 시간 측정 예시

```markdown
@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    @Around("execution(* com.myapp.service..*(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();  // 실제 메서드 실행
            long elapsed = System.currentTimeMillis() - start;
            log.info("[성능] {} - {}ms", methodName, elapsed);
            return result;
        } catch (Exception e) {
            log.error("[성능] {} - 예외 발생: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
```

### Pointcut 표현식

```markdown
// 패키지 하위 모든 메서드
"execution(* com.myapp.service..*(..))"

// 특정 어노테이션이 붙은 메서드
"@annotation(com.myapp.annotation.Loggable)"

// 특정 클래스의 모든 메서드
"execution(* com.myapp.service.MemberService.*(..))"

// 특정 파라미터 타입
"execution(* com.myapp..*(*..Member, ..))"
```

### 커스텀 어노테이션 기반 AOP

```markdown
// 어노테이션 정의
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {}

// Aspect
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(Loggable)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("요청: {}", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        log.info("응답 완료");
        return result;
    }
}

// 사용
@Loggable
public Member findById(Long id) { ... }
```

### 프록시 기반 AOP

Spring AOP는 **프록시 패턴**으로 동작한다. 실제 객체 대신 프록시 객체가 주입되고, 프록시가 Advice를 실행한 뒤 실제 메서드를 호출한다.

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 같은 클래스 내 메서드 호출 | 프록시를 거치지 않아 AOP 미적용 | 별도 클래스로 분리 |
| `private` 메서드 | 프록시 적용 불가 | `public` 또는 AspectJ 사용 |
| `@Around`에서 `proceed()` 누락 | 실제 메서드가 실행 안 됨 | `joinPoint.proceed()` 반드시 호출 |
| 남용 | 실행 흐름 파악이 어려워짐 | 진짜 횡단 관심사에만 적용 |

---

## 내부 동작 원리

### 프록시가 뭔가?

> 프록시(Proxy)는 **대리인**이다. 친구에게 전화를 걸었는데 대신 비서가 받아서 메모를 남기고 친구에게 연결해주는 것처럼 — 스프링 AOP는 실제 객체 앞에 **프록시 객체**를 끼워 넣어서, 메서드 호출을 가로채 부가 로직(로깅, 트랜잭션 등)을 실행한 다음 실제 객체로 위임한다.

```text
개발자가 의존성 주입 받는 것 ──→ 실제로 주입되는 것

MemberService memberService         MemberService$$EnhancerBySpringCGLIB
     (인터페이스/타입)             (실제 빈 → 사실은 프록시)
          ↓                                  ↓
 memberService.save()          프록시.save()
                                  ① @Before Advice 실행
                                  ② 실제 memberService.save() 위임
                                  ③ @AfterReturning Advice 실행
```

### 프록시 생성 시점 — AnnotationAwareAspectJAutoProxyCreator

> 스프링이 언제 프록시를 만드나? — 빈 생성 직후, `BeanPostProcessor`가 처리할 때다.

```text
스프링 컨테이너 빈 생성 과정
  ① MemberService 인스턴스 생성 (new MemberService())
  ② 의존관계 주입
  ③ BeanPostProcessor.postProcessAfterInitialization() 호출
       → AnnotationAwareAspectJAutoProxyCreator 실행
       → "이 빈에 적용할 @Aspect가 있나?" 확인
       → MemberService 메서드 중 Pointcut 조건과 매칭되는 것이 있으면
       → MemberService 대신 프록시 객체 생성 후 반환
  ④ 컨테이너에 저장되는 것은 프록시 객체
```

### JDK 동적 프록시 vs CGLIB

스프링 AOP가 사용하는 두 가지 프록시 방식.

<div class="compare-grid" markdown="1">
<div class="before" markdown="1">

**JDK 동적 프록시**

```java
// 인터페이스가 있어야 함
public interface MemberService {
    void save(Member member);
}
// 인터페이스 기반으로 프록시 생성
// java.lang.reflect.Proxy 사용
Proxy.newProxyInstance(
    classLoader,
    new Class[]{MemberService.class},
    invocationHandler
)
```

- 인터페이스 필수
- Java 기본 제공
- 리플렉션 기반 (느릴 수 있음)

</div>
<div class="after" markdown="1">

**CGLIB 프록시** (Spring Boot 기본)

```java
// 인터페이스 없어도 됨 — 클래스 상속으로 프록시 생성
public class MemberService { ... }

// CGLIB이 MemberService를 상속한 서브클래스 생성
class MemberService$$EnhancerBySpringCGLIB extends MemberService {
    @Override
    public void save(Member member) {
        // Advice 실행
        super.save(member); // 실제 메서드 위임
    }
}
```

- 인터페이스 없어도 됨
- 클래스 상속 기반
- Spring Boot 기본값

</div>
</div>

<div class="warning-box" markdown="1">

**CGLIB 프록시 제약**: 상속으로 프록시를 만들기 때문에 `final` 클래스나 `final` 메서드에는 적용 불가.
또한 기본 생성자(no-args constructor) 또는 `@RequiredArgsConstructor`에서 주의 필요.

</div>

### 프록시 체인 — 여러 Aspect가 겹칠 때

Aspect가 여러 개면 프록시가 체인으로 연결된다. `@Order`로 순서를 지정하지 않으면 순서가 불확실하다.

```text
클라이언트 호출
  → SecurityAspect 프록시 (@Around)
    → LoggingAspect 프록시 (@Around)
      → TransactionAspect 프록시 (@Around)
        → 실제 MemberService.save()
      ← TransactionAspect afterReturning
    ← LoggingAspect afterReturning
  ← SecurityAspect afterReturning
```

```java
@Order(1) @Aspect // 가장 먼저 실행 (가장 바깥 프록시)
public class SecurityAspect { ... }

@Order(2) @Aspect
public class LoggingAspect { ... }

@Order(3) @Aspect // 가장 나중에 실행 (가장 안쪽 프록시)
public class TransactionAspect { ... }
```

### Self-Invocation — AOP의 대표 함정

```java
@Service
public class MemberService {

    public void outer() {
        inner();  // ← this.inner() 직접 호출 = 프록시를 거치지 않음!
    }

    @Transactional  // @Around Advice가 있어야 트랜잭션 시작
    public void inner() { ... }
}
```

```text
문제 원인:
  - outer()를 호출하면 → 프록시.outer() → this.inner()
  - this는 실제 MemberService 객체 참조 (프록시 아님)
  - 따라서 inner()의 @Transactional이 무시됨

해결:
  - inner()를 별도 클래스로 분리
  - 또는 ApplicationContext에서 자기 자신을 다시 주입받아 프록시를 통해 호출
```
