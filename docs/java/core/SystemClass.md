> [← 홈](/README.md) · [Java](/docs/java/java.md) · [중급 1편](/docs/java/core/core.md)

## System Class
- JVM과 운영체제 수준의 시스템 기능을 접근하기 위한 유틸리티 클래스
- `java.lang` 패키지에 포함 → `import` 없이 사용 가능
- 모든 멤버 `static`, 생성자 `private` → 인스턴스 생성 불가

### 왜 사용하는가?
- JVM 실행 환경과 직접 연결된 기능(입출력, 시간, 환경변수, 종료 등) 접근
- 배열 복사를 네이티브 레벨로 빠르게 수행
- 성능 측정, 환경 설정 값 조회 등 시스템 수준 작업 처리

### 특이점
- `System.arraycopy()`는 native 메서드로 JVM 내부 최적화 → 일반 `for`문보다 빠름
- `System.exit()`는 `finally`도 실행 안 될 수 있어 서버 환경에서는 사용 지양

---

### 표준 입출력 스트림
| 스트림 | 설명 |
| --- | --- |
| `System.in` | 표준 입력 스트림 (키보드 입력) |
| `System.out` | 표준 출력 스트림 (콘솔 출력) |
| `System.err` | 표준 오류 출력 |

```java
System.out.println("출력");
System.err.println("오류 출력"); // 빨간색으로 출력
```

### 시간 측정
| 메서드 | 설명 | 용도 |
| --- | --- | --- |
| `System.currentTimeMillis()` | 밀리초(ms) 단위 현재 시간 | 날짜/시간 계산 |
| `System.nanoTime()` | 나노초(ns) 단위 시간 | 정밀한 성능 측정 |

```java
long start = System.nanoTime();
// 측정할 코드
long end = System.nanoTime();
System.out.println("경과 시간: " + (end - start) + "ns");
```

### 환경 변수 조회
- 운영체제 환경 변수 접근 (읽기 전용)
```java
String path = System.getenv("PATH");
String dbHost = System.getenv("DB_HOST"); // 실무에서 민감 정보 관리
```

### 배열 고속 복사
```java
System.arraycopy(src, srcPos, dest, destPos, length)
```
```java
int[] a = {1, 2, 3};
int[] b = new int[3];
System.arraycopy(a, 0, b, 0, 3); // a 전체를 b로 복사
```
- native 메서드, JVM 내부 최적화
- `ArrayList` 내부에서도 사용됨

### 시스템 속성 (System Properties)
- JVM 실행 환경의 설정 정보 (key-value)
```java
System.getProperty("java.version"); // JVM 버전
System.getProperty("user.dir");     // 현재 실행 디렉토리
System.getProperty("os.name");      // 운영체제
System.getProperty("file.separator"); // 파일 구분자
```

### 프로그램 종료
```java
System.exit(0);  // 정상 종료
System.exit(1);  // 비정상 종료
```
- JVM 즉시 종료 → `finally`가 실행되지 않을 수 있음
- Spring / 서버 환경에서는 사용 지양

### 어떻게 사용하는가?
```java
// 성능 측정
long start = System.nanoTime();
performTask();
System.out.println((System.nanoTime() - start) + "ns");

// 배열 복사
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[src.length];
System.arraycopy(src, 0, dest, 0, src.length);

// 환경 변수
String apiKey = System.getenv("API_KEY");
```

### 어떨 때 많이 쓰는가?
| 상황 | 사용 메서드 |
| --- | --- |
| 성능 측정, 벤치마크 | `nanoTime()` |
| 타임스탬프, 로그 시간 | `currentTimeMillis()` |
| 배열 대량 복사 | `arraycopy()` |
| 환경 설정 값 읽기 | `getenv()`, `getProperty()` |
| 콘솔 출력/디버깅 | `out.println()`, `err.println()` |
