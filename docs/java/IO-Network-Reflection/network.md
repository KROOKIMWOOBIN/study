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