> [← 홈](/study/) · [실무](/study/실무/실무/)

# 트러블슈팅: OOM (OutOfMemoryError)

## OOM이란?

JVM이 더 이상 메모리를 할당할 수 없을 때 발생하는 런타임 에러.
`java.lang.OutOfMemoryError`는 `Error`의 하위 클래스로, `Exception`이 아니다.
일반적인 try-catch로 복구하기 어렵고, 대부분 JVM 프로세스 종료로 이어진다.

```
JVM 메모리 구조:
┌─────────────────────────────────────────────┐
│                   JVM                       │
│  ┌──────────────────────────────────────┐   │
│  │              Heap                    │   │
│  │  ┌────────────┐  ┌────────────────┐  │   │
│  │  │  Young Gen │  │   Old Gen      │  │   │
│  │  │ (Eden,S0,S1│  │ (Tenured)      │  │   │
│  │  └────────────┘  └────────────────┘  │   │
│  └──────────────────────────────────────┘   │
│  ┌─────────────┐  ┌───────────────────────┐ │
│  │  Metaspace  │  │  Direct Buffer Memory │ │
│  │ (클래스 메타│  │ (NIO, off-heap)       │ │
│  │  데이터)    │  └───────────────────────┘ │
│  └─────────────┘                            │
└─────────────────────────────────────────────┘
```

---

## OOM 종류

### 1. java.lang.OutOfMemoryError: Java heap space

가장 흔한 OOM. Heap 메모리가 가득 찬 경우.

```
증상:
  Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
      at java.util.Arrays.copyOf(Arrays.java:3210)
      at java.util.ArrayList.grow(ArrayList.java:265)
      at com.example.service.DataService.loadAllData(DataService.java:45)

원인:
  - 대용량 데이터를 한 번에 메모리에 로드
  - 메모리 누수 (GC가 수거하지 못하는 객체 누적)
  - Heap 크기 설정 부족
```

```java
// 문제 코드: 전체 데이터를 메모리에 올림
public List<Order> getAllOrders() {
    return orderRepository.findAll();  // 100만 건을 한 번에!
}

// 해결: 페이징 처리
public Page<Order> getOrders(Pageable pageable) {
    return orderRepository.findAll(pageable);
}
```

---

### 2. java.lang.OutOfMemoryError: GC overhead limit exceeded

GC가 전체 시간의 98% 이상을 소비하는데도 메모리를 2% 미만밖에 회수하지 못할 때 발생.

```
증상:
  java.lang.OutOfMemoryError: GC overhead limit exceeded

의미:
  GC가 계속 돌고 있지만 효과가 없는 상태
  → 힙이 꽉 찼고, 수거할 수 있는 객체가 거의 없음
  → 실질적으로 Java heap space OOM과 동일한 원인

해결:
  - Heap 크기 증가 (-Xmx)
  - 메모리 누수 제거
  - GC 알고리즘 변경 (G1GC → ZGC)
```

---

### 3. java.lang.OutOfMemoryError: Metaspace

클래스 메타데이터를 저장하는 Metaspace가 부족한 경우.

```
증상:
  java.lang.OutOfMemoryError: Metaspace

원인:
  - 동적 클래스 생성 과다 (Reflection, CGLIB, Javassist)
  - Spring AOP 프록시 과도 생성
  - 클래스 로더 누수 (웹앱 재배포 시 구버전 클래스 잔류)
  - Metaspace 크기 미설정 (Java 8+에서 기본값 무제한이나 OS 제한 있음)
```

```bash
# Metaspace 크기 설정
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m
```

```java
// 문제: 동적으로 클래스를 무한 생성
// CGLIB 기반 동적 프록시 과도 생성 등

// 확인 방법
jstat -gc <pid> 1000  # Metaspace 사용량 모니터링
```

---

### 4. java.lang.OutOfMemoryError: Direct buffer memory

NIO의 Direct Buffer(off-heap) 메모리 부족.

```
증상:
  java.lang.OutOfMemoryError: Direct buffer memory

원인:
  - Netty, NIO 기반 프레임워크에서 Direct Buffer 과도 사용
  - ByteBuffer.allocateDirect() 후 해제 안 함
  - MaxDirectMemorySize 설정 미흡

해결:
  -XX:MaxDirectMemorySize=512m  # Direct Buffer 최대 크기 설정
```

---

## 원인별 분류

### 1. 메모리 누수 (Memory Leak)

GC가 수거해야 할 객체를 수거하지 못하고 메모리가 계속 증가하는 현상.

#### 원인 1: 컬렉션에 무한 추가

