## Spring MVC

### 개념

<div class="concept-box" markdown="1">

Spring MVC는 ==프론트 컨트롤러 패턴== 기반의 웹 프레임워크다. 모든 HTTP 요청이 `DispatcherServlet` 하나를 거쳐 적절한 컨트롤러로 분배된다.

</div>

### 요청 처리 흐름

```markdown
HTTP 요청
  → DispatcherServlet
    → HandlerMapping (어떤 컨트롤러?)
    → HandlerAdapter (어떻게 호출?)
    → Controller (비즈니스 로직)
    → ViewResolver (어떤 뷰?)
  → HTTP 응답
```

### 핵심 구성 요소

| 컴포넌트 | 역할 |
|----------|------|
| `DispatcherServlet` | 모든 요청을 받는 프론트 컨트롤러 |
| `HandlerMapping` | URL → 컨트롤러 매핑 |
| `HandlerAdapter` | 컨트롤러 실행 방식 처리 |
| `ViewResolver` | 뷰 이름 → 실제 뷰 변환 |
| `Model` | 뷰에 전달할 데이터 저장소 |

### 왜 쓰는가?

서블릿 기반 개발은 URL마다 서블릿 클래스를 만들어야 했다. DispatcherServlet이 이를 통합해 **공통 처리(인코딩, 보안, 로깅)를 중앙화**하고, 개발자는 비즈니스 로직만 집중할 수 있다.

### 기본 사용

```markdown
@Controller
public class MemberController {

    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "members/list"; // ViewResolver가 templates/members/list.html로 변환
    }

    @PostMapping("/members")
    public String create(@ModelAttribute MemberDto dto) {
        memberService.save(dto);
        return "redirect:/members";
    }
}
```

### 주요 어노테이션

| 어노테이션 | 설명 |
|-----------|------|
| `@Controller` | MVC 컨트롤러, 뷰 이름 반환 |
| `@RequestMapping` | URL 매핑 (클래스/메서드 레벨) |
| `@GetMapping`, `@PostMapping` 등 | HTTP 메서드별 단축 어노테이션 |
| `@PathVariable` | URL 경로 변수 (`/members/{id}`) |
| `@RequestParam` | 쿼리 파라미터 |
| `@ModelAttribute` | 폼 데이터 → 객체 바인딩 |

### 실무에서의 사용

!!! tip "실무 팁"
    REST API 위주인 실무에서는 `@Controller` + 뷰 반환보다 `@RestController`를 주로 쓴다. Spring MVC의 DispatcherServlet 흐름 자체는 REST API에서도 동일하게 작동한다. (→ [REST API](./restapi.md) 참고)

### 단점 / 주의할 점

<div class="warning-box" markdown="1">

| 상황 | 문제 | 해결 |
|------|------|------|
| `redirect:` vs `forward:` 혼동 | redirect는 새 요청, forward는 같은 요청 | POST 후 redirect(PRG 패턴) 사용 |
| Model에 민감 정보 담기 | 뷰에 노출될 수 있음 | DTO로 필요한 데이터만 전달 |
| 컨트롤러에 비즈니스 로직 | 테스트 어렵고 재사용 불가 | Service 레이어로 분리 |

</div>
