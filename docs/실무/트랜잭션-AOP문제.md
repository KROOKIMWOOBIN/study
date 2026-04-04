> [← 홈](/study/) · [실무](/실무/실무/)

# 트랜잭션 & AOP 문제 (@Transactional이 작동하지 않는 케이스)

---

## 왜 공부하는가?

`@Transactional`은 Spring에서 가장 많이 쓰이는 어노테이션 중 하나지만, **AOP 프록시 기반**으로 동작하기 때문에 특정 상황에서는 기대와 달리 동작하지 않는다.
이를 모르면 롤백이 안 되거나, 트랜잭션이 아예 적용되지 않는 심각한 버그가 발생한다.

---

## 핵심 원리: 스프링 AOP는 프록시 기반

```
클라이언트 → [Spring Proxy] → 실제 Bean
                  ↑
            여기서 @Transactional 처리
            (트랜잭션 시작/커밋/롤백)
```

Spring은 `@Transactional`이 붙은 빈을 생성할 때, **실제 빈을 감싸는 프록시 객체**를 생성한다.
외부에서 메서드를 호출하면 프록시를 거쳐 트랜잭션이 적용된다.

**이 구조 때문에 "프록시를 우회하는 경우"에는 @Transactional이 동작하지 않는다.**

---

## 문제 케이스 1: 같은 클래스 내 메서드 호출 (Self-invocation)

### 발생 원인

같은 클래스 내에서 `this.메서드()`를 호출하면 프록시를 거치지 않고 실제 객체의 메서드가 직접 호출된다.

```
외부 호출 → [Proxy] → methodA()  ← 트랜잭션 시작됨
                          ↓
                      this.methodB()  ← 프록시 우회! 트랜잭션 미적용
```

### 잘못된 코드

```java
@Service
public class OrderService {

    @Transactional
    public void createOrder(OrderRequest request) {
        // 트랜잭션 O
        Order order = saveOrder(request);
        sendNotification(order); // ← 내부 호출! @Transactional 무시됨
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        // 의도: 별도 트랜잭션으로 실행
        // 실제: createOrder의 트랜잭션에 그냥 참여 (REQUIRES_NEW 무시)
        notificationRepository.save(new Notification(order));
    }
}
```

### 해결 방법 1: 메서드를 별도 클래스로 분리 (가장 권장)

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final NotificationService notificationService; // 외부 빈 주입

    @Transactional
    public void createOrder(OrderRequest request) {
        Order order = saveOrder(request);
        notificationService.sendNotification(order); // 프록시를 통해 호출됨
    }
}

@Service
public class NotificationService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        // 별도 트랜잭션으로 올바르게 동작
        notificationRepository.save(new Notification(order));
    }
}
```

### 해결 방법 2: AopContext.currentProxy() 사용

```java
@Service
@EnableAspectJAutoProxy(exposeProxy = true) // application class 또는 설정에 추가
public class OrderService {

    @Transactional
    public void createOrder(OrderRequest request) {
        Order order = saveOrder(request);
        // 프록시를 통해 호출
        ((OrderService) AopContext.currentProxy()).sendNotification(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        notificationRepository.save(new Notification(order));
    }
}
```

### 해결 방법 3: 자기 자신을 @Autowired로 주입

```java
@Service
public class OrderService {

    @Autowired
    private OrderService self; // 순환 참조 주의, Spring Boot 2.6+ 기본 비허용

    @Transactional
    public void createOrder(OrderRequest request) {
        Order order = saveOrder(request);
        self.sendNotification(order); // 프록시를 통해 호출
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        notificationRepository.save(new Notification(order));
    }
}
```

> **가장 좋은 방법은 클래스 분리다.** Self-injection이나 AopContext 사용은 코드 구조를 복잡하게 만든다.

---

## 문제 케이스 2: private 메서드에 @Transactional

### 발생 원인

Spring AOP 프록시는 **public 메서드만 가로챌 수 있다.**
`private` 메서드에 `@Transactional`을 붙여도 프록시가 적용되지 않아 아무 효과가 없다.

### 잘못된 코드

```java
@Service
public class PaymentService {

    public void processPayment(PaymentRequest request) {
        deductBalance(request); // private 메서드 직접 호출
    }

    @Transactional // 아무 효과 없음!
    private void deductBalance(PaymentRequest request) {
        accountRepository.deductBalance(request.getAmount());
        paymentRepository.save(new Payment(request));
        // 예외가 발생해도 롤백 안 됨
    }
}
```

### 올바른 코드

```java
@Service
public class PaymentService {

    public void processPayment(PaymentRequest request) {
        deductBalance(request);
    }

