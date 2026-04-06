## Stream
- 데이터의 연속적인 흐름
- 바이트 단위로 데이터를 처리한다.

### InputStream
- 외부 데이터 소스로부터 바이트를 읽는 추상 클래스
- 파일, 네트워크, 메모리 등 다양한 입력 소스를 동일한 방식으로 처리하기 위해 사용
 
### OutputSteam
- 프로그램의 데이터를 외부로 바이트 단위로 출력하는 추상 클래스

## 직렬화
- 클래스를 직렬화 하기 위해서는 [Serializable]를 구현해줘야 한다.
  - [JVM]에게 `이 객체는 직렬화해도 안전하다`는 명시적 표시(marker) 역할을 하기 때문입니다.
```java
public class Member implements Serializable {}
```

## 경로

### 상대경로
- 현재 기준 위치 기준으로 표현하는 경로
```markdown
./config/app.yml
../logs/error.log
```

### 절대경로
- 파일 시스템 루트부터 전체 경로를 모두 표현
```markdown
/usr/local/app/config.yml
C:\app\data.txt
```

### 정규경로
- 파일 시스템 기준으로 해석된 ‘실제 최종 경로’
```markdown
/app/../app/config/./test.txt
→ /app/config/test.txt
```

## File VS Files

### File
```markdown
파일이나 디렉토리의 경로 + 메타 정보 표현 객체
실제 파일 내용을 직접 다루는 객체는 아님.
```

#### 특징
- Java 1.0부터 존재 (구 IO API)
- 파일 존재 여부 확인
- 디렉토리 생성/삭제
- 경로 문자열 기반 처리

#### 사용 예시
```markdown
File file = new File("test.txt");

file.exists();
file.isDirectory();
file.delete();
```

#### 단점
- 기능 제한적
- 예외 처리 애매함
- 심볼릭 링크 대응 부족
- 문자열 경로 처리라 안전성 떨어짐

### Files + Path
```markdown
Path = 경로 표현
Files = 파일 작업 실행
```

#### 핵심
File = 옛날 파일 객체
Path = 경로 표현 객체 (현대 표준)
Files = 실제 파일 작업 API

#### Files
```markdown
파일 실제 작업 수행하는 유틸리티 클래스
(static 메서드 기반)
```

##### 주요 기능
- 파일 읽기/쓰기
- 복사/이동
- 권한 관리
- 디렉토리 탐색
- 스트림 기반 처리

##### 사용 예시
```markdown
Path path = Path.of("test.txt");

Files.exists(path);
Files.readString(path);
Files.writeString(path, "hello");
```

##### 특징
- 예외 명확
- NIO 기반이라 성능/확장성 좋음
- 실무 표준 API

#### Path
```markdown
파일 경로 자체를 표현하는 객체.
```

##### 특징
- 경로 조작 기능 풍부
- OS 독립적 처리
- 정규화(normalize) 지원
- Files와 같이 사용

##### 예시
```markdown
Path path = Path.of("dir", "test.txt");

path.getFileName();
path.getParent();
path.normalize();
```