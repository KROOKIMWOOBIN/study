> [← 홈](/) · [실무](/실무/실무/)

# REST API 설계

---

## 왜 공부하는가?

REST API는 현대 웹 서비스에서 가장 널리 쓰이는 인터페이스 방식이다.
잘못 설계된 API는 클라이언트와의 혼란, 유지보수 어려움, 확장성 문제를 야기한다.
올바른 설계 원칙을 알면 **일관되고, 직관적이고, 확장 가능한 API**를 만들 수 있다.

---

## REST란? RESTful이란?

**REST (Representational State Transfer)**
Roy Fielding이 2000년 박사 논문에서 정의한 아키텍처 스타일.
HTTP 프로토콜을 기반으로 자원(Resource)을 URI로 표현하고, HTTP 메서드로 행위를 표현한다.

**RESTful**
REST 제약 조건을 잘 준수하는 API를 RESTful API라고 부른다.
단순히 HTTP를 쓴다고 RESTful이 아니라, 아래 6가지 제약 조건을 만족해야 한다.

---

## REST 6가지 제약 조건

| 제약 조건 | 설명 |
|---|---|
| 1. 클라이언트-서버 분리 | UI(클라이언트)와 데이터 저장소(서버)를 분리해 독립적으로 발전 가능 |
| 2. 무상태 (Stateless) | 서버는 클라이언트의 상태를 저장하지 않음. 요청마다 모든 정보를 담아야 함 |
| 3. 캐시 가능 (Cacheable) | HTTP 캐싱 메커니즘 활용 가능 (Cache-Control, ETag 등) |
| 4. 계층형 시스템 | 클라이언트는 중간 서버(프록시, 게이트웨이) 존재를 알 수 없어야 함 |
| 5. 균일한 인터페이스 | 자원 식별, 표현을 통한 조작, 자기 기술적 메시지, HATEOAS |
| 6. 코드 온 디맨드 (선택) | 서버가 클라이언트에 실행 코드(JS 등)를 전달할 수 있음 |

---

## HTTP 메서드 사용 원칙

| HTTP 메서드 | 의미 | 멱등성 | 안전성 | 사용 예 |
|---|---|---|---|---|
| GET | 자원 조회 | O | O | GET /users/1 |
| POST | 자원 생성 | X | X | POST /users |
| PUT | 자원 전체 교체 | O | X | PUT /users/1 |
| PATCH | 자원 부분 수정 | △ | X | PATCH /users/1 |
| DELETE | 자원 삭제 | O | X | DELETE /users/1 |

**멱등성(Idempotent):** 같은 요청을 여러 번 해도 결과가 동일
**안전성(Safe):** 서버 상태를 변경하지 않음

```java
// Spring MVC에서의 HTTP 메서드 매핑
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")           // GET /api/v1/users/1
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PostMapping                   // POST /api/v1/users
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        URI location = URI.create("/api/v1/users/" + response.getId());
        return ResponseEntity.created(location).body(response); // 201 Created
    }

    @PutMapping("/{id}")           // PUT /api/v1/users/1 (전체 교체)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}")         // PATCH /api/v1/users/1 (부분 수정)
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable Long id,
            @RequestBody @Valid PatchUserRequest request) {
        return ResponseEntity.ok(userService.patchUser(id, request));
    }

    @DeleteMapping("/{id}")        // DELETE /api/v1/users/1
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
```

---

## URI 설계 원칙

### 기본 원칙

```
좋은 URI 설계:
/api/v1/users               → 사용자 컬렉션
/api/v1/users/123           → ID가 123인 사용자
/api/v1/users/123/orders    → 사용자 123의 주문 목록
/api/v1/orders/456/items    → 주문 456의 아이템 목록
```

### URI 설계 규칙 표

| 규칙 | 좋은 예 | 나쁜 예 |
|---|---|---|
| 명사 사용 (복수형) | /users, /orders | /getUser, /createOrder |
| 소문자 사용 | /user-profiles | /UserProfiles |
| 하이픈(-) 사용 | /user-profiles | /user_profiles |
| 계층 구조 표현 | /users/1/orders | /getUserOrders?userId=1 |
| 행위를 URI에 포함 금지 | DELETE /users/1 | GET /deleteUser/1 |
| 확장자 포함 금지 | /users/1 | /users/1.json |
| 버전 관리 | /api/v1/users | /users (버전 없음) |

### 예외적으로 동사 허용: 컨트롤 자원

```
/api/v1/orders/123/cancel     → 주문 취소 (상태 변경 동작)
/api/v1/emails/send           → 이메일 발송 (RPC 스타일 동작)
/api/v1/payments/123/refund   → 환불 처리
```

### 버전 관리 전략

