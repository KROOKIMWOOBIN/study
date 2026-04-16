## I/O (Input/Output) 스트림

### 왜 쓰는지

프로그램은 **다양한 소스에서 데이터를 읽고 써야** 합니다:
- 파일에서 읽기/쓰기
- 네트워크에서 송수신
- 메모리 버퍼에 데이터 처리
- 압축 파일 다루기

매 번 다른 방식으로 처리하면 복잡하므로, **통일된 인터페이스(Stream)로 처리**합니다.

<div class="concept-box" markdown="1">

**핵심**: I/O Stream은 **데이터를 바이트 단위로 순차적으로 처리하는 연속적인 흐름**입니다. 데이터 소스(파일, 네트워크 등)의 종류와 관계없이 동일한 방식으로 처리합니다.

</div>

### 어떻게 쓰는지

#### InputStream: 데이터 읽기

```java
// 1️⃣ 파일에서 읽기
try (FileInputStream fis = new FileInputStream("file.txt")) {
    byte[] buffer = new byte[1024];
    int bytesRead = fis.read(buffer);
    System.out.println("읽음: " + new String(buffer, 0, bytesRead));
}

// 2️⃣ 네트워크에서 읽기
Socket socket = new Socket("example.com", 80);
InputStream in = socket.getInputStream();
byte[] data = new byte[1024];
int bytesRead = in.read(data);

// 3️⃣ 메모리에서 읽기
byte[] data = "hello".getBytes();
ByteArrayInputStream bais = new ByteArrayInputStream(data);
int byte1 = bais.read();  // 'h'
```

#### OutputStream: 데이터 쓰기

```java
// 1️⃣ 파일에 쓰기
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    fos.write("Hello World".getBytes(StandardCharsets.UTF_8));
    fos.flush();  // 버퍼 비우기
}

// 2️⃣ 네트워크에 전송
Socket socket = new Socket("example.com", 80);
OutputStream out = socket.getOutputStream();
out.write("HTTP GET...".getBytes());
out.flush();

// 3️⃣ 메모리에 쓰기
ByteArrayOutputStream baos = new ByteArrayOutputStream();
baos.write("hello".getBytes());
byte[] result = baos.toByteArray();
```

#### 보조 스트림 (래퍼)

```java
// BufferedInputStream: 버퍼를 사용한 효율적 읽기
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("large-file.txt"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // 처리
    }
}

// DataInputStream: 기본형 데이터를 읽기
try (DataInputStream dis = new DataInputStream(
        new FileInputStream("data.bin"))) {
    int count = dis.readInt();
    String name = dis.readUTF();
    double price = dis.readDouble();
}

// ObjectInputStream: 객체 역직렬화
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("object.ser"))) {
    User user = (User) ois.readObject();
}

// PrintWriter: 편리한 텍스트 출력
try (PrintWriter writer = new PrintWriter("output.txt")) {
    writer.println("line 1");
    writer.println("line 2");
}
```

#### Path와 Files (NIO - 권장)

```java
// 1️⃣ 파일 읽기/쓰기 (현대 표준)
Path path = Path.of("test.txt");

// 텍스트 읽기
String content = Files.readString(path, StandardCharsets.UTF_8);

// 텍스트 쓰기
Files.writeString(path, "Hello", StandardCharsets.UTF_8);

// 바이트 배열 읽기/쓰기
byte[] bytes = Files.readAllBytes(path);
Files.write(path, "data".getBytes());

// 2️⃣ 파일 조작
Files.copy(path, Path.of("copy.txt"));
Files.move(path, Path.of("moved.txt"));
Files.delete(path);
Files.exists(path);

// 3️⃣ 디렉토리 탐색
try (var stream = Files.list(Path.of("."))) {
    stream.forEach(System.out::println);
}

// 4️⃣ 라인 단위 읽기
List<String> lines = Files.readAllLines(path);
lines.forEach(System.out::println);
```

#### 직렬화 (Serialization)

