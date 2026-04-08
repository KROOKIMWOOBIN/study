## Logging

### 왜 쓰는가?

<div class="concept-box" markdown="1">

장애 발생 시 원인을 파악할 수 있는 유일한 수단이 로그다. 적절한 로깅 전략 없이는 문제가 발생해도 원인을 찾을 수 없다.

</div>

### Logback (Spring Boot 기본)

Spring Boot는 SLF4J + Logback을 기본 제공한다.

```markdown
@Slf4j  // lombok: private static final Logger log = LoggerFactory.getLogger(this.getClass());
@Service
public class MemberService {

    public Member findById(Long id) {
        log.debug("회원 조회 시작: id={}", id);
        Member member = memberRepository.findById(id)
            .orElseThrow(MemberNotFoundException::new);
        log.info("회원 조회 완료: id={}, name={}", id, member.getName());
        return member;
    }
}
```

### 로그 레벨

| 레벨 | 용도 | 운영 사용 |
|------|------|-----------|
| `ERROR` | 즉각 대응 필요한 심각한 오류 | O |
| `WARN` | 잠재적 문제, 예상 가능한 오류 | O |
| `INFO` | 서비스 주요 흐름 (요청 시작/완료) | O |
| `DEBUG` | 개발 중 상세 흐름 | 개발만 |
| `TRACE` | 아주 상세한 디버깅 | 거의 사용 안 함 |

```markdown
# application.yml
logging:
  level:
    root: WARN
    com.myapp: INFO        # 내 패키지만 INFO
    com.myapp.service: DEBUG  # 특정 패키지는 DEBUG
```

### Logback 설정 (logback-spring.xml)

```markdown
<configuration>
    <!-- 콘솔 출력 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 파일 출력 (날짜별 롤링) -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>  <!-- 30일 보관 -->
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="WARN">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

    <logger name="com.myapp" level="INFO"/>
</configuration>
```

### MDC — 요청별 추적 ID (Trace ID)

MDC(Mapped Diagnostic Context)로 요청별 고유 ID를 로그에 자동 삽입한다. 여러 로그 중 특정 요청의 흐름을 추적할 수 있다.

```markdown
// Filter에서 요청마다 TraceId 부여
@Component
public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put("traceId", UUID.randomUUID().toString().substring(0, 8));
            chain.doFilter(request, response);
        } finally {
            MDC.clear();  // 반드시 제거 (스레드 풀 재사용으로 오염 방지)
        }
    }
}
```

```markdown
# logback-spring.xml 패턴에 traceId 추가
<pattern>%d{HH:mm:ss} [%X{traceId}] %-5level %logger - %msg%n</pattern>
```

```markdown
// 로그 출력 예시
10:23:45 [a3f9c2b1] INFO  MemberService - 회원 조회 시작: id=42
10:23:45 [a3f9c2b1] DEBUG MemberRepository - SELECT * FROM members WHERE id=42
10:23:45 [a3f9c2b1] INFO  MemberService - 회원 조회 완료: id=42, name=홍길동
```

같은 traceId로 한 요청의 전체 흐름을 추적할 수 있다.

### 실무 로깅 전략

```markdown
// 외부 API 호출 로깅
log.info("[외부 API 호출] url={}, request={}", url, request);
try {
    Response response = httpClient.post(url, request);
    log.info("[외부 API 응답] status={}, response={}", response.getStatus(), response.getBody());
    return response;
} catch (Exception e) {
    log.error("[외부 API 오류] url={}, error={}", url, e.getMessage(), e);
    throw e;
}

// DB 쿼리 로깅 (개발환경)
# application-dev.yml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE  # 파라미터 값 출력
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| 개인정보 로그 출력 | 비밀번호, 카드번호 등 노출 | 마스킹 처리 |
| 과도한 로그 | 디스크 부족, 성능 저하 | 적절한 레벨과 롤링 정책 설정 |
| `MDC.clear()` 누락 | 다른 요청에 이전 traceId 오염 | finally 블록에서 반드시 호출 |
| 문자열 더하기로 로그 | 레벨 비활성 시에도 문자열 생성 비용 | `log.debug("값: {}", value)` 사용 |
