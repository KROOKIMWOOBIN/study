> [← 홈](/study/) · [Spring](/study/spring/basic/)

## AOP (Aspect-Oriented Programming)

### 왜 쓰는가?

로깅, 트랜잭션, 보안, 성능 측정 같은 **횡단 관심사(Cross-Cutting Concern)**는 여러 클래스에 반복 등장한다. AOP는 이를 핵심 비즈니스 로직과 분리해 **코드 중복을 제거**하고 관심사를 분리한다.

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