```java
// 문제: static 캐시에 계속 추가, 제거 없음
public class BadCache {
    private static final Map<String, Object> cache = new HashMap<>();

    public void put(String key, Object value) {
        cache.put(key, value);  // 계속 쌓임!
    }
    // remove() 호출 없음, 크기 제한 없음
}

// 해결: LRU 캐시 사용
private static final Map<String, Object> cache =
    Collections.synchronizedMap(
        new LinkedHashMap<String, Object>(1000, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > 1000;  // 최대 1000개 유지
            }
        }
    );

// 또는 Caffeine 사용
Cache<String, Object> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(Duration.ofMinutes(10))
    .build();
```

#### 원인 2: static 필드 참조

```java
// 문제: static 필드가 대용량 객체를 참조
public class DataProcessor {
    private static List<byte[]> processedData = new ArrayList<>();

    public void process(byte[] data) {
        // 처리 후 결과를 static 리스트에 추가
        processedData.add(process(data));
        // GC가 절대 수거 불가!
    }
}

// 해결: static 사용 자제, 필요 시 WeakReference 사용
public class DataProcessor {
    private static Map<String, WeakReference<ProcessedData>> cache = new HashMap<>();

    public ProcessedData getProcessed(String key) {
        WeakReference<ProcessedData> ref = cache.get(key);
        if (ref != null) {
            ProcessedData data = ref.get();
            if (data != null) return data;
        }
        // 재생성
        ProcessedData data = regenerate(key);
        cache.put(key, new WeakReference<>(data));
        return data;
    }
}
```

#### 원인 3: 이벤트 리스너 미해제

```java
// 문제: 리스너 등록 후 해제 안 함
public class EventService {
    private final List<EventListener> listeners = new ArrayList<>();

    public void register(EventListener listener) {
        listeners.add(listener);  // 계속 쌓임
    }
    // unregister() 없음!
}

// 해결
public void unregister(EventListener listener) {
    listeners.remove(listener);
}

// 또는 WeakReference 기반 리스너
private final List<WeakReference<EventListener>> listeners = new ArrayList<>();

public void notifyAll(Event event) {
    listeners.removeIf(ref -> ref.get() == null);  // 수거된 리스너 제거
    listeners.stream()
        .map(WeakReference::get)
        .filter(Objects::nonNull)
        .forEach(l -> l.onEvent(event));
}
```

---

### 2. 대용량 데이터 처리

```java
// 문제: 전체 데이터를 메모리에 올려서 처리
public void exportAllOrders() {
    List<Order> orders = orderRepository.findAll();  // 100만 건!
    orders.forEach(this::writeToFile);
}

// 해결 1: 페이징 처리
public void exportAllOrders() {
    int page = 0;
    int size = 1000;
    Page<Order> orders;

    do {
        orders = orderRepository.findAll(PageRequest.of(page, size));
        orders.getContent().forEach(this::writeToFile);
        page++;
    } while (orders.hasNext());
}

// 해결 2: JPA Streaming (Cursor 기반)
@Query("SELECT o FROM Order o")
@QueryHints(value = {
    @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
    @QueryHint(name = HINT_CACHEABLE, value = "false"),
    @QueryHint(name = READ_ONLY, value = "true")
})
Stream<Order> streamAllOrders();

// 서비스에서
@Transactional(readOnly = true)
public void exportAllOrders() {
    try (Stream<Order> stream = orderRepository.streamAllOrders()) {
        stream.forEach(this::writeToFile);
        // 메모리에 한 번에 1건만 로드!
    }
}

// 해결 3: Spring Batch ItemReader
@Bean
public JpaPagingItemReader<Order> orderReader() {
    return new JpaPagingItemReaderBuilder<Order>()
        .name("orderReader")
        .entityManagerFactory(emf)
        .queryString("SELECT o FROM Order o")
        .pageSize(1000)
        .build();
}
```

---

### 3. Metaspace 부족 (클래스 동적 생성)

```java
// 문제: 매번 새로운 프록시 클래스 동적 생성
for (int i = 0; i < 100000; i++) {
    // Enhancer를 반복적으로 생성 → 클래스 로드 폭발
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(SomeClass.class);
    enhancer.setCallback(new MethodInterceptor() { ... });
    SomeClass proxy = (SomeClass) enhancer.create();
}

// 해결: 프록시 재사용
Enhancer enhancer = new Enhancer();
enhancer.setSuperclass(SomeClass.class);
enhancer.setCallback(sharedInterceptor);
// 한 번 생성 후 재사용
SomeClass proxy = (SomeClass) enhancer.create();
```

