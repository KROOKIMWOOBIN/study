## 네트워크 통신 기초

### 왜 쓰는지

Java 애플리케이션이 **다른 컴퓨터와 통신**해야 하는 상황:
- 웹 서버-클라이언트 통신
- 마이크로서비스 간 통신
- IoT 기기와의 데이터 송수신
- 게임 멀티플레이 서버

네트워크를 통해 데이터를 주고받을 수 있어야 합니다.

<div class="concept-box" markdown="1">

**핵심**: Java 네트워크 API는 **Socket과 ServerSocket을 통해 TCP/IP 기반의 양방향 통신**을 제공합니다.

</div>

### 어떻게 쓰는지

#### 로컬호스트 vs 특정 IP

```java
// localhost: 호스트명 (DNS 해석 필요)
Socket socket1 = new Socket("localhost", 8080);

// 127.0.0.1: IPv4 Loopback 주소 (즉시 연결)
Socket socket2 = new Socket("127.0.0.1", 8080);

// 원격 서버
Socket socket3 = new Socket("192.168.1.100", 8080);
Socket socket4 = new Socket("example.com", 80);
```

#### 기본 서버-클라이언트 구조

```java
// ===== 서버 =====
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("서버 시작. 포트: 8080");
        
        while (true) {
            // 클라이언트 접속 대기 (블로킹)
            Socket client = server.accept();
            System.out.println("클라이언트 연결됨: " + client.getInetAddress());
            
            // 클라이언트와 통신 (각 클라이언트마다 스레드 생성)
            new Thread(() -> handleClient(client)).start();
        }
    }
    
    private static void handleClient(Socket client) throws IOException {
        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {
            
            // 데이터 수신
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            System.out.println("수신: " + message);
            
            // 데이터 송신
            String response = "Echo: " + message;
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// ===== 클라이언트 =====
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8080);
        System.out.println("서버에 연결됨");
        
        try (OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            
            // 데이터 송신
            String message = "Hello Server";
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
            
            // 데이터 수신
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            String response = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            System.out.println("수신: " + response);
        } finally {
            socket.close();
        }
    }
}
```

#### Socket vs ServerSocket

```java
// ServerSocket: 서버 측 (listening, accepting)
ServerSocket server = new ServerSocket(8080);  // 포트 바인딩
Socket client = server.accept();               // 클라이언트 대기

// Socket: 클라이언트 측 (connecting, communicating)
Socket socket = new Socket("127.0.0.1", 8080);  // 서버에 연결
InputStream in = socket.getInputStream();       // 입력 스트림
OutputStream out = socket.getOutputStream();    // 출력 스트림
```

#### 타임아웃 설정 (무한 대기 방지)

```java
Socket socket = new Socket("127.0.0.1", 8080);

// 읽기 타임아웃: 5초 이내에 데이터 없으면 예외 발생
socket.setSoTimeout(5000);

try {
    InputStream in = socket.getInputStream();
    byte[] buffer = new byte[1024];
    int bytesRead = in.read(buffer);  // 5초 동안 데이터 없으면 SocketTimeoutException
} catch (SocketTimeoutException e) {
    System.out.println("타임아웃: 서버로부터 응답 없음");
}

// 연결 타임아웃
Socket socket2 = new Socket();
SocketAddress address = new InetSocketAddress("192.168.1.1", 8080);
socket2.connect(address, 3000);  // 3초 내에 연결되지 않으면 예외
```

#### FIN vs RST (TCP 종료)

```java
// 1️⃣ 정상 종료 (FIN)
socket.close();  // 정상 종료 신호 (FIN) 전송
// 양쪽이 graceful shutdown 수행

// 2️⃣ 강제 종료 (RST)
// SO_LINGER 옵션으로 강제 종료 강제
socket.setSoLinger(true, 0);  // 즉시 RST 전송
socket.close();
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **웹 서버** | ✅ ServerSocket | 다수 클라이언트 동시 처리 |
| **클라이언트-서버 통신** | ✅ Socket | 양방향 통신 필요 |
| **실시간 메시지** | ✅ Socket | 지속적 연결 유지 |
| **HTTP 통신** | ❌ 직접 사용 | HttpClient, RestTemplate 사용 |
| **높은 성능** | ❌ 스레드 기반 | NIO 기반 Selector 사용 |

### 장점

| 장점 | 설명 |
|------|------|
| **표준 API** | JDK에 포함, 학습 자료 풍부 |
| **양방향 통신** | 서버-클라이언트 양쪽에서 데이터 주고받기 |
| **유연성** | 프로토콜, 형식 자유 선택 |
| **스트림 기반** | InputStream/OutputStream으로 일관된 처리 |
| **신뢰성** | TCP 기반으로 데이터 손실 방지 |

### 단점

| 단점 | 설명 |
|------|------|
| **스레드 오버헤드** | 클라이언트마다 스레드 생성 (수천 개 동시 연결 불가) |
| **블로킹 I/O** | accept(), read()에서 스레드 멈춤 |
| **복잡한 프로토콜** | 직렬화, 마샬링 수동 처리 |
| **에러 처리** | IOException 처리 복잡 |
| **성능 한계** | C10K 문제 (1만 동시 연결 곤란) |

### 특징

#### 1. localhost vs 127.0.0.1

```java
// localhost: 호스트명 (DNS 해석 필요)
// - /etc/hosts에서 IP로 변환
// - IPv4/IPv6 우선순위 에 따라 다를 수 있음
// - 브라우저에서 127.0.0.1과 다르게 취급 (쿠키, CORS)