    @Transactional // public으로 변경
    public void deductBalance(PaymentRequest request) {
        accountRepository.deductBalance(request.getAmount());
        paymentRepository.save(new Payment(request));
    }
}
```

**장점/단점 정리**

| 상황 | 결과 |
|---|---|
| public 메서드 + @Transactional | 정상 동작 |
| protected 메서드 + @Transactional | CGLib 프록시에서는 동작, 일반 JDK 프록시는 미동작 |
| private 메서드 + @Transactional | 절대 동작하지 않음 |
| static 메서드 + @Transactional | 동작하지 않음 |

---

## 문제 케이스 3: @Transactional(readOnly=true)에서 쓰기 연산

### 발생 원인

`readOnly=true`는 해당 트랜잭션이 읽기 전용임을 선언한다.
JPA는 이 경우 **변경 감지(Dirty Checking)를 비활성화**하고, DB에 따라서는 읽기 전용 연결을 사용한다.
쓰기 연산을 시도하면 예외가 발생할 수 있다.

### 잘못된 코드

```java
@Service
public class UserService {

    @Transactional(readOnly = true) // 읽기 전용 선언
    public User updateUser(Long id, UpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.update(request.getName()); // Dirty Checking으로 업데이트 시도
        return user;
        // 결과: 변경 감지가 비활성화되어 업데이트가 DB에 반영되지 않을 수 있음
        // 또는 일부 DB에서는 예외 발생
    }
}
```

### 올바른 코드

```java
@Service
public class UserService {

    @Transactional(readOnly = true) // 조회 전용 메서드
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional // 기본값: readOnly=false (쓰기 가능)
    public User updateUser(Long id, UpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.update(request.getName());
        return user;
    }
}
```

### readOnly=true의 실제 효과

| 효과 | 설명 |
|---|---|
| JPA Dirty Checking 비활성화 | flush 시 엔티티 변경 감지 안 함 → 성능 향상 |
| 1차 캐시 스냅샷 미생성 | 메모리 절약 |
| DB 읽기 전용 연결 사용 | 읽기 전용 replica로 라우팅 가능 |
| 쓰기 방지 | DB에 따라 쓰기 시도 시 예외 |

---

## 문제 케이스 4: 예외를 catch하여 삼키는 경우

### 발생 원인

트랜잭션은 **예외가 메서드 밖으로 전파**되어야 롤백 처리를 한다.
예외를 catch 블록에서 잡아서 처리(삼키기)하면, 프록시는 예외가 발생했는지 알 수 없어 **커밋을 진행한다.**

### 잘못된 코드

```java
@Service
public class OrderService {

    @Transactional
    public void placeOrder(OrderRequest request) {
        try {
            orderRepository.save(new Order(request));
            paymentService.pay(request); // 여기서 예외 발생
        } catch (Exception e) {
            log.error("주문 처리 실패", e);
            // 예외를 삼킴 → 프록시는 정상 완료로 판단 → 커밋!
            // 결과: 주문은 저장되고 결제는 안 된 상태로 커밋됨
        }
    }
}
```

### 올바른 코드 1: 예외 재던지기

```java
@Service
public class OrderService {

    @Transactional
    public void placeOrder(OrderRequest request) {
        try {
            orderRepository.save(new Order(request));
            paymentService.pay(request);
        } catch (Exception e) {
            log.error("주문 처리 실패", e);
            throw new OrderException("주문 처리 실패", e); // 예외 재던지기
        }
    }
}
```

### 올바른 코드 2: 수동 롤백 마킹

```java
@Service
public class OrderService {

    @Transactional
    public void placeOrder(OrderRequest request) {
        try {
            orderRepository.save(new Order(request));
            paymentService.pay(request);
        } catch (Exception e) {
            log.error("주문 처리 실패", e);
            // 트랜잭션을 롤백 전용으로 마킹
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
```

### 올바른 코드 3: rollbackFor 설정

```java
@Service
public class OrderService {

    @Transactional(rollbackFor = Exception.class)
    public void placeOrder(OrderRequest request) throws Exception {
        orderRepository.save(new Order(request));
        paymentService.pay(request); // checked exception이 발생해도 롤백
    }
}
```

---

## 문제 케이스 5: Checked Exception은 기본적으로 롤백 안 됨

### 발생 원인

Spring의 기본 롤백 정책:
- **RuntimeException (Unchecked Exception)** → 롤백 O
- **Error** → 롤백 O
- **Checked Exception** → 롤백 X (커밋)

### 잘못된 코드

```java
@Service
public class FileService {

    @Transactional // 기본 정책: Checked Exception은 롤백 안 됨
    public void processFile(Long id) throws IOException { // Checked Exception
        fileRepository.updateStatus(id, "PROCESSING");
        processLargeFile(id); // IOException 발생 시 rollback 안 됨!
        fileRepository.updateStatus(id, "DONE");
    }
}
```

```
실행 결과:
1. updateStatus("PROCESSING") 실행
2. processLargeFile() 에서 IOException 발생
3. 트랜잭션은 IOException을 Checked로 판단 → 커밋
4. DB에는 PROCESSING 상태로 남음 (DONE도 아니고 롤백도 안 됨)
```

### 올바른 코드

```java
@Service
public class FileService {

    // 방법 1: rollbackFor로 명시적 설정
    @Transactional(rollbackFor = Exception.class)
    public void processFile(Long id) throws IOException {
        fileRepository.updateStatus(id, "PROCESSING");
        processLargeFile(id);
        fileRepository.updateStatus(id, "DONE");
    }

    // 방법 2: Checked Exception을 RuntimeException으로 감싸기
    @Transactional
    public void processFile2(Long id) {
        try {
            fileRepository.updateStatus(id, "PROCESSING");
            processLargeFile(id);
            fileRepository.updateStatus(id, "DONE");
        } catch (IOException e) {
            throw new FileProcessingException("파일 처리 실패", e); // RuntimeException
        }
    }
}
```

### 롤백 정책 표

| 예외 타입 | 기본 롤백 여부 | rollbackFor 사용 시 |
|---|---|---|
| RuntimeException | O | - |
| Error | O | - |
| Checked Exception | X | rollbackFor = Exception.class 설정 시 O |
| noRollbackFor 설정 예외 | X | 강제 커밋 |

---

## 트랜잭션 전파(Propagation) 실수 사례

### REQUIRES_NEW가 예상대로 동작하지 않는 경우

```java
@Service
public class AuditService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog log) {
        auditLogRepository.save(log); // 별도 트랜잭션으로 커밋되길 기대
    }
}

@Service
@RequiredArgsConstructor
public class OrderService {
    private final AuditService auditService;

