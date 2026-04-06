> [← 홈](/study/) · [Spring](/study/spring/basic/)

## REST API

### @Controller vs @RestController

| 구분 | `@Controller` | `@RestController` |
|------|--------------|-------------------|
| 반환 | 뷰 이름 (템플릿 렌더링) | 객체 (JSON 직렬화) |
| 내부 | `@Controller` | `@Controller` + `@ResponseBody` |
| 사용 | SSR(서버사이드 렌더링) | REST API |

### 왜 쓰는가?

프론트엔드(React, Vue 등)와 분리된 구조에서 서버는 JSON 데이터만 제공하면 된다. `@RestController`는 반환 객체를 자동으로 JSON으로 변환해 응답한다.

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

엔티티를 직접 노출하지 않고 DTO를 사용한다. 엔티티 변경이 API 스펙에 영향을 주지 않도록 격리한다.

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 엔티티 직접 반환 | 순환 참조, 민감 정보 노출, API 스펙 결합 | DTO로 변환 후 반환 |
| `ResponseEntity` 남용 | 모든 메서드에 `ResponseEntity` 사용 시 코드 복잡 | 단순 조회는 객체 직접 반환 |
| URL에 행위 표현 | RESTful하지 않음 | 명사 + HTTP 메서드로 표현 |