// 127.0.0.1: IPv4 Loopback (즉시 연결)
// - 자신의 컴퓨터만 접근 가능
// - DNS 영향 없음
// - 네트워크 트래픽 없음 (커널 내부 처리)

Socket s1 = new Socket("localhost", 8080);    // DNS 해석 후 연결
Socket s2 = new Socket("127.0.0.1", 8080);    // 즉시 연결
```

#### 2. TCP 연결 단계 (Three-Way Handshake)

```text
클라이언트 ─── SYN ──→ 서버
        ← SYN-ACK ←
        ─── ACK ──→
        ↓
     연결됨
```

```java
Socket socket = new Socket("127.0.0.1", 8080);  // 위의 3단계 자동 실행
// 이 시점에서 연결 수립 완료
```

#### 3. Graceful Shutdown vs Abortive Close

```java
// Graceful Shutdown (FIN)
socket.close();  // 정상 종료 신호

// 1. 남은 데이터 전송
// 2. FIN 신호 전송
// 3. 상대방 ACK 대기
// 4. TIME_WAIT 상태에 머무름

// Abortive Close (RST)
socket.setSoLinger(true, 0);  // 강제 종료
socket.close();
// 즉시 RST 신호 → 바로 CLOSED
```

#### 4. 포트 바인딩

```java
// 1️⃣ 특정 IP에만 바인딩
ServerSocket server1 = new ServerSocket(8080, 50, InetAddress.getByName("127.0.0.1"));
// 로컬만 접근 가능

// 2️⃣ 모든 네트워크 인터페이스에 바인딩
ServerSocket server2 = new ServerSocket(8080);  // 0.0.0.0로 바인딩
// 외부에서도 접근 가능

// 3️⃣ 특정 인터페이스
ServerSocket server3 = new ServerSocket(8080, 50, InetAddress.getByName("192.168.1.100"));
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ 무한 대기 (Deadlock)**

```java
// accept()가 클라이언트 없으면 영원히 멈춤
ServerSocket server = new ServerSocket(8080);
Socket client = server.accept();  // 클라이언트 없으면 영구 대기
```

**✅ 올바른 방식:**
```java
// 스레드에서 실행하거나 타임아웃 설정
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> {
    try {
        Socket client = server.accept();
    } catch (IOException e) {}
});

// 또는 NIO Selector 사용
```

</div>

<div class="danger-box" markdown="1">

**❌ 포트 이미 사용 중 (Address already in use)**

```java
// 서버를 종료 후 바로 재시작하면
ServerSocket server = new ServerSocket(8080);  // ❌ Address already in use
```

**✅ 올바른 방식:**
```java
// SO_REUSEADDR 옵션 설정
ServerSocket server = new ServerSocket();
server.setReuseAddress(true);
server.bind(new InetSocketAddress(8080));
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 리소스 누수**

```java
// ❌ 예외 발생 시 close() 미실행
Socket socket = new Socket("127.0.0.1", 8080);
byte[] data = new byte[1024];
int n = socket.read(data);  // 예외 발생 시 socket.close() 안됨
```

**✅ try-with-resources 사용:**
```java
try (Socket socket = new Socket("127.0.0.1", 8080);
     InputStream in = socket.getInputStream()) {
    byte[] data = new byte[1024];
    int n = in.read(data);
}  // 자동으로 close()
```

</div>

<div class="warning-box" markdown="1">

**⚠️ C10K 문제 (1만 동시 연결)**

```java
// Thread 기반 수용 한계
for (int i = 0; i < 10000; i++) {
    new Thread(() -> {
        ServerSocket server = new ServerSocket(8080 + i);
        // 각 스레드: 1MB 스택
        // 10000개: 10GB 메모리 필요!
    }).start();
}

// ✅ NIO/Netty 사용
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **ServerSocket** | 클라이언트 접속 대기 |
| **Socket** | 클라이언트 연결 객체 |
| **InputStream/OutputStream** | 데이터 송수신 |
| **타임아웃** | setSoTimeout() |
| **종료** | FIN (정상) vs RST (강제) |
| **한계** | 동시 연결 수 제한 (스레드 오버헤드) |

---

**관련 파일:**
- [IO](IO.md) — 스트림 기본
- [Charset](Charset_Encoding.md) — 문자 인코딩
- [Thread](../thread/기본개념.md) — 멀티스레드 처리
