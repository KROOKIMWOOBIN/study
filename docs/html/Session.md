### HTTP Session
- HTTP는 무상태 프로토콜
- 이 문제를 해결하기 위해 세션 개념 등장

#### Session 구조
- 서버: 사용자별 상태 정보를 저장
- 클라이언트(브라우저): 그 사용자를 시벽할 수 있는 세션 ID만 보관
```markdown
[브라우저] -> 세션ID -> [서버]
```

#### Session 구성 요소
| 요소           | 위치                  | 설명                 |
|--------------|---------------------|--------------------|
| Session ID   | 브라우저(쿠기)            | 사용자를 식별하는 고유 키     |
| Session Data | 서버 메모리 / DB / Redis | 로그인 정보, 권한, 사용자 상태 |

#### Session 동작 흐름
- 로그인 시
    - 사용자가 ID/PW로 로그인 요청
    - 서버가 인증 성공
    - 서버가 세션 생성
  ```http
  Session ID: ABC123
  Session Data: { userId: 10, role: ADMIN }
  ``` 
    - 서버가 응답 헤더에 쿠기 설정
  ```http
  Set-Cookie: JSESSIONID=ABC123; HttpOnly; Path=/
  ```
- 이후 요청
    - 브라우저가 자동으로 쿠기 전송
  ```http
  Cookie: JSESSIONID=ABC123
  ```
    - 서버가 세션 저장소에서 조회
    - 로그인된 사용자로 판단

#### JSESSIONID란 무엇인가?
- 정의
    - JSESSIONID는 Java(Servlet / Spring) 기반 서버에서 사용하는 기본 세션 쿠키 이름
    - Tomcat에 web.xml에서 기본 세션 쿠기 이름 변경 가능
- 중요한 점
    - JSESSIONID 자체에는 로그인 정보가 없다
    - 단순한 랜덤 문자열
    - 진짜 로그인 정보는 서버에만 존재

#### 왜 JSESSIONID를 복사하면 로그인도 유지되는가?
- 핵심 이유 (아주 중요)
    - 서버는 “세션 ID를 가진 자 = 로그인한 사용자”로 신뢰하기 때문
- 실제 상황 예시
    - A 사용자가 로그인
    - 서버: JSESSIONID = XYZ999
    - 누군가 이 값을 복사해서 다른 브라우저에 붙여넣음
    - 서버 입장에서는: "아, XYZ999 세션이네 → 로그인된 사용자군"로 인식

#### 이것이 의미하는 보안 개념
- 세션 ID = 로그인 권한 그 자체
- 세션 ID가 탈취되면 아래와 같은 상황 발생
    - 계정 탈취와 동일
    - 비밀번호 변경 없이도 접근 가능

#### 세션 탈취(Session Hijacking) 기법
|기법|설명|
|--|--|
|XSS|JS로 document.cookie 탈취|
|네트워크 스니핑|HTTPS 없을 때 패킷 가로채기|
|악성 프록시|중간자 공격|
|로그 유출|서버 로그에 세션 출력|

#### 세션 탈취 방어 방법
```html
Set-Cookie: JSESSIONID=ABC;
    HttpOnly;
    Secure;
    SameSite=Strict
```
|옵션| 의미 |
|--|--|
|HttpOnly|JS에서 접근 불가 (XSS 방어)|
|Secure|HTTPS에서만 전송|
|SameSite|CSRF 방어|
- 로그인 시 세션 재발급