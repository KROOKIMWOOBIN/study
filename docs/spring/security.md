## Spring Security + JWT

### 왜 쓰는가?

<div class="concept-box" markdown="1">

로그인, 권한 제어는 거의 모든 서비스의 필수 요소다. Spring Security는 ==인증(Authentication)==과 ==인가(Authorization)==를 표준화된 방식으로 제공한다.

</div>

### 핵심 개념

| 개념 | 설명 |
|------|------|
| 인증 (Authentication) | 누구인가? (로그인) |
| 인가 (Authorization) | 무엇을 할 수 있는가? (권한) |
| `SecurityFilterChain` | HTTP 요청 처리 필터 체인 |
| `UserDetails` | Spring Security의 사용자 정보 인터페이스 |
| `UserDetailsService` | DB에서 사용자 정보를 로드하는 인터페이스 |

### Security 설정

```markdown
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // REST API는 CSRF 불필요
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // JWT는 세션 미사용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()    // 인증 없이 접근 가능
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### UserDetails 구현

```markdown
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }

    @Override
    public String getPassword() { return member.getPassword(); }

    @Override
    public String getUsername() { return member.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }
    // ... 나머지 기본 true
}

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        return new CustomUserDetails(member);
    }
}
```

### JWT 흐름

```markdown
로그인 요청 (email, password)
  → AuthController
  → UserDetailsService.loadUserByUsername()
  → PasswordEncoder.matches()
  → 성공: JwtProvider.generateToken()
  → 응답: accessToken, refreshToken

이후 요청
  → JwtAuthFilter (요청 헤더에서 토큰 추출)
  → JwtProvider.validateToken()
  → SecurityContextHolder에 인증 정보 저장
  → 컨트롤러 진입
```

### JWT 구현

```markdown
@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignKey())
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build()
            .parseSignedClaims(token).getPayload().getSubject();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
```

### Refresh Token 전략

```markdown
// Access Token: 짧은 만료 (15분~1시간)
// Refresh Token: 긴 만료 (7~30일), DB 또는 Redis에 저장

POST /api/auth/refresh
  → Refresh Token 검증
  → DB에 저장된 토큰과 비교
  → 새 Access Token 발급
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| JWT 탈취 | 만료 전까지 사용 가능 | 짧은 만료 + Refresh Token 로테이션 |
| 로그아웃 처리 | JWT는 서버에서 무효화 불가 | Redis 블랙리스트 또는 짧은 만료 |
| Secret Key 노출 | 모든 토큰 위조 가능 | 환경 변수 관리, 충분한 길이의 키 |
| ROLE_ 접두사 | Spring Security는 `ROLE_` 접두사 필요 | `hasRole("ADMIN")` → DB에 `ADMIN` 저장 |

---

## 내부 동작 원리

### Spring Security는 필터 체인이다

> Spring Security는 서블릿 필터(Filter) 기반으로 동작한다.
> HTTP 요청이 스프링 컨트롤러에 도달하기 **전에**, 필터 체인을 통과하면서 인증/인가를 처리한다.

```text
클라이언트 HTTP 요청
  ↓
[Servlet Filter Chain — Tomcat 레벨]
  ↓
  DelegatingFilterProxy   ← 스프링이 서블릿 필터에 연결하는 브릿지
  ↓
  FilterChainProxy         ← Spring Security의 핵심, 아래 필터들을 순서대로 실행
  ↓
  ┌─────────────────────────────────────────────────┐
  │ Spring Security Filter Chain (주요 필터만)       │
  │ 1. SecurityContextPersistenceFilter              │  ← SecurityContext 로딩/저장
  │ 2. UsernamePasswordAuthenticationFilter          │  ← 폼 로그인 처리
  │ 3. JwtAuthenticationFilter (커스텀)              │  ← JWT 토큰 검증 (우리가 추가)
  │ 4. ExceptionTranslationFilter                   │  ← 인증/인가 예외 처리
  │ 5. AuthorizationFilter                          │  ← 권한 검사 (가장 마지막)
  └─────────────────────────────────────────────────┘
  ↓
  DispatcherServlet → 컨트롤러
```

### SecurityContextHolder — 인증 정보 저장소

> `SecurityContextHolder`는 현재 인증된 사용자 정보를 저장하는 **ThreadLocal 기반 저장소**다.
> 요청을 처리하는 스레드에 "지금 누가 로그인해 있는가"를 기억해 두는 것이다.

```java
JWT 필터에서 토큰 검증 성공 시:
  UsernamePasswordAuthenticationToken auth
      = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
  SecurityContextHolder.getContext().setAuthentication(auth)
  // ThreadLocal에 저장

컨트롤러에서 사용:
  @GetMapping("/me")
  public MemberResponse me(@AuthenticationPrincipal CustomUserDetails user) {
      // SecurityContextHolder에서 자동으로 꺼내줌
      return MemberResponse.from(user.getMember());
  }

요청 처리 완료 후:
  SecurityContextPersistenceFilter가 SecurityContextHolder.clearContext() 호출
  → 다음 요청에서 이전 사용자 정보가 남지 않도록 정리
```

### JWT 인증 필터 — 전체 흐름

```text
모든 HTTP 요청
  → JwtAuthenticationFilter.doFilterInternal()
       ① Authorization 헤더에서 "Bearer {token}" 추출
       ② JwtProvider.validateToken(token) → 서명, 만료 검증
       ③ JwtProvider.extractUsername(token) → 이메일 추출
       ④ UserDetailsService.loadUserByUsername(email) → DB 조회
       ⑤ SecurityContextHolder에 인증 정보 저장
  → 다음 필터로 넘어감

AuthorizationFilter (마지막 필터):
  → SecurityContextHolder에 인증 정보 있는지 확인
  → "/api/admin/**" 요청 → ROLE_ADMIN 권한 있는지 확인
  → 없으면 403 Forbidden
```

### AuthenticationManager 위임 체인

폼 로그인 방식일 때 `UsernamePasswordAuthenticationFilter`가 인증을 처리하는 구조.

```text
UsernamePasswordAuthenticationFilter
  → AuthenticationManager.authenticate(token)
       → ProviderManager (AuthenticationManager 구현체)
           → 등록된 AuthenticationProvider 목록 순회
           → DaoAuthenticationProvider 선택
               → UserDetailsService.loadUserByUsername(username)
               → PasswordEncoder.matches(rawPassword, encodedPassword)
               → 성공 → Authentication 객체 반환
               → 실패 → BadCredentialsException
```

<div class="concept-box" markdown="1">

**JWT vs 세션 인증의 차이**:
- **세션**: 서버 메모리에 세션 저장 → `SecurityContextPersistenceFilter`가 세션에서 인증 정보 복원
- **JWT**: 서버에 상태 없음(Stateless) → 매 요청마다 JWT를 검증해 인증 정보를 새로 만듦
  → `SessionCreationPolicy.STATELESS`로 설정하면 세션을 전혀 사용하지 않음

</div>