---

## 진단 도구

### 1. Heap Dump 분석

```bash
# 힙 덤프 생성 (OOM 발생 시 자동)
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/logs/heapdump.hprof

# 수동 힙 덤프 생성
jmap -dump:format=b,file=heapdump.hprof <pid>

# 살아있는 객체만 덤프 (더 작은 파일)
jmap -dump:live,format=b,file=heapdump.hprof <pid>
```

```
Eclipse MAT (Memory Analyzer Tool) 분석 순서:
  1. heapdump.hprof 파일 열기
  2. Leak Suspects Report 실행
  3. Dominator Tree 확인 (메모리 점유 객체 순위)
  4. Object References 추적 (누가 참조하고 있는지)
  5. 코드에서 해당 객체 생성 지점 찾기
```

---

### 2. GC 로그 분석

```bash
# GC 로그 활성화 (JVM 옵션)
-Xlog:gc*:file=/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=10m

# 또는 구버전 방식
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-Xloggc:/logs/gc.log
```

```
GC 로그 읽는 법:
[2024-01-15T09:00:00.123+0900] GC(1) Pause Young (Normal) (G1 Evacuation Pause)
  Eden regions: 150->0(150)
  Survivor regions: 3->4(20)
  Old regions: 45->45
  Heap: 2048M->512M(4096M)     ← 이 부분 주목!
  [2024-01-15T09:00:00.234+0900] [0.111s]

- 2048M→512M: GC 전후 힙 사용량
- (4096M): 전체 힙 크기
- 0.111s: GC 소요 시간 (긴 경우 문제)
```

---

### 3. jstat으로 실시간 모니터링

```bash
# JVM 상태 실시간 모니터링 (1초 간격)
jstat -gc <pid> 1000

# 출력 예시:
# S0C   S1C   S0U   S1U   EC      EU      OC       OU      MC     MU    CCSC   CCSU   YGC   YGCT  FGC  FGCT   GCT
# 1024  1024  0     512   10240   8192    51200    40960   65536  63000 8192   7800   120   2.345  5   10.123 12.468
#
# OU (Old Used)가 계속 증가하면 메모리 누수 의심!
# FGC (Full GC Count)가 자주 증가하면 위험!

# Metaspace 확인
jstat -gcmetacapacity <pid>

# 클래스 로더 상태
jstat -class <pid>
```

---

### 4. Spring Actuator + Prometheus + Grafana

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

```java
// JVM 메모리 메트릭 자동 수집 (spring-boot-actuator + micrometer)
// /actuator/metrics/jvm.memory.used
// /actuator/metrics/jvm.gc.pause
// /actuator/prometheus (Prometheus 형식)
```

```
Grafana 대시보드 주요 패널:
  - JVM Heap Used / Max
  - GC Pause Duration
  - GC Count per minute
  - Non-Heap (Metaspace) Used
  - Thread Count
```

---

## 해결 방법

### 1. Heap 크기 조정

```bash
# JVM 시작 옵션
-Xms2g         # 초기 힙 크기 (2GB)
-Xmx4g         # 최대 힙 크기 (4GB)

# 권장: Xms = Xmx (힙 리사이징 오버헤드 제거)
-Xms4g -Xmx4g

# 컨테이너 환경 (Docker/Kubernetes)
# 컨테이너 메모리의 70~80% 설정 권장
-XX:MaxRAMPercentage=75.0

# GC 알고리즘 선택
-XX:+UseG1GC          # Java 9+ 기본, 대용량 힙에 적합
-XX:+UseZGC           # Java 15+, 저지연 GC
-XX:+UseShenandoahGC  # OpenJDK, 저지연 GC
```

---

### 2. 페이징/스트리밍 처리

```java
// JPA Streaming
@Transactional(readOnly = true)
public void processLargeData() {
    try (Stream<Order> stream = orderRepository.streamAllOrders()) {
        stream
            .filter(o -> o.getStatus() == OrderStatus.PAID)
            .map(this::transform)
            .forEach(this::process);
    }
}

// Spring Batch로 배치 처리
@Bean
public Step processOrdersStep() {
    return stepBuilderFactory.get("processOrders")
        .<Order, ProcessedOrder>chunk(500)  // 500건씩 처리
        .reader(orderReader())
        .processor(orderProcessor())
        .writer(orderWriter())
        .build();
}
```

---

### 3. WeakReference / SoftReference 활용

