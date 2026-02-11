## Stream
- 데이터의 연속적인 흐름
- 바이트 단위로 데이터를 처리한다.

### InputStream
- 외부 데이터 소스로부터 바이트를 읽는 추상 클래스
- 파일, 네트워크, 메모리 등 다양한 입력 소스를 동일한 방식으로 처리하기 위해 사용
 
### OutputSteam
- 프로그램의 데이터를 외부로 바이트 단위로 출력하는 추상 클래스

### 직렬화
- 클래스를 직렬화 하기 위해서는 [Serializable]를 구현해줘야 한다.
  - [JVM]에게 `이 객체는 직렬화해도 안전하다`는 명시적 표시(marker) 역할을 하기 때문입니다.
```java
public class Member implements Serializable {}
```