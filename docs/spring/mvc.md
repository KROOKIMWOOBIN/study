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

---

## 내부 동작 원리

### DispatcherServlet이 뭔가?

> 서블릿(Servlet)은 HTTP 요청을 처리하는 Java 클래스다. 예전에는 URL마다 서블릿 클래스를 따로 만들었다(`/login` → `LoginServlet`, `/members` → `MemberServlet`...). DispatcherServlet은 이 모든 요청을 **혼자서 다 받아서** 적절한 곳으로 넘겨주는 **중앙 접수창구**다.

```text
클라이언트 HTTP 요청
      ↓
  Servlet Container (Tomcat)
      ↓ "모든 경로(/*) 담당"
  DispatcherServlet        ← 딱 1개, 모든 요청의 입구
      ↓
  각 컨트롤러 메서드로 위임
```

### DispatcherServlet.doDispatch() 상세 흐름

실제로 `DispatcherServlet`은 `doDispatch()` 메서드 하나로 모든 요청을 처리한다.

```text
① HandlerMapping 탐색
   → 등록된 HandlerMapping 목록 순회
   → URL, HTTP 메서드, 헤더 조건과 일치하는 @RequestMapping 메서드를 찾음
   → "GET /members" → MemberController.list() 찾아냄
   → 결과: HandlerExecutionChain (컨트롤러 메서드 + 인터셉터 목록)

② HandlerAdapter 선택
   → 찾은 컨트롤러 메서드를 "어떻게 호출할지" 담당하는 어댑터를 선택
   → @RequestMapping 메서드라면 RequestMappingHandlerAdapter 선택

③ 인터셉터 preHandle() 실행
   → 등록된 HandlerInterceptor들의 preHandle() 순서대로 실행
   → false 반환하면 여기서 요청 중단

④ HandlerAdapter.handle() — 실제 컨트롤러 실행
   → ArgumentResolver로 메서드 파라미터 준비 (자세한 설명 아래)
   → 컨트롤러 메서드 호출
   → ReturnValueHandler로 반환값 처리

⑤ ViewResolver (뷰 렌더링인 경우)
   → 컨트롤러가 "members/list" 문자열 반환
   → ViewResolver가 "templates/members/list.html" 실제 경로로 변환
   → Thymeleaf 등으로 HTML 렌더링

⑥ 인터셉터 postHandle() / afterCompletion() 실행

⑦ HTTP 응답 전송
```

### ArgumentResolver — 파라미터는 어떻게 채워지나?

컨트롤러 메서드에 `@PathVariable`, `@RequestParam`, `@RequestBody`, `Model` 등 다양한 파라미터가 붙는다. `RequestMappingHandlerAdapter`가 호출 전에 각 파라미터를 **자동으로 채워주는** 역할을 한다.

```java
// 이 메서드를 호출하려면 id, model 파라미터가 필요하다
@GetMapping("/members/{id}")
public String find(@PathVariable Long id, Model model) { ... }
```

```text
RequestMappingHandlerAdapter
  → 파라미터 목록 검사
  → @PathVariable Long id
       → PathVariableMethodArgumentResolver
       → URL에서 {id} 부분 추출 → "42" → Long으로 변환 → 42L
  → Model model
       → ModelMethodProcessor
       → 현재 요청의 Model 객체를 그대로 주입
  → 모든 파라미터 준비 완료 → 메서드 호출
```

| ArgumentResolver | 처리하는 파라미터 |
|-----------------|----------------|
| `PathVariableMethodArgumentResolver` | `@PathVariable` |
| `RequestParamMethodArgumentResolver` | `@RequestParam` |
| `RequestResponseBodyMethodProcessor` | `@RequestBody` (JSON → 객체 변환) |
| `ServletModelAttributeMethodProcessor` | `@ModelAttribute` |
| `ModelMethodProcessor` | `Model` |
| `HttpServletRequestMethodArgumentResolver` | `HttpServletRequest` |

### HandlerMapping 내부 — URL을 어떻게 찾나?

`RequestMappingHandlerMapping`은 애플리케이션 시작 시점에 모든 `@Controller` 클래스를 스캔해 `@RequestMapping` 정보를 **Map에 미리 캐싱**해 둔다.

```text
애플리케이션 시작
  → @Controller 빈들 스캔
  → 각 메서드의 @RequestMapping, @GetMapping 등 정보 읽음
  → Map<RequestMappingInfo, HandlerMethod>에 저장

요청 처리 시 (매번)
  → Map에서 URL + HTTP 메서드로 조회 → O(1) 성능
  → HandlerMethod (컨트롤러 + 메서드 정보) 반환
```

<div class="tip-box" markdown="1">

**@RestController는 뭐가 다른가?**
`@RestController` = `@Controller` + `@ResponseBody`. `@ResponseBody`가 있으면 뷰 이름 반환 대신 반환 객체를 **HTTP 응답 바디에 직접 직렬화**(JSON 변환)한다. DispatcherServlet 흐름은 동일하지만 ⑤단계(ViewResolver)를 건너뛰고 `HttpMessageConverter`로 직렬화한다.

</div>
