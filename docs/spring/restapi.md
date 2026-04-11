## REST API

### @Controller vs @RestController

| 구분 | `@Controller` | `@RestController` |
|------|--------------|-------------------|
| 반환 | 뷰 이름 (템플릿 렌더링) | 객체 (JSON 직렬화) |
| 내부 | `@Controller` | `@Controller` + `@ResponseBody` |
| 사용 | SSR(서버사이드 렌더링) | REST API |

### 왜 쓰는가?

<div class="concept-box" markdown="1">

프론트엔드(React, Vue 등)와 분리된 구조에서 서버는 JSON 데이터만 제공하면 된다. `@RestController`는 반환 객체를 자동으로 JSON으로 변환해 응답한다.

</div>

### 기본 CRUD API

```markdown
@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    @GetMapping
    public List<MemberResponse> list() {
        return memberService.findAll();
    }

    @GetMapping("/{id}")
    public MemberResponse find(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(@RequestBody @Valid MemberCreateRequest request) {
        MemberResponse response = memberService.save(request);
        return ResponseEntity.created(URI.create("/api/members/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @RequestBody @Valid MemberUpdateRequest request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### ResponseEntity

응답 상태 코드와 헤더를 직접 제어할 때 사용한다.

```markdown
// 201 Created + Location 헤더
ResponseEntity.created(URI.create("/api/members/1")).body(response);

// 200 OK
ResponseEntity.ok(response);

// 204 No Content
ResponseEntity.noContent().build();

// 400 Bad Request
ResponseEntity.badRequest().body(errorResponse);
```

### HTTP 메서드와 의미

| 메서드 | 의미 | 멱등성 | 바디 |
|--------|------|--------|------|
| `GET` | 조회 | O | X |
| `POST` | 생성 | X | O |
| `PUT` | 전체 수정 | O | O |
| `PATCH` | 부분 수정 | △ | O |
| `DELETE` | 삭제 | O | X |

### API 설계 원칙

```markdown
// Good: 명사, 계층 구조
GET    /api/orders
GET    /api/orders/{orderId}
POST   /api/orders
DELETE /api/orders/{orderId}
GET    /api/orders/{orderId}/items

// Bad: 동사 사용
GET    /api/getOrders
POST   /api/createOrder
POST   /api/deleteOrder
```

### 실무 패턴 — DTO 분리

```markdown
// 요청 DTO (외부 입력)
public class MemberCreateRequest {
    @NotBlank private String name;
    @Email    private String email;
}

// 응답 DTO (외부 노출)
public class MemberResponse {
    private Long   id;
    private String name;
    // 비밀번호 등 민감 정보 제외
}
```

<div class="success-box" markdown="1">

엔티티를 직접 노출하지 않고 DTO를 사용한다. 엔티티 변경이 API 스펙에 영향을 주지 않도록 격리한다.

</div>

### 단점 / 주의할 점

<div class="warning-box" markdown="1">

| 상황 | 문제 | 해결 |
|------|------|------|
| 엔티티 직접 반환 | 순환 참조, 민감 정보 노출, API 스펙 결합 | DTO로 변환 후 반환 |
| `ResponseEntity` 남용 | 모든 메서드에 `ResponseEntity` 사용 시 코드 복잡 | 단순 조회는 객체 직접 반환 |
| URL에 행위 표현 | RESTful하지 않음 | 명사 + HTTP 메서드로 표현 |

</div>

---

## 내부 동작 원리

### HttpMessageConverter — JSON 변환의 실체

> `@RestController`에서 객체를 반환하면 어떻게 JSON 문자열로 바뀌는 걸까?
> 그 역할을 하는 것이 **HttpMessageConverter**다. "HTTP 메시지(요청/응답 바디)와 자바 객체 사이를 변환해주는 컨버터"라는 뜻이다.

```java
[요청 처리 — @RequestBody]
클라이언트 → JSON 문자열 전송
  → RequestResponseBodyMethodProcessor
  → 등록된 HttpMessageConverter 목록 순회
  → Content-Type: application/json이면 MappingJackson2HttpMessageConverter 선택
  → converter.read(MemberCreateRequest.class, request)
  → Jackson ObjectMapper.readValue(json, MemberCreateRequest.class)
  → 자바 객체 생성 → 컨트롤러 메서드 파라미터로 전달

[응답 처리 — @ResponseBody / @RestController]
컨트롤러 메서드 반환값 (MemberResponse 객체)
  → RequestResponseBodyMethodProcessor
  → 클라이언트 Accept: application/json 확인
  → MappingJackson2HttpMessageConverter 선택
  → converter.write(memberResponse, response)
  → Jackson ObjectMapper.writeValueAsString(memberResponse)
  → JSON 문자열 → HTTP 응답 바디에 기록
```

### 컨버터 선택 기준 — Content Negotiation

클라이언트 요청의 `Content-Type`(요청 바디 형식)과 `Accept`(원하는 응답 형식) 헤더를 보고 컨버터를 선택한다.

| 상황 | 헤더 | 선택되는 컨버터 |
|------|------|---------------|
| JSON 요청 파싱 | `Content-Type: application/json` | `MappingJackson2HttpMessageConverter` |
| JSON 응답 생성 | `Accept: application/json` | `MappingJackson2HttpMessageConverter` |
| 문자열 응답 | `Accept: text/plain` | `StringHttpMessageConverter` |
| 폼 데이터 | `Content-Type: application/x-www-form-urlencoded` | `FormHttpMessageConverter` |

### Jackson ObjectMapper — 직렬화 규칙

`MappingJackson2HttpMessageConverter`는 내부적으로 Jackson의 **ObjectMapper**를 사용한다.

```java
직렬화 (자바 → JSON):
  MemberResponse {id=1, name="김철수", email="kim@test.com"}
  ObjectMapper.writeValueAsString()
    → 리플렉션으로 필드/getter 탐색
    → {"id":1,"name":"김철수","email":"kim@test.com"}

역직렬화 (JSON → 자바):
  {"name":"김철수","email":"kim@test.com"}
  ObjectMapper.readValue()
    → 기본 생성자로 객체 생성
    → 리플렉션으로 필드 값 주입
    → MemberCreateRequest {name="김철수", email="kim@test.com"}
```

<div class="warning-box" markdown="1">

**역직렬화 주의**: Jackson이 JSON → 자바 객체로 변환할 때 **기본 생성자(no-args constructor)**가 필요하다.
`@RequestBody`로 받는 DTO에 기본 생성자가 없으면 `InvalidDefinitionException` 발생.

```java
// 문제: Lombok @AllArgsConstructor만 있고 기본 생성자 없음
@AllArgsConstructor
public class MemberCreateRequest {
    private String name;
    private String email;
}

// 해결: @NoArgsConstructor 추가
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateRequest {
    private String name;
    private String email;
}
```

</div>

### @RequestBody vs @ModelAttribute 차이

| | `@RequestBody` | `@ModelAttribute` |
|--|---------------|------------------|
| 데이터 위치 | HTTP 바디 (JSON) | URL 파라미터 / 폼 데이터 |
| 변환 방식 | HttpMessageConverter (Jackson) | DataBinder (setter/생성자) |
| Content-Type | `application/json` | `application/x-www-form-urlencoded` |
| 주 용도 | REST API | HTML 폼 제출 |
