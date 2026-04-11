## Validation

### 왜 쓰는가?

<div class="concept-box" markdown="1">

잘못된 요청이 서비스 레이어나 DB까지 도달하면 예외 처리가 복잡해지고 에러 원인 파악이 어렵다. 컨트롤러 진입 시점에 **요청 데이터의 유효성을 검증**해 조기에 차단한다.

</div>

### 의존성

```markdown
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### 주요 어노테이션

| 어노테이션 | 대상 | 설명 |
|-----------|------|------|
| `@NotNull` | 모든 타입 | null 불가 |
| `@NotBlank` | String | null, 빈 문자열, 공백만 있는 문자열 불가 |
| `@NotEmpty` | String, Collection | null, 빈 값 불가 |
| `@Size(min, max)` | String, Collection | 크기 범위 |
| `@Min`, `@Max` | 숫자 | 최소/최대값 |
| `@Email` | String | 이메일 형식 |
| `@Pattern(regexp)` | String | 정규식 패턴 |
| `@Positive` | 숫자 | 양수 |
| `@Past`, `@Future` | 날짜 | 과거/미래 |

### 기본 사용

```markdown
public class MemberCreateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이내여야 합니다")
    private String name;

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotNull
    @Min(value = 0, message = "나이는 0 이상이어야 합니다")
    private Integer age;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
             message = "비밀번호는 영문+숫자 8자 이상이어야 합니다")
    private String password;
}
```

```markdown
@RestController
public class MemberController {

    @PostMapping("/api/members")
    public ResponseEntity<?> create(@RequestBody @Valid MemberCreateRequest request) {
        // @Valid가 없으면 검증 안 함
        return ResponseEntity.ok(memberService.save(request));
    }
}
```

### @Validated — 그룹 검증 & 서비스 레이어 검증

```markdown
// 클래스 레벨에 @Validated 추가 시 메서드 파라미터 검증 가능
@Service
@Validated
public class MemberService {
    public void save(@Valid MemberCreateRequest request) { ... }
}
```

### Custom Validator

```markdown
// 어노테이션 정의
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "이미 사용 중인 이메일입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 검증 로직
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !memberRepository.existsByEmail(email);
    }
}
```

### 에러 응답 처리

검증 실패 시 `MethodArgumentNotValidException`이 발생한다. GlobalExceptionHandler에서 처리한다.

```markdown
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    List<String> errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_FAILED", errors));
}
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| `@Valid` 누락 | 검증이 전혀 실행 안 됨 | 컨트롤러 파라미터에 반드시 명시 |
| Custom Validator에서 DB 조회 | 성능 부담 | 서비스 레이어에서 별도 검증 고려 |
| `@NotNull` vs `@NotBlank` 혼동 | `@NotNull`은 빈 문자열 허용 | String에는 `@NotBlank` 사용 |
| 중첩 객체 검증 누락 | 내부 객체는 `@Valid` 추가 필요 | 중첩 DTO 필드에 `@Valid` 추가 |

---

## 내부 동작 원리

### Bean Validation이 뭔가?

> Bean Validation은 Java 표준 명세(JSR-380)다. 스프링이 만든 것이 아니라 **자바 생태계 표준**이고, Hibernate Validator가 대표적인 구현체다.
> `spring-boot-starter-validation`을 추가하면 Hibernate Validator가 자동으로 등록된다.

```text
표준 (명세)              구현체
Bean Validation API  →  Hibernate Validator
(javax.validation.*)     (실제 검증 로직)
```

### @Valid 처리 흐름 — 어디서 검증이 실행되나?

```text
컨트롤러 메서드 호출 직전
  ↓
RequestMappingHandlerAdapter
  → ArgumentResolver 목록에서 @RequestBody 처리할 것 선택
  → RequestResponseBodyMethodProcessor.resolveArgument()
       ① HttpMessageConverter로 JSON → 자바 객체 변환
       ② @Valid 어노테이션 감지
       ③ SmartValidator.validate(object, bindingResult) 호출
            → Hibernate Validator가 각 필드의 제약 어노테이션 검사
            → @NotBlank: 값이 blank인가? → 위반이면 ConstraintViolation 추가
            → @Email: 이메일 형식인가? → 위반이면 ConstraintViolation 추가
       ④ ConstraintViolation이 하나라도 있으면
            → BindingResult에 오류 정보 기록
            → MethodArgumentNotValidException 던짐
  ↓
GlobalExceptionHandler.handleValidation() 에서 처리
```

### ConstraintValidator 구현 원리

> `@NotBlank`, `@Email` 같은 어노테이션 뒤에는 실제 검증 로직을 가진 `ConstraintValidator` 구현체가 있다.

```java
// @NotBlank 어노테이션 선언 (스프링 내부)
@Constraint(validatedBy = NotBlankValidator.class)  // 실제 검증 클래스 연결
public @interface NotBlank { ... }

// NotBlank 실제 검증 로직 (Hibernate Validator 내부)
public class NotBlankValidator implements ConstraintValidator<NotBlank, CharSequence> {
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.toString().strip().length() > 0;  // 공백 제거 후 길이 확인
    }
}
```

커스텀 `@UniqueEmail`을 만들 때 `ConstraintValidator`를 구현하는 것이 이 패턴을 따르는 것이다.

### @Valid vs @Validated

| | `@Valid` | `@Validated` |
|--|---------|-------------|
| 출처 | Java 표준 (javax.validation) | Spring 전용 |
| 그룹 검증 | 불가 | 가능 (`groups` 파라미터 활용) |
| 중첩 객체 검증 | 가능 | 가능 |
| 메서드 파라미터 검증 | 컨트롤러만 | 서비스 등 모든 스프링 빈 |

```java
// @Validated로 서비스 레이어 검증 (AOP 기반)
@Service
@Validated  // 클래스 레벨에 선언
public class MemberService {

    public void save(@Valid MemberCreateRequest request) {
        // 컨트롤러를 거치지 않고 서비스 직접 호출해도 검증 실행됨
        memberRepository.save(request.toEntity());
    }
}
```

<div class="tip-box" markdown="1">

**@Validated는 AOP 기반**: `@Validated`가 붙은 빈의 메서드 호출 시 스프링이 AOP 프록시를 통해 `MethodValidationInterceptor`를 실행한다. 따라서 같은 클래스 내 Self-Invocation에서는 검증이 실행되지 않는다.

</div>