    @Transactional
    public void createOrder(OrderRequest request) {
        orderRepository.save(new Order(request));
        auditService.saveAuditLog(new AuditLog(request)); // 별도 트랜잭션 OK
        throw new RuntimeException("의도적 롤백");
        // 기대: Order는 롤백, AuditLog는 커밋 (REQUIRES_NEW이므로)
        // 결과: 올바르게 동작 (AuditLog는 이미 별도 트랜잭션으로 커밋됨)
    }
}
```

### 전파 옵션 비교

| 전파 옵션 | 기존 트랜잭션 있을 때 | 기존 트랜잭션 없을 때 |
|---|---|---|
| REQUIRED (기본값) | 기존 트랜잭션 참여 | 새 트랜잭션 생성 |
| REQUIRES_NEW | 기존 일시 중단, 새 트랜잭션 생성 | 새 트랜잭션 생성 |
| SUPPORTS | 기존 트랜잭션 참여 | 트랜잭션 없이 실행 |
| NOT_SUPPORTED | 기존 일시 중단, 트랜잭션 없이 실행 | 트랜잭션 없이 실행 |
| MANDATORY | 기존 트랜잭션 참여 | 예외 발생 |
| NEVER | 예외 발생 | 트랜잭션 없이 실행 |
| NESTED | 중첩 트랜잭션 생성 (savepoint) | 새 트랜잭션 생성 |

---

## 전체 케이스 요약 표

| 문제 케이스 | 원인 | 해결 방법 |
|---|---|---|
| Self-invocation | 내부 호출은 프록시 우회 | 클래스 분리 (강력 추천) |
| private 메서드 | 프록시가 private 가로채기 불가 | public으로 변경 |
| readOnly + 쓰기 | Dirty Checking 비활성화 | 메서드 분리, readOnly 제거 |
| 예외 삼키기 | 프록시가 예외 감지 못함 | 예외 재던지기 또는 setRollbackOnly |
| Checked Exception | 기본 정책이 커밋 | rollbackFor = Exception.class |

---

## 특이점 및 실무 팁

1. **@Transactional의 위치는 인터페이스보다 구현 클래스에**
   - 인터페이스에 선언해도 동작하지만, Spring 권장 방식은 구현 클래스에 선언

2. **@Transactional은 public 메서드에만 선언**
   - 컴파일 에러는 나지 않지만 효과 없음

3. **테스트에서의 @Transactional**
   - `@Test` + `@Transactional` → 테스트 종료 시 자동 롤백 (주의: 실제 커밋이 안 됨)
   - JPA 1차 캐시 때문에 실제 SELECT 쿼리 안 나갈 수 있음

4. **LazyInitializationException과 트랜잭션**
   - 트랜잭션 밖에서 Lazy 로딩 시도 시 발생
   - `@Transactional` 범위 안에서 접근하거나 EAGER로 변경 또는 fetch join 사용

5. **Spring Boot 3.x에서 self-autowire 기본 비활성화**
   - `spring.main.allow-circular-references=true` 설정 필요 (비권장)
   - 클래스 분리가 최선

```java
// 실무 베스트 프랙티스
@Service
@Transactional(readOnly = true) // 클래스 레벨: 기본적으로 읽기 전용
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 조회는 readOnly=true 상속
    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional // 쓰기 메서드만 readOnly 오버라이드
    public User updateUser(Long id, UpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.update(request);
        return user;
    }

    @Transactional(rollbackFor = Exception.class) // 모든 예외에 롤백
    public void processWithCheckedEx(Long id) throws Exception {
        // ...
    }
}
```
