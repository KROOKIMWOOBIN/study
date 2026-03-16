## System Class
- System 클래스는 JVM과 운영체제 수준의 시스템 기능을 접근하기 위한 유틸리티 클래스다.
- java.lang 패키지에 포함되어 있어 import 없이 사용 가능하다.
  - 모든 멤버가 static
  - 객체 생성 불가 (System 생성자 private)
  - JVM 실행 환경과 직접 연결된 기능 제공

### 표준 입출력 스트림
| 스트림          | 설명                 |
| ------------ | ------------------ |
| `System.in`  | 표준 입력 스트림 (키보드 입력) |
| `System.out` | 표준 출력 스트림 (콘솔 출력)  |
| `System.err` | 표준 오류 출력           |

### 시간 측정
| 메서드                          | 설명                           |
| ---------------------------- | ---------------------------- |
| `System.currentTimeMillis()` | 현재 시간을 밀리초(ms) 단위로 반환        |
| `System.nanoTime()`          | 나노초(ns) 단위 시간 반환 (정밀한 성능 측정) |

### 환경 변수 조회
- 운영체제 환경 변수 접근
- 읽기 전용
```markdown
String path = System.getenv("PATH");
System.out.println(path);
```
- 실무 예시
```markdown
DB_HOST
DB_PORT
API_KEY
```

### 배열 고속 복사
- 배열을 메모리 블록 단위로 복사한다.
```markdown
System.arraycopy(src, srcPos, dest, destPos, length)
```
- 예시
```markdown
int[] a = {1,2,3};
int[] b = new int[3];

System.arraycopy(a, 0, b, 0, 3);
```

### 특징
1. native 메서드
2. JVM 내부 최적화
3. 일반 `for`문보다 빠름
- 예) `ArrayList`에서도 사용됨

- 표준 입출력, 오류 스트림
    - 시간 측정
    - 환경 변수
        - 예시) System.getEnv();
    - 배열 고속 복사
        - 예시) System.arraycopy();
        - 메모리 블록 단위로 이동하여 빠름
    - 시스템 속성
        - 예시) Java version, properties
    - 프로그램 종료
        - 예시) System.exit(0);