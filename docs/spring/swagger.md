## API 문서화 (Swagger / Springdoc)

### 왜 쓰는가?

프론트엔드, 외부 팀, QA가 API를 사용하려면 문서가 필요하다. 수동으로 문서를 작성하면 코드와 문서가 불일치하는 문제가 생긴다. Swagger는 **코드에서 자동으로 API 문서를 생성**한다.

### 의존성 (Springdoc OpenAPI)

```markdown
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
```

### 기본 설정

```markdown
# application.yml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: alpha
```

실행 후 `http://localhost:8080/swagger-ui.html`에서 확인.

### 전역 설정

```markdown
@OpenAPIDefinition(
    info = @Info(
        title = "My API",
        description = "서비스 API 문서",
        version = "v1"
    ),
    servers = @Server(url = "http://localhost:8080")
)
@Configuration
public class SwaggerConfig { }
```

### 컨트롤러 문서화

```markdown
@Tag(name = "회원", description = "회원 관련 API")
@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Operation(
        summary = "회원 조회",
        description = "ID로 특정 회원 정보를 조회합니다"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @GetMapping("/{id}")
    public MemberResponse find(
        @Parameter(description = "회원 ID") @PathVariable Long id
    ) {
        return memberService.findById(id);
    }
}
```

### 운영 환경에서 비활성화

```markdown
# application-prod.yml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

### Swagger vs Spring REST Docs

| 구분 | Swagger (Springdoc) | Spring REST Docs |
|------|--------------------|--------------------|
| 문서 생성 방식 | 어노테이션 기반 자동 생성 | 테스트 기반 생성 |
| 정확도 | 코드와 불일치 가능 | 테스트 통과 시 정확 |
| UI 제공 | O (Swagger UI) | 별도 설정 필요 |
| 설정 복잡도 | 낮음 | 높음 |
| 실무 선호 | 빠른 개발, 스타트업 | 정확성 중시, 공개 API |

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 운영 환경 Swagger 노출 | 내부 API 구조 공개 | Profile로 운영 비활성화 |
| 어노테이션 과다 | 컨트롤러 코드 가독성 저하 | 핵심 API에만 상세 문서화 |
| 문서와 실제 동작 불일치 | 어노테이션 미업데이트 | Spring REST Docs 고려 |
