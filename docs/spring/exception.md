## Exception Handler

### 왜 쓰는가?

<div class="concept-box" markdown="1">

컨트롤러마다 예외를 처리하면 중복 코드가 생기고 에러 응답 형식이 일관되지 않는다. ==@RestControllerAdvice==로 **전역 예외 처리를 중앙화**하고 일관된 에러 응답을 제공한다.

</div>

### ErrorCode 설계

에러를 Enum으로 관리하면 코드와 메시지를 한 곳에서 관리할 수 있다.

```markdown
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "잘못된 입력값입니다"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다"),

    // 회원
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다"),
    DUPLICATE_EMAIL(409, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다"),

    // 주문
    ORDER_NOT_FOUND(404, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다"),
    INSUFFICIENT_STOCK(400, "INSUFFICIENT_STOCK", "재고가 부족합니다");

    private final int status;
    private final String code;
    private final String message;
}
```

### 커스텀 예외

```markdown
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

// 구체적인 예외
public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
```

### 표준 에러 응답

```markdown
@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    private final List<String> errors;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .status(errorCode.getStatus())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
    }
}
```

### GlobalExceptionHandler

```markdown
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException: {}", e.getMessage());
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode));
    }

    // 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();

        return ResponseEntity.badRequest().body(
            ErrorResponse.builder()
                .status(400)
                .code("INVALID_INPUT_VALUE")
                .message("입력값이 올바르지 않습니다")
                .errors(errors)
                .build()
        );
    }

    // 그 외 예외 (예상치 못한 오류)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
            .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
```

### 서비스에서 사용

```markdown
public Member findById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(MemberNotFoundException::new);
}
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 예외 스택 트레이스를 응답에 포함 | 내부 구조 노출, 보안 위험 | 운영에서는 로그에만 기록 |
| `@ExceptionHandler` 순서 충돌 | 상위 예외가 먼저 잡힘 | 구체적인 예외를 위에 배치 |
| checked exception 사용 | 메서드 시그니처에 throws 전파 | RuntimeException 상속으로 통일 |
| 예외 로그 누락 | 장애 원인 파악 불가 | `log.error`로 스택 트레이스 기록 |

---

## 내부 동작 원리

### 예외가 터지면 어디서 잡히나?

```
컨트롤러 메서드에서 예외 발생
  ↓
DispatcherServlet.processDispatchResult()
  → 등록된 HandlerExceptionResolver 목록 순회 (우선순위 순)
  ↓
  1. ExceptionHandlerExceptionResolver       ← @ExceptionHandler 처리 (최우선)
  2. ResponseStatusExceptionResolver         ← @ResponseStatus 처리
  3. DefaultHandlerExceptionResolver         ← 스프링 기본 예외 처리 (400, 405 등)
  ↓
  해결된 경우 → 해당 응답 반환
  해결 안 된 경우 → 서블릿 컨테이너로 예외 전파 → 500 Internal Server Error
```

### ExceptionHandlerExceptionResolver — @ExceptionHandler 탐색 순서

```
예외 발생 (MemberNotFoundException)
  ↓
ExceptionHandlerExceptionResolver.resolveException()
  ↓
  ① 현재 컨트롤러 클래스에서 @ExceptionHandler 탐색
       → MemberController에 @ExceptionHandler(MemberNotFoundException.class)가 있으면 실행
  ② 없으면 → @RestControllerAdvice / @ControllerAdvice 클래스에서 탐색
       → GlobalExceptionHandler.handleBusiness() 찾아서 실행
  ③ 없으면 → 다음 HandlerExceptionResolver로 넘김
```

```java
// @ExceptionHandler의 예외 매칭 규칙
// 가장 구체적인 타입이 먼저 매칭됨
@ExceptionHandler(MemberNotFoundException.class)  // 구체 예외 → 먼저 잡힘
public ResponseEntity<?> handleMember(MemberNotFoundException e) { ... }

@ExceptionHandler(BusinessException.class)  // 부모 예외 → 나중에 잡힘
public ResponseEntity<?> handleBusiness(BusinessException e) { ... }

@ExceptionHandler(Exception.class)  // 최상위 예외 → 마지막 안전망
public ResponseEntity<?> handleAll(Exception e) { ... }
```

### @RestControllerAdvice 내부 — 왜 모든 컨트롤러에 적용되나?

> `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`
> `@ControllerAdvice`는 스프링 시작 시점에 **모든 컨트롤러에 적용되는 AOP 형태의 글로벌 설정**이다.

```
스프링 컨테이너 시작
  → @ControllerAdvice 빈 스캔
  → ExceptionHandlerExceptionResolver에 등록
  → 이후 모든 컨트롤러에서 예외 발생 시 여기를 먼저 참조
```

### 예외 처리 전체 흐름 — 실제 요청부터 응답까지

```
POST /api/members  (이메일 중복)
  ↓
MemberController.create()
  ↓
MemberService.save() → memberRepository.existsByEmail() → true
  → throw new DuplicateEmailException()  (BusinessException 상속)
  ↓ 예외 전파 (컨트롤러까지 올라옴)
DispatcherServlet
  ↓
ExceptionHandlerExceptionResolver
  → GlobalExceptionHandler.handleBusiness() 매칭
  → ErrorCode.DUPLICATE_EMAIL (409, "이미 사용 중인 이메일")
  → ResponseEntity(status=409, body=ErrorResponse) 반환
  ↓
HttpMessageConverter → JSON 직렬화
  ↓
HTTP 409 응답:
{
  "status": 409,
  "code": "DUPLICATE_EMAIL",
  "message": "이미 사용 중인 이메일입니다"
}
```
