## 모니터링 (Actuator + Prometheus + Grafana)

### 왜 쓰는가?

<div class="concept-box" markdown="1">

서버가 죽기 전에 신호를 감지하고 대응해야 한다. 모니터링은 CPU, 메모리, 응답 시간, 에러율 등의 지표를 수집해 **장애를 사전에 예방**하고 원인을 빠르게 파악하게 한다.

</div>

---

### Spring Actuator

애플리케이션 상태를 HTTP 엔드포인트로 노출한다.

```markdown
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

```markdown
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  endpoint:
    health:
      show-details: always
```

| 엔드포인트 | 설명 |
|-----------|------|
| `/actuator/health` | 서버 상태 확인 (로드밸런서 헬스체크) |
| `/actuator/metrics` | 수집 가능한 메트릭 목록 |
| `/actuator/metrics/jvm.memory.used` | JVM 메모리 사용량 |
| `/actuator/prometheus` | Prometheus 형식 메트릭 |

```markdown
// 헬스체크 응답
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

---

### Prometheus + Grafana 연동

```markdown
implementation 'io.micrometer:micrometer-registry-prometheus'
```

```markdown
# prometheus.yml
scrape_configs:
  - job_name: 'spring-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['app:8080']
```

Prometheus가 15초마다 `/actuator/prometheus`에서 메트릭 수집 → Grafana에서 시각화.

---

### 커스텀 메트릭

```markdown
@Service
@RequiredArgsConstructor
public class OrderService {

    private final MeterRegistry meterRegistry;
    private final Counter orderCounter;

    @PostConstruct
    public void init() {
        orderCounter = Counter.builder("order.count")
            .description("총 주문 수")
            .tag("type", "all")
            .register(meterRegistry);
    }

    public void placeOrder(OrderRequest request) {
        // 주문 처리...
        orderCounter.increment();  // 메트릭 기록
    }
}
```

---

### 주요 모니터링 지표

| 지표 | 의미 | 알람 기준 (예시) |
|------|------|----------------|
| `jvm.memory.used` | JVM 힙 사용량 | 80% 이상 |
| `process.cpu.usage` | CPU 사용률 | 80% 이상 |
| `http.server.requests` | 요청 수, 응답 시간, 에러율 | 에러율 1% 이상 |
| `hikaricp.connections.active` | DB 커넥션 사용 수 | 풀 크기의 80% |
| `jvm.gc.pause` | GC 일시정지 시간 | 500ms 이상 |

---

### 헬스체크 커스터마이징

```markdown
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // 외부 API 상태 확인
            restTemplate.getForObject("https://api.external.com/health", String.class);
            return Health.up().withDetail("externalApi", "정상").build();
        } catch (Exception e) {
            return Health.down().withDetail("externalApi", e.getMessage()).build();
        }
    }
}
```

### 단점 / 주의할 점

| 상황 | 문제 | 해결 |
|------|------|------|
| Actuator 외부 노출 | 내부 정보 노출 | 운영 환경에서는 `/actuator` 접근 제한 |
| Prometheus 데이터 무제한 축적 | 디스크 부족 | 보존 기간 설정 (`--storage.tsdb.retention.time`) |
| 메트릭 태그 폭발 | 카디널리티 높은 태그(userId 등) | 범용 태그만 사용 |