```
방법 1: URI 경로 버전 (가장 일반적)
GET /api/v1/users
GET /api/v2/users

방법 2: 쿼리 파라미터 버전
GET /api/users?version=1

방법 3: 요청 헤더 버전
GET /api/users
Accept: application/vnd.myapp.v1+json

방법 4: 커스텀 헤더
GET /api/users
X-API-Version: 1
```

```java
// URI 경로 버전 관리 (Spring)
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller { ... }

@RestController
@RequestMapping("/api/v2/users")
public class UserV2Controller { ... }
```

---

## HTTP 상태 코드 올바른 사용

### 2xx 성공

| 코드 | 의미 | 사용 시점 |
|---|---|---|
| 200 OK | 요청 성공 | GET, PUT, PATCH 성공 |
| 201 Created | 자원 생성 성공 | POST로 자원 생성 성공 |
| 204 No Content | 성공, 응답 본문 없음 | DELETE 성공, 업데이트 후 본문 불필요 |

### 4xx 클라이언트 오류

| 코드 | 의미 | 사용 시점 |
|---|---|---|
| 400 Bad Request | 잘못된 요청 | 파라미터 형식 오류, 필수값 누락 |
| 401 Unauthorized | 인증 필요 | 로그인 필요 (토큰 없음/만료) |
| 403 Forbidden | 권한 없음 | 로그인은 됐지만 접근 권한 없음 |
| 404 Not Found | 자원 없음 | 존재하지 않는 ID 조회 |
| 409 Conflict | 충돌 | 이미 존재하는 이메일로 가입 시도 |
| 422 Unprocessable Entity | 처리 불가 | 비즈니스 로직 검증 실패 |
| 429 Too Many Requests | 요청 초과 | Rate Limit 초과 |

### 5xx 서버 오류

| 코드 | 의미 | 사용 시점 |
|---|---|---|
| 500 Internal Server Error | 서버 내부 오류 | 예상치 못한 예외 |
| 502 Bad Gateway | 게이트웨이 오류 | 상위 서버 응답 오류 |
| 503 Service Unavailable | 서비스 불가 | 서버 점검 중, 과부하 |
| 504 Gateway Timeout | 게이트웨이 타임아웃 | 상위 서버 응답 지연 |

```java
// 상태 코드 올바른 사용 예시
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409
                .body(new ErrorResponse("이미 사용 중인 이메일입니다."));
        }
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED) // 201
            .location(URI.create("/api/v1/users/" + response.getId()))
            .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userService.findUser(id)
            .map(ResponseEntity::ok) // 200
            .orElse(ResponseEntity.notFound().build()); // 404
    }
}
```

---

## 멱등성과 안전성 (Safe vs Idempotent)

```
          안전(Safe)    멱등(Idempotent)
GET         O               O
HEAD        O               O
OPTIONS     O               O
POST        X               X
PUT         X               O
DELETE      X               O
PATCH       X               △ (구현에 따라)
```

**실무 중요성:**
- 멱등 메서드는 **재시도 로직에 안전하게 사용** 가능
- POST는 멱등이 아니므로 재시도 시 중복 생성 위험 → 클라이언트에서 idempotency key 사용 고려

```java
// POST 멱등 처리: Idempotency-Key 헤더 사용
@PostMapping("/payments")
public ResponseEntity<PaymentResponse> createPayment(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody CreatePaymentRequest request) {
    // 동일 key로 재시도 시 기존 결과 반환
    return paymentService.createPaymentIdempotent(idempotencyKey, request);
}
```

---

## API 응답 구조 설계 (표준 응답 포맷)

### 공통 응답 포맷 설계

```java
// 공통 응답 래퍼
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;
    private final LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

```json
// 성공 응답
{
  "success": true,
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "hong@example.com"
  },
  "timestamp": "2026-03-21T10:00:00"
}

