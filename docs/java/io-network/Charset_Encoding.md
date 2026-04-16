## 문자 집합과 인코딩 (Charset, Encoding)

### 왜 쓰는지

컴퓨터는 **0과 1(바이트)만 이해**합니다. 문자를 저장하거나 전송하려면:
- **문자 → 바이트** (인코딩)
- **바이트 → 문자** (디코딩)

<div class="concept-box" markdown="1">

**핵심**:
- **문자 집합(Charset)**: 문자를 숫자로 매핑한 규칙 (A=65, 가=...?)
- **인코딩**: 문자를 바이트로 변환
- **디코딩**: 바이트를 문자로 변환

같은 바이트를 다른 Charset으로 디코딩하면 다른 문자가 될 수 있습니다!

</div>

### 어떻게 쓰는지

#### 문자 집합(Charset) 정보 조회

```java
// 1️⃣ 시스템에서 지원하는 모든 Charset 조회
SortedMap<String, Charset> allCharsets = Charset.availableCharsets();
for (String name : allCharsets.keySet()) {
    System.out.println(name);  // UTF-8, MS949, EUC-KR, ...
}

// 2️⃣ 이름으로 Charset 조회
Charset utf8 = Charset.forName("UTF-8");
Charset eucKr = Charset.forName("EUC-KR");

// 3️⃣ 상수로 직접 조회 (타입 안전)
Charset utf8Safe = StandardCharsets.UTF_8;
Charset iso = StandardCharsets.ISO_8859_1;

// 4️⃣ Charset 별칭 확인
Set<String> aliases = utf8.aliases();
System.out.println(aliases);  // [UTF8, utf8, ...]

// 5️⃣ 시스템 기본 Charset 조회
Charset defaultCharset = Charset.defaultCharset();
System.out.println(defaultCharset);  // Windows: MS949, Linux: UTF-8
```

#### 문자 ↔ 바이트 변환

```java
// 1️⃣ 문자를 바이트로 인코딩
String text = "Hello";
byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
System.out.println(Arrays.toString(bytes));  // [72, 101, 108, 108, 111]

// 2️⃣ 바이트를 문자로 디코딩
String decoded = new String(bytes, StandardCharsets.UTF_8);
System.out.println(decoded);  // Hello

// 3️⃣ 한글 인코딩
String korean = "가";
byte[] koreanBytes = korean.getBytes(StandardCharsets.UTF_8);
System.out.println(Arrays.toString(koreanBytes));  // [234, 176, 128] (3 바이트)

// 4️⃣ 잘못된 Charset으로 디코딩하면?
String wrongDecoded = new String(koreanBytes, StandardCharsets.ISO_8859_1);
System.out.println(wrongDecoded);  // ??? (깨짐)
```

#### ByteBuffer 사용 (더 효율적)

```java
// CharsetEncoder/Decoder 사용 (성능 최적화)
Charset charset = StandardCharsets.UTF_8;
CharsetEncoder encoder = charset.newEncoder();
CharsetDecoder decoder = charset.newDecoder();

// 인코딩
String text = "Hello, 가";
ByteBuffer encoded = encoder.encode(CharBuffer.wrap(text));
System.out.println(encoded);

// 디코딩
CharBuffer decoded = decoder.decode(encoded.duplicate());
System.out.println(decoded);  // Hello, 가
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **파일 읽기/쓰기** | ✅ 명시적 Charset 지정 | 인코딩 문제 방지 |
| **네트워크 통신** | ✅ UTF-8 권장 | 국제 표준 |
| **데이터베이스** | ✅ UTF-8 기본 | 다국어 지원 |
| **다국어 애플리케이션** | ✅ UTF-8 | 모든 문자 표현 가능 |
| **레거시 시스템** | ⚠️ EUC-KR/MS949 | 호환성 필요 시 |

### 장점

| 장점 | 설명 |
|------|------|
| **다국어 지원** | UTF-8로 모든 언어 표현 |
| **명시적 제어** | 어떤 인코딩을 쓸지 명확히 지정 가능 |
| **호환성** | 다양한 Charset 지원으로 레거시 호환 가능 |
| **표준화** | UTF-8이 국제 표준 |

### 단점

| 단점 | 설명 |
|------|------|
| **복잡성** | Charset 종류가 많아 혼동 가능 |
| **호환성 문제** | 잘못된 Charset으로 디코딩하면 깨짐 |
| **성능** | 인코딩/디코딩은 CPU 비용 발생 |
| **메모리** | 일부 Charset은 더 많은 바이트 사용 |

### 특징

#### 주요 Charset 비교

| Charset | 언어 | 바이트/글자 | 특징 |
|---------|------|-----------|------|
| **ASCII** | 영문 | 1 | 0-127만 표현 (한글 불가) |
| **ISO-8859-1** | 서유럽 | 1 | ASCII 확장, 한글 불가 |
| **EUC-KR** | 한글 | 1-2 | 한글 2바이트, 레거시 표준 |
| **MS949** | 한글 | 1-2 | EUC-KR 확장, 윈도우 표준 |
| **UTF-8** | 모든 언어 | 1-4 | 가변길이, 국제 표준 ✅ |
| **UTF-16** | 모든 언어 | 2-4 | 고정 크기, 메모리 비효율 |

#### UTF-8 vs EUC-KR vs MS949

```java
String text = "가";

