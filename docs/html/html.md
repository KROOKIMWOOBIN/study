> [← 홈](/study/)

# HTTP / Web

모든 개발자를 위한 HTTP 웹 기본 지식 정리.

---

| 주제 | 한 줄 설명 |
| --- | --- |
| [IP](#ip---internet-protocol) | 인터넷 프로토콜 — 패킷 기반 데이터 전달 |
| [TCP / UDP](#tcp---전송-제어-프로토콜transmission-control-protocol) | 전송 계층 프로토콜 — 신뢰성 vs 속도 |
| [PORT / DNS](#port) | 포트 번호와 도메인 이름 시스템 |
| [URI / URL](#uri---resource-identifier) | 리소스 식별자와 위치 표기 방식 |
| [Stateful / Stateless](#stateful-vs-stateless) | 상태 유지 vs 무상태 설계 |
| [비연결성](#비연결성) | HTTP 연결 모델의 특성 |
| [쿠키](#쿠기) | 세션 쿠키, 영속 쿠키 |
| [캐시](#캐시) | 캐시 제어 헤더, ETag, 검증 헤더 |
| [세션](./Session.md) | HTTP Session, 세션 탈취 방어 |

---

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
| 레벨 | 단계 |
| --- | --- |
| 애플리케이션 | 웹 브라우저, 네트워크 게임 |
| 애플리케이션 | 소켓 라이브러리 |
| OS | TCP, UDP |
| OS | IP |
| 네트워크 인터페이스 | LAN드라이버, LAN장비 |

1. 프로그램이 전송할 데이터 작성
2. 소켓 라이브러리를 통해 전달
3. TCP 정보 생성, 메시지 데이터 포함
4. IP 패킷 생성, TCP 데이터 포함

---

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

---

### UDP - 사용자 데이터그램 프로토콜(User Datagram Protocol)
- 데이터 전달 및 순서가 보장되지 않지만, 단순하고 속도가 빠름
- IP + PORT + 체크섬 정도

| 구분 | TCP | UDP |
| --- | --- | --- |
| 연결 | 연결지향 (3-way handshake) | 비연결 |
| 신뢰성 | 보장 | 미보장 |
| 속도 | 상대적으로 느림 | 빠름 |
| 용도 | 일반 웹, 파일 전송 | 스트리밍, 게임, DNS |

---

### PORT
- 0 ~ 65535 : 할당 가능
- 0 ~ 1023 : 잘 알려진 포트, 사용하지 않는 것이 좋음

| 포트 | 서비스 |
| --- | --- |
| 20, 21 | FTP |
| 23 | TELNET |
| 80 | HTTP |
| 443 | HTTPS |

---

### DNS - 도메인 이름 시스템(Domain Name System)
- 전화번호부 역할
- 도메인 명을 IP 주소로 변환

---

### URI - Resource Identifier
- Uniform : 리소스를 식별하는 통일된 방식
- Resource : 자원, URI로 식별할 수 있는 모든 것
- Identifier : 다른 항목과 구분하는데 필요한 정보

#### URL - Resource Locator (리소스가 있는 위치를 지정)
```
scheme://[userinfo@]host[:port][/path][?query][#fragment]
https://www.google.com:443/search?q=hello&hl=ko
```
| 구성 요소 | 값 | 설명 |
| --- | --- | --- |
| 프로토콜 | `https` | 통신 방식 |
| 호스트명 | `www.google.com` | 도메인 |
| 포트 | `443` | 생략 가능 |
| 패스 | `/search` | 리소스 경로 |
| 쿼리 | `?q=hello&hl=ko` | 파라미터 |
| userinfo | - | 인증 정보 (거의 미사용) |
| fragment | `#section` | HTML 내부 북마크 (서버 미전송) |

#### URN - Resource Name (리소스에 이름을 부여)

---

### Stateful VS Stateless
#### Stateful (상태유지)
- 항상 같은 서버를 바라봐야 한다
- 서버 장애 시 상태 유실
- 예) 로그인 유지

#### Stateless (무상태)
- 스케일 아웃(수평 확장) 유리
- 클라이언트가 요청마다 상태 정보를 함께 전송
- 예) 로그인이 필요 없는 페이지

---

### 비연결성
#### 장점
- HTTP는 기본적으로 연결을 유지하지 않는 모델
- 1시간 동안 수천 명이 사용해도 실제 서버에서 처리하는 요청은 수십 개 이하

#### 단점
- TCP/IP 재연결 필요
- 지금은 HTTP 지속 연결(Persistent Connection)로 문제 해결

---

### 쿠기
#### 세션 쿠기
- 만료 날짜를 생략하면 브라우저 종료 시 소멸

#### 영속 쿠기
- 만료 날짜를 지정하면 해당 날짜까지 유지

---

### 캐시
#### 장점
- 브라우저 로딩 속도가 빨라진다
- 비싼 네트워크 사용량을 줄일 수 있다

#### 캐시 제어 헤더
- `Cache-Control` : 캐시 제어
- `Pragma` : 캐시 제어 (하위 호환)
- `Expires` : 캐시 유효 기간 (하위 호환)

#### Cache-Control (캐시 지시어)
| 지시어 | 설명 |
| --- | --- |
| `max-age` | 유효시간 (초 단위) |
| `no-cache` | 캐시 가능하나 항상 원 서버에 검증 후 사용 |
| `no-store` | 민감 정보 → 저장 금지 |
| `must-revalidate` | 만료 후 원 서버 검증 필수, 실패 시 504 |
| `public` | 모든 사용자 캐시 가능 |
| `private` | 해당 사용자만 캐시 (기본값) |
| `s-maxage` | 프록시 서버 전용 유효시간 |

#### 캐시 무효화
```
Cache-Control: no-cache, no-store, must-revalidate
Pragma: no-cache
```

#### 검증 헤더
| 헤더 | 설명 | 응답 코드 |
| --- | --- | --- |
| `Last-Modified` | 데이터 최종 수정일 | 변경 200, 미변경 304 |
| `ETag` | 캐시 데이터의 고유 버전 이름 | 불일치 200, 일치 304 |