```java
// Serializable 구현 필수
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int age;
    
    // getter, setter, constructor...
}

// 직렬화: 객체 → 바이트 저장
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("user.ser"))) {
    User user = new User("Alice", 25);
    oos.writeObject(user);
}

// 역직렬화: 바이트 → 객체 복원
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("user.ser"))) {
    User user = (User) ois.readObject();
    System.out.println(user.getName());  // "Alice"
}
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **파일 읽기/쓰기** | ✅ Files (NIO) | 현대 표준, 간편 |
| **대용량 파일** | ✅ BufferedInputStream | 효율적 메모리 사용 |
| **텍스트 처리** | ✅ Files.readString() | 인코딩 처리 자동 |
| **바이너리 데이터** | ✅ ObjectInputStream | 직렬화/역직렬화 |
| **네트워크 통신** | ✅ Socket의 InputStream | 소켓과 통합 |
| **옛날 코드** | ⚠️ FileInputStream | 레거시 호환 필요 시만 |

### 장점

| 장점 | 설명 |
|------|------|
| **통일된 인터페이스** | 소스 종류와 관계없이 동일하게 처리 |
| **다양한 소스 지원** | 파일, 네트워크, 메모리 등 |
| **보조 스트림** | 버퍼링, 암호화, 압축 등 기능 확장 |
| **직렬화 지원** | 객체를 파일에 저장/복원 |

### 단점

| 단점 | 설명 |
|------|------|
| **바이트 단위** | 문자 처리 시 인코딩 신경 필요 |
| **블로킹 I/O** | read() 호출 시 데이터 올 때까지 대기 |
| **복잡성** | 보조 스트림 조합으로 코드 복잡 |
| **성능** | 대규모 데이터는 NIO Selector가 나음 |

### 특징

#### 1. 스트림의 분류

```text
InputStream/OutputStream (바이트 기반)
├── FileInputStream/FileOutputStream
├── ByteArrayInputStream/ByteArrayOutputStream
├── PipedInputStream/PipedOutputStream
├── BufferedInputStream/BufferedOutputStream (보조)
├── DataInputStream/DataOutputStream (보조)
└── ObjectInputStream/ObjectOutputStream (보조)

Reader/Writer (문자 기반)
├── FileReader/FileWriter
├── InputStreamReader/OutputStreamWriter (변환)
├── BufferedReader/BufferedWriter (보조)
└── PrintWriter (보조)
```

#### 2. try-with-resources로 자동 리소스 해제

```java
// ❌ 구식: 수동으로 close() 호출
FileInputStream fis = null;
try {
    fis = new FileInputStream("file.txt");
    // 작업
} finally {
    if (fis != null) {
        fis.close();  // 예외 무시됨
    }
}

// ✅ 현대: try-with-resources (자동 close())
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 작업
}  // 자동으로 close()

// 여러 리소스
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt")) {
    // 작업
}
```

#### 3. 인코딩 지정

```java
// 문제: 기본 인코딩 사용
FileInputStream fis = new FileInputStream("file.txt");

// ✅ 올바른 방식: UTF-8 명시
FileInputStream fis = new FileInputStream("file.txt");
InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);

// 또는 Files 사용 (권장)
String content = Files.readString(Path.of("file.txt"), StandardCharsets.UTF_8);
```

#### 4. 파일 경로 (상대 vs 절대)

```java
// 상대경로: 현재 작업 디렉토리 기준
Path.of("config/app.txt");
Path.of("../logs/error.log");

// 절대경로: 파일시스템 루트부터
Path.of("/usr/local/config/app.txt");  // Unix
Path.of("C:\\data\\file.txt");          // Windows

// 정규 경로: . / .. 제거
Path path = Path.of("/app/../app/config/./test.txt");
Path normalized = path.normalize();  // /app/config/test.txt
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 리소스 누수**

```java
// ❌ 예외 발생 시 close() 미실행
FileInputStream fis = new FileInputStream("file.txt");
byte[] data = new byte[1024];
int n = fis.read(data);
fis.close();  // 예외 발생하면 실행 안됨
```

**✅ try-with-resources 사용:**
```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    byte[] data = new byte[1024];
    int n = fis.read(data);
}  // 자동으로 close()
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 인코딩 불일치**

```java
// ❌ 기본 인코딩 사용 (시스템마다 다름)
String content = new String(bytes);  // Windows: EUC-KR, Linux: UTF-8

// ✅ 명시적 인코딩 지정
String content = new String(bytes, StandardCharsets.UTF_8);
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 버퍼링 필수**

```java
// ❌ 바이트 단위 읽기 (느림)
try (FileInputStream fis = new FileInputStream("large-file.txt")) {
    int byte1, byte2, byte3;
    while ((byte1 = fis.read()) != -1) {  // 반복할 때마다 I/O
        // 처리
    }
}

// ✅ 버퍼를 사용한 읽기 (빠름)
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("large-file.txt"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // 처리
    }
}
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **InputStream** | 데이터 읽기 (바이트 기반) |
| **OutputStream** | 데이터 쓰기 (바이트 기반) |
| **Reader/Writer** | 문자 기반 처리 |
| **Files (NIO)** | 현대 표준 파일 API |
| **try-with-resources** | 자동 리소스 해제 |
| **인코딩** | 반드시 명시적으로 지정 |
| **버퍼링** | 대용량 데이터는 버퍼 필수 |

---

**관련 파일:**
- [Charset](Charset_Encoding.md) — 문자 인코딩
- [network](network.md) — 네트워크 소켓 I/O