// 오류 응답
{
  "success": false,
  "message": "사용자를 찾을 수 없습니다.",
  "errorCode": "USER_NOT_FOUND",
  "timestamp": "2026-03-21T10:00:00"
}
```

```java
// 글로벌 예외 핸들러
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidation(
            MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("입력값 검증 실패", "VALIDATION_FAILED"));
    }
}
```

---

## 페이지네이션 방법

### Offset 기반 페이지네이션

```java
// 요청: GET /api/v1/posts?page=0&size=10&sort=createdAt,desc
@GetMapping("/posts")
public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable) {
    Page<PostResponse> posts = postService.getPosts(pageable);
    return ResponseEntity.ok(ApiResponse.success(posts));
}
```

```json
// 응답
{
  "success": true,
  "data": {
    "content": [...],
    "totalElements": 1000,
    "totalPages": 100,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

**Offset 장점:** 특정 페이지로 바로 이동 가능, 구현 쉬움
**Offset 단점:** 데이터가 많아질수록 OFFSET 쿼리 성능 저하, 페이지 건너뜀 현상

### Cursor 기반 페이지네이션

```java
// 요청: GET /api/v1/posts?cursor=xxx&size=10
@GetMapping("/posts")
public ResponseEntity<ApiResponse<CursorPage<PostResponse>>> getPostsByCursor(
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int size) {
    CursorPage<PostResponse> posts = postService.getPostsByCursor(cursor, size);
    return ResponseEntity.ok(ApiResponse.success(posts));
}
```

```json
// 응답
{
  "success": true,
  "data": {
    "content": [...],
    "nextCursor": "eyJpZCI6MTAwfQ==",
    "hasNext": true
  }
}
```

**Cursor 장점:** 대용량 데이터에서 성능 좋음, 실시간 데이터에 적합
**Cursor 단점:** 특정 페이지 바로 이동 불가, 구현 복잡

### 페이지네이션 방법 비교

| 항목 | Offset | Cursor |
|---|---|---|
| 특정 페이지 이동 | O | X |
| 대용량 성능 | 나쁨 | 좋음 |
| 실시간 데이터 | 불안정 (데이터 추가/삭제 시 중복/누락) | 안정적 |
| 구현 복잡도 | 낮음 | 높음 |
| 적합한 상황 | 관리자 페이지, 검색 | 무한 스크롤, SNS 피드 |

---

## HATEOAS

**HATEOAS (Hypermedia As The Engine Of Application State)**
응답에 관련 리소스의 링크를 포함시켜 클라이언트가 API 구조를 발견할 수 있게 한다.

```json
// HATEOAS 적용 응답 예시
{
  "id": 1,
  "name": "홍길동",
  "email": "hong@example.com",
  "_links": {
    "self": { "href": "/api/v1/users/1" },
    "update": { "href": "/api/v1/users/1", "method": "PUT" },
    "delete": { "href": "/api/v1/users/1", "method": "DELETE" },
    "orders": { "href": "/api/v1/users/1/orders" }
  }
}
```

```java
// Spring HATEOAS 적용
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    public EntityModel<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.findUser(id);
        return EntityModel.of(user,
            linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
            linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete")
        );
    }
}
```

**실무에서 HATEOAS 사용 여부**
- 완전한 HATEOAS 구현은 복잡도가 높아 실무에서 잘 사용하지 않음
- 일부 링크 정보만 포함하는 방식으로 절충하는 경우가 많음

---

## 실무에서 자주 하는 실수

### 실수 1: GET 요청에 Body 사용

```
나쁜 예: GET /users (body에 검색 조건)
좋은 예: GET /users?name=홍길동&email=hong@example.com
```

### 실수 2: 동사를 URI에 포함

```
나쁜 예: POST /createUser
        GET /getUserById/1
        POST /deleteUser/1
좋은 예: POST /users
        GET /users/1
        DELETE /users/1
```

### 실수 3: 상태 코드 무시하고 항상 200 반환

```json
// 나쁜 예: 오류도 200으로 반환
HTTP/1.1 200 OK
{
  "success": false,
  "error": "사용자를 찾을 수 없습니다."
}

// 좋은 예: 올바른 상태 코드 사용
HTTP/1.1 404 Not Found
{
  "success": false,
  "message": "사용자를 찾을 수 없습니다.",
  "errorCode": "USER_NOT_FOUND"
}
```

### 실수 4: 필터/검색에 POST 사용

```
나쁜 예: POST /users/search (body에 검색 조건)
좋은 예: GET /users?name=홍&age=20&sort=createdAt,desc
예외:    검색 조건이 매우 복잡하거나 민감한 경우 POST /users/search 허용
```

### 실수 5: 버전 관리 없이 API 수정

```
하위 호환성이 깨지는 변경사항 (Breaking Change):
- 응답 필드 제거
- 필드명 변경
- 필수 파라미터 추가
- 상태 코드 변경

이런 경우 반드시 새 버전(/v2) 으로 분리해야 함
```

### 실수 6: 인증 에러 혼동 (401 vs 403)

```
401 Unauthorized: 인증이 안 됨 (로그인 안 됨, 토큰 없음/만료)
403 Forbidden:    인증은 됐지만 권한 없음 (일반 사용자가 어드민 API 접근)
```

---

## 어떨 때 많이 쓰는가?

| 상황 | 적용 패턴 |
|---|---|
| 모바일 앱 백엔드 | 표준 REST + JSON 응답, 버전 관리 필수 |
| MSA 내부 통신 | REST 또는 gRPC 선택 |
| 공개 API (Open API) | HATEOAS + 엄격한 버전 관리 |
| 관리자 API | Offset 페이지네이션, 상세한 에러 메시지 |
| SNS 피드 API | Cursor 페이지네이션 |
| 파일 업로드 | multipart/form-data (POST) |
| 실시간 데이터 | WebSocket 또는 SSE (REST 한계) |