```java
// WeakReference: GC가 언제든지 수거 가능 (캐시 구현 시 활용)
Map<String, WeakReference<HeavyObject>> cache = new HashMap<>();

// SoftReference: 메모리 부족 시에만 GC가 수거 (이미지 캐시 등)
Map<String, SoftReference<BufferedImage>> imageCache = new HashMap<>();

public BufferedImage getImage(String path) {
    SoftReference<BufferedImage> ref = imageCache.get(path);
    if (ref != null) {
        BufferedImage image = ref.get();
        if (image != null) return image;
    }
    BufferedImage image = loadFromDisk(path);
    imageCache.put(path, new SoftReference<>(image));
    return image;
}

// WeakHashMap: 키가 GC 수거되면 엔트리 자동 제거
Map<Object, String> weakMap = new WeakHashMap<>();
```

---

## 실무 대응 플로우

```
OOM 발생!
    │
    ▼
1. 즉각 대응
   - 힙 덤프 확보 (자동: HeapDumpOnOutOfMemoryError)
   - 서버 재시작 (임시 조치)
   - 알림 확인 (Grafana, PagerDuty)
    │
    ▼
2. 원인 분석
   - GC 로그 확인: Full GC 빈도, 힙 사용량 추이
   - 힙 덤프 분석: MAT로 메모리 점유 객체 확인
   - APM 확인: 메모리 증가 시점의 요청 패턴
    │
    ▼
3. 원인 특정
   - 메모리 누수? → 코드 수정
   - 대용량 데이터? → 페이징/스트리밍
   - 힙 크기 부족? → JVM 옵션 조정
   - Metaspace? → 동적 클래스 생성 확인
    │
    ▼
4. 수정 및 검증
   - 코드 수정 / JVM 옵션 변경
   - 로컬 + 스테이징에서 부하 테스트
   - 메모리 사용량 모니터링 (jstat, Grafana)
    │
    ▼
5. 재발 방지
   - 메모리 임계값 알람 설정 (힙 80% 이상)
   - HeapDumpOnOutOfMemoryError 설정 확인
   - 정기적인 GC 로그 모니터링
```

---

## JVM 힙 모니터링 설정

```bash
# 권장 JVM 운영 옵션
JAVA_OPTS="\
  -Xms4g \
  -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/logs/heapdump.hprof \
  -XX:+ExitOnOutOfMemoryError \
  -Xlog:gc*:file=/logs/gc.log:time,uptime:filecount=5,filesize=20m \
  -XX:MetaspaceSize=256m \
  -XX:MaxMetaspaceSize=512m"
```

```yaml
# Kubernetes 환경 - 메모리 요청/제한 설정
resources:
  requests:
    memory: "4Gi"
  limits:
    memory: "6Gi"

# JVM 옵션 (컨테이너 메모리 인식)
env:
  - name: JAVA_OPTS
    value: "-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"
```

```java
// Spring Actuator 메모리 알람
@Component
public class MemoryHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        long used = heapUsage.getUsed();
        long max = heapUsage.getMax();
        double usagePercent = (double) used / max * 100;

        if (usagePercent > 90) {
            return Health.down()
                .withDetail("heapUsage", String.format("%.1f%%", usagePercent))
                .withDetail("used", used / 1024 / 1024 + "MB")
                .withDetail("max", max / 1024 / 1024 + "MB")
                .build();
        }
        return Health.up()
            .withDetail("heapUsage", String.format("%.1f%%", usagePercent))
            .build();
    }
}
```

---

## 특이점 및 주의사항

```
1. OOM은 Error이지 Exception이 아니다
   → catch (Exception e) 로 잡히지 않음
   → catch (OutOfMemoryError e) 또는 catch (Throwable e) 필요
   → 하지만 잡아도 복구가 어려움

2. Kubernetes/Docker 환경에서 OOM
   → 컨테이너 OOM Killer가 먼저 프로세스 종료 (OOM Error 로그 없이!)
   → -XX:+UseContainerSupport 반드시 설정 (Java 10+ 기본값)
   → kubectl describe pod 에서 OOMKilled 확인

3. Full GC vs Minor GC
   → Minor GC: Young Gen 정리 (빠름, ms 단위)
   → Full GC: 전체 힙 정리 (느림, 초 단위) → STW 발생
   → Full GC 빈도가 높으면 OOM 임박 신호

4. GC 알고리즘 선택
   → G1GC: Java 9+ 기본, 대용량 힙(4GB+)에 적합
   → ZGC: 저지연 필요 시, 수 TB 힙도 지원 (Java 15+)
   → 트레이드오프: 처리량 vs 지연시간
```