// UTF-8: 3바이트
byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
System.out.println(utf8.length);  // 3 bytes

// EUC-KR: 2바이트
byte[] euckr = text.getBytes(Charset.forName("EUC-KR"));
System.out.println(euckr.length);  // 2 bytes

// MS949: 2바이트
byte[] ms949 = text.getBytes(Charset.forName("MS949"));
System.out.println(ms949.length);  // 2 bytes
```

#### 인코딩/디코딩 불일치 문제

```java
String text = "가";
byte[] utf8Encoded = text.getBytes(StandardCharsets.UTF_8);

// ❌ 잘못된 디코딩: UTF-8으로 인코딩 → EUC-KR로 디코딩
String wrongDecoded = new String(utf8Encoded, Charset.forName("EUC-KR"));
System.out.println(wrongDecoded);  // ??? (깨짐)

// ✅ 올바른 디코딩
String correctDecoded = new String(utf8Encoded, StandardCharsets.UTF_8);
System.out.println(correctDecoded);  // 가
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 플랫폼 기본 Charset에 의존하지 않기**

```java
// ❌ 위험: 시스템마다 다를 수 있음
String text = "한글";
byte[] bytes = text.getBytes();  // 기본 Charset 사용
String decoded = new String(bytes);  // 기본 Charset 사용

// Windows: MS949 → 한글 OK
// Linux: UTF-8 → 한글 OK
// Mac: UTF-8 → 한글 OK
// 하지만 보증 없음!

// ✅ 명시적으로 지정
String text = "한글";
byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
String decoded = new String(bytes, StandardCharsets.UTF_8);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 파일 읽기/쓰기 시 Charset 지정**

```java
// ❌ 나쁜 예: Charset 미지정
try (FileReader reader = new FileReader("file.txt")) {
    // 시스템 기본 Charset으로 읽음
}

// ✅ 좋은 예: Charset 명시
try (FileReader reader = new FileReader("file.txt", StandardCharsets.UTF_8)) {
    // UTF-8로 명시적으로 읽음
}

// 또는 Files 클래스 사용
String content = Files.readString(Path.of("file.txt"), StandardCharsets.UTF_8);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 레거시 시스템 호환 시 주의**

```java
// EUC-KR/MS949로 인코딩된 데이터를 받은 경우
byte[] legacyData = ...; // EUC-KR로 인코딩됨

// ✅ 올바른 Charset으로 디코딩
String text = new String(legacyData, Charset.forName("EUC-KR"));

// 새로운 시스템으로는 UTF-8로 변환
byte[] newData = text.getBytes(StandardCharsets.UTF_8);
```

</div>

### 실전 예시

#### 파일 읽기/쓰기

```java
// 쓰기: UTF-8로 저장
String content = "안녕하세요";
Files.writeString(
    Path.of("greeting.txt"),
    content,
    StandardCharsets.UTF_8
);

// 읽기: UTF-8로 읽음
String read = Files.readString(
    Path.of("greeting.txt"),
    StandardCharsets.UTF_8
);
System.out.println(read);  // 안녕하세요
```

#### 네트워크 통신

```java
// 전송: 문자를 바이트로 변환
String message = "Hello, 世界";
byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
outputStream.write(bytes);

// 수신: 바이트를 문자로 변환
byte[] received = new byte[1024];
int bytesRead = inputStream.read(received);
String message = new String(received, 0, bytesRead, StandardCharsets.UTF_8);
System.out.println(message);  // Hello, 世界
```

#### CSV 파일 읽기 (한글 포함)

```java
// ❌ 나쁜 예
Scanner scanner = new Scanner(new File("data.csv"));  // 기본 Charset

// ✅ 좋은 예
Scanner scanner = new Scanner(
    new File("data.csv"),
    StandardCharsets.UTF_8
);
```

### 정리

| 항목 | 설명 |
|------|------|
| **Charset** | 문자를 숫자로 매핑하는 규칙 |
| **인코딩** | 문자(String) → 바이트(byte[]) |
| **디코딩** | 바이트(byte[]) → 문자(String) |
| **표준 선택** | **UTF-8 권장** |
| **주의** | 인코딩과 디코딩 Charset 반드시 일치 |

<div class="success-box" markdown="1">

**권장**:
- 새 프로젝트: UTF-8 기본 사용
- 레거시: 명시적으로 Charset 지정
- 파일/네트워크: 항상 `StandardCharsets.UTF_8` 명시

</div>

---

**관련 파일:** [IO](IO.md), [Network](network.md)
