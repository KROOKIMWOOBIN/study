### IP - Internet Protocol
#### 인터넷 프로토콜 역할
- 지정한 IP(IP Address)에 데이터 전달
- 패킷(Packet)이라는 통신 단위로 데이터 전달
  - 노드(중간다리)를 통해 패킷을 전달

#### IP 패킷 정보
- 출발지 IP, 목적지 IP, 기타...
- 전송 데이터

#### IP 프로토콜의 한계
- 비 연결성
  - 패킷을 받을 대상이 없거나, 서비스 불능 상태에도 패킷 전송
- 비 신뢰성
  - 패킷 손실 가능성
  - 순서 보장 안됨
- 프로그램 구분
  - 같은 IP를 사용하는 서버에서 통신하는 애플리케이션이 여러 개일 경우 구분X

#### 인터넷 프로토콜 스택의 4계층
1. 애플리케이션 - HTTP, FTP
2. 전송 계층 - TCP, UDP
3. 인터넷 계층 - IP
4. 네트워크 인터페이스 계층

#### 프로토콜 계층
| 레벨         | 단계              |
|------------|-----------------|
| 애플리케이션     | 웹 브라우저, 네트워크 게임 |
| 애플리케이션     | 소켓 라이브러리        |
| OS         |TCP, UDP|
| OS         |IP|
| 네트워크 인터페이스 |LAN드라이버, LAN장비|

1. 프로그램이 전송할 데이터 작성
2. 소켓 라이브러리를 통해 전달
3. TCP 정보 생성, 메시지 데이터 포함
4. IP 패킷 생성, TCP 데이터 포함

### TCP - 전송 제어 프로토콜(Transmission Control Protocol)
#### TCP/IP 패킷 정보
- 출발지 IP, 목적지 IP, 기타...
- 출발지 PORT, 목적지 PORT, 전송 제어, 순서, 검증 정보... 
- 전송 데이터

#### TCP 특징
- 연결지향
  - 3 way handshake (가상 연결)
- 데이터 전달 보증
- 순서 보장
- 신뢰할 수 있는 프로토콜
- 현재는 대부분 TCP 사용

### UDP - 사용자 데이터그램 프로토콜(User Datagram Protocol)
- 데이터 전달 및 순서가 보장되지 않지만, 단순하고 속도가 빠름
- IP + PORT + 체크섬 정도

### PORT
- 0 ~ 65535 : 할당 가능
- 0 ~ 1023 : 잘 알려진 포트, 사용하지 않는 것이 좋음
- FTP : 20, 21
- TELNET : 23
- HTTP : 80
- HTTPS : 443

### DNS - 도메인 이름 시스템(Domain Name System)
- 전화번호부
- 도메인 명을 IP 주소로 변환

### URI - Resource Identifier
- Uniform : 리소스 식별하는 통일된 방식
- Resource : 자원, URI로 식별할 수 있는 모든 (제한 없음)
- Identifier : 다른 항목과 구부하는데 필요한 정보
#### URL - Resource Locator(리소스가 있는 위치를 지정)
##### 전체 문법
scheme://[userinfo@]host[:port][/path][?qurey][#flagment]
https://www.google.com:443/search?q=hello&hl=ko
- 프로토콜 : https
- 호스트명 : www.google.com
- 포트 : 443
- 패스 : /search
- 쿼리 파라미터 : ?q=hello&hl=ko
- userinfo : 시용자정보를 포함하여 인증(거의 사용하지 않음)
- flagment : html 내부 북마크 등에 사(서버에 전송하는 정보 아님)
#### URN - Resource Name(리소스에 이름을 부여)

### Stateful VS Stateless
#### Stateful(상태유지)
- 항상 같은 서버를 바라보고 있어야 한다.
- 상태 유지 예) 로그인
#### Stateless(무상태)
- 스케일 아웃(수평 확장 유리)
- 무상태 예) 로그인이 필요없는 사이트

### 비연결성
#### 장점
- HTTP는 기본 연결을 유지하지 않는 모델
- 1시간 동안 수천명이 사용해도 실제 서버에서 처리하는 요청은 수십개 이하
#### 단점
- TCP/IP 재연결 필요
- 지금은 HTTP 지속 연결로 문제 해결

### 쿠기
#### 세션 쿠기
- 만료 날짜를 생략하면 브라우저 종료 시 소멸
#### 영속 쿠기
- 만료 날짜를 지정하면 해당 날짜까지 유지

### 캐시
#### 장점
- 브라우저 로딩 속도가 빨라진다.
- 비싼 네트워크 사용량을 줄일 수 있다.

#### 캐시 제어 헤더
- Cache-Control : 캐시 제어
- Pragma : 캐시 제어(하위 호환)
- Expires : 캐시 유효 기간(하위 호환)

#### Cache-Control (캐시 지시어)
- max-age : 유효시간(초단위)
- no-cache : 데이터는 캐시해도 되지만, 항상 원(origin) 서버에 검증하고 사용
- no-store : 데이터 민감한 정보가 있으므로 저장하면 안됨
- must-revalidate
  - 캐시 만료 후 최초 조회시 원 서버에 검증해야 함
  - 원 서버 접근 실패 시 반드시 오류 발생 504
  - 캐시 유효 시간이라면 캐시를 사용함
- public : 모든 사용자
- private : 고유 사용자
- s-maxage : 프록시 서버만 해당
- age : 원 서버에 응답 후 프록시 캐시 내에 머문 시간(초)

#### 캐시 무효화
- no-cache, no-store, must-revalidate
  - 항상 검증하며, 저장도 금지하고, 만료된 캐시는 재사용 금지
- Pragma: no-cache
  - HTTP 1.0 하위 호환

#### 검증 헤더
- Last-Modified : 데이터 최종 수정일
  - 데이터 변경 시 : 200
  - 데이터 미변경 시 : 304
- ETag(Entity Tag)
  - 캐시용 데이터의 고유한 버전 이름 부여