> [← 홈](/study/) · [Java](/java/java/) · [I/O·네트워크·리플렉션](/java/io-network/)

## localhost VS 127.0.0.1
```markdown
두 개념 모두 자기 자신의 컴퓨터를 가리키지만,
표현 계층·해석 방식·네트워크 동작 특성이 다릅니다.
```

### localhost
- 호스트 이름(hostname)
- 사람이 읽기 쉬운 논리적 식별자
- OS의 이름 해석 시스템(DNS/hosts 파일 등)을 통해 IP로 변환됨.
```markdown
127.0.0.1 localhost
::1 localhost
```

#### 특징
1. DNS 해석 필요
```markdown
1. /etc/hosts 파일 사용
2. 네트워크 설정에 따라 IP가 바뀔 수 있음
3. localhost가 다른 IP로 지정되는 사례 존재
```
2. IPv6 우선 가능
```markdown
현대 OS에서 먼저 해석되는 경우 존재
- localhost → ::1 (IPv6 loopback)
=> 즉 IPv4 서버인데 접속 실패 발생 가능.
```
3. 도메인 취급됨
```markdown
브라우저 기준 localhost ≠ 127.0.0.1
1. 쿠키 저장 분리
2. CORS 정책 영향
3. 인증 세션 충돌 가능
위 3가지 영향을 받음
```

### 127.0.0.1
- IPv4 표준에서 지정된 Loopback IP 주소
- 자기 자신 네트워크 인터페이스로 즉시 연결됨.
```markdown
127.0.0.0 ~ 127.255.255.255
```

#### 특징
1. 네트워크 스택 내부 처리
```markdown
외부 NIC(Network Interface Card) 안 거침.
즉 프로세스 → 커널 TCP/IP 스택 → 자기 자신 -> 실제 네트워크 트래픽 없음.
```
2. DNS 영향 없음
```markdown
즉시 연결됨
-> 이름 해석 실패 없음
-> hosts 설정 영향 없음
```
3. 서버 Binding 영향 큼
```markdown
bind 127.0.0.1 
-> 로컬만 접근 가능
bind 0.0.0.0 
-> 외부 접근 가능
```
4. 네트워크 테스트용 필수 주소
```markdown
서버 alive 확인
TCP/IP 스택 테스트
방화벽 점검
예시) ping 127.0.0.1
```

## Socket VS ServerSocket

### Socket
`Socket`은 **클라이언트 측 통신 엔드포인트**다.  
TCP/IP 기반에서 특정 서버의 IP와 Port로 연결을 시도하고, 연결이 성립되면 양방향 스트림 통신을 수행한다.

#### 특징
- 역할: **요청(Request) 주체**
- 연결 대상: 서버 IP + Port
- 통신 방식: Full-duplex (입출력 스트림 동시 가능)

#### 예시
```java
Socket socket = new Socket("127.0.0.1", 8080);
InputStream in = socket.getInputStream();
OutputStream out = socket.getOutputStream();
```

### ServerSocket
ServerSocket은 서버 측에서 연결을 수신하는 객체다.
클라이언트의 접속 요청을 기다리고 있다가 accept()를 통해 연결을 생성한다.

#### 특징
- 역할: 연결 대기(Listen)
- 바인딩: 특정 Port에 바인딩
- accept() 호출 시 새로운 Socket 객체 반환

#### 예시
```markdown
ServerSocket serverSocket = new ServerSocket(8080);

while (true) {
    Socket client = serverSocket.accept(); // 클라이언트가 올 때까지 메인 스레드는 멈춰 있음
}
```

### Thread가 왜 필요한가?
메인 스레드가 accept()에 물려 있으면 한 번에 1개밖에 처리 못 하니까,
연결마다 별도의 실행 단위(Thread)를 분리하는 것이다.
```markdown
while (true) {
    Socket client = serverSocket.accept();
    new Thread(() -> handle(client)).start();
}
```

## JVM Shutdown Hook
- [JVM]이 “정상 종료”될 때 실행되는 콜백 스레드
- [JVM]에 미리 등록해두면, 프로세스가 내려가기 직전에 실행된다.
```markdown
Runtime.getRuntime().addShutdownHook(
    new Thread(() -> {
        System.out.println("JVM 종료 직전 실행");
    })
);
```

### 실행되는 경우
1. main() 종료
2. System.exit()
3. Ctrl + C (SIGINT)
4. kill (SIGTERM)

### 실행되지 않는 경우
1. kill -9 (SIGKILL)
2. JVM crash
3. OS 강제 종료
4. 전원 차단

## setSoTimeout(int timeout)
setSoTimeout(int timeout)은 블로킹 I/O에서 무한 대기 문제를 해결하기 위해 등장했습니다.

### 사용 예시
```markdown
socket.setSoTimeout(5000);

해당 소켓에서 read() 호출 시
5초 동안 데이터가 도착하지 않으면
SocketTimeoutException 발생
```

## FIN VS RST  

### FIN
- TCP 연결의 정상 종료 (Graceful Shutdown)
- `나는 더 이상 보낼 데이터가 없다`는 선언
```markdown
1) A → B : FIN
2) B → A : ACK   (A의 송신 종료 확인)
3) B → A : FIN   (B도 송신 종료)
4) A → B : ACK   (최종 종료 확인)
```

### RST
- TCP 연결의 비정상 종료 (Abortive Close)
- 상태 전이 없이 바로 CLOSED

| 구분        | FIN   | RST   |
|-----------|-------|-------|
| 종료 방식     | 정상 종료 | 강제 종료 |
| 데이터 보존    | 보장    | 보장 안됨 | 
| 상태 전이     | 단계적   | 즉시 종료 |
| 재전송 가능 여부 | 가능    | 불가능   |