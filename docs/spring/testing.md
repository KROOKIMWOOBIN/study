## Testing

### 왜 쓰는가?

코드 변경이 기존 기능을 망가뜨렸는지 매번 수동으로 확인하는 건 불가능하다. 테스트는 **변경에 대한 안전망**이고, 문서 역할도 한다.

### 테스트 종류

| 종류 | 범위 | 속도 | 신뢰도 |
|------|------|------|--------|
| 단위 테스트 | 클래스/메서드 단위 | 매우 빠름 | 낮음 |
| 통합 테스트 | 여러 컴포넌트 + DB | 느림 | 높음 |
| E2E 테스트 | 전체 시스템 | 매우 느림 | 가장 높음 |

실무에서는 **단위 테스트를 많이, 통합 테스트를 적절히** 작성한다.

### JUnit5 기본

```markdown
class MemberServiceTest {

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findByEmail_success() {
        // given
        String email = "test@example.com";

        // when
        Member result = memberService.findByEmail(email);

        // then
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 예외 발생")
    void findByEmail_notFound() {
        assertThatThrownBy(() -> memberService.findByEmail("no@example.com"))
            .isInstanceOf(MemberNotFoundException.class);
    }
}
```

### Mockito — 단위 테스트

외부 의존성(Repository, 외부 API)을 가짜 객체로 대체해 순수하게 로직만 테스트한다.

```markdown
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("회원 저장 시 이메일 발송")
    void save_sendEmail() {
        // given
        MemberCreateRequest request = new MemberCreateRequest("홍길동", "test@test.com");
        Member savedMember = new Member(1L, "홍길동", "test@test.com");

        given(memberRepository.existsByEmail("test@test.com")).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        memberService.save(request);

        // then
        verify(emailService, times(1)).sendWelcome("test@test.com");
    }
}
```

### @SpringBootTest — 통합 테스트

실제 Spring 컨텍스트를 띄워 DB까지 포함한 전체 흐름을 테스트한다.

```markdown
@SpringBootTest
@Transactional  // 테스트 후 롤백
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save_and_find() {
        MemberCreateRequest request = new MemberCreateRequest("홍길동", "test@test.com");
        memberService.save(request);

        Optional<Member> found = memberRepository.findByEmail("test@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("홍길동");
    }
}
```

### MockMvc — 컨트롤러 테스트

```markdown
@WebMvcTest(MemberController.class)  // 컨트롤러 레이어만 로드
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("POST /api/members - 회원 생성 성공")
    void create_success() throws Exception {
        MemberResponse response = new MemberResponse(1L, "홍길동", "test@test.com");
        given(memberService.save(any())).willReturn(response);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "홍길동", "email": "test@test.com"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("홍길동"))
            .andDo(print());
    }
}
```

### 테스트 픽스처 — @BeforeEach

```markdown
@BeforeEach
void setUp() {
    memberRepository.deleteAll();
    testMember = memberRepository.save(new Member("홍길동", "test@test.com"));
}
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| `@SpringBootTest` 남용 | 전체 컨텍스트 로딩으로 느림 | 단위 테스트 위주, 통합 테스트 최소화 |
| `@Transactional` 테스트와 실제 동작 차이 | 프록시 동작 차이 발생 가능 | 중요 시나리오는 별도 검증 |
| Mock 과다 | 실제 동작과 괴리 | 핵심 의존성은 실제 객체 사용 고려 |
| 테스트 간 의존성 | 한 테스트가 다른 테스트에 영향 | 테스트마다 DB 초기화 |
