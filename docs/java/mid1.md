### 2. 김영한의 실전 자바 - 중급 1편
- Object
  - 최상위 클래스
  - 모든 클래스를 품어줄 수 있다.
  ```java
    public class ObjMain {
        public static void main(String[] args){
              Object[] objects = {new Dog(), new Cat()};
              for(Object obj : objects) {
                  obj.sound();
              }
          }
      }
      class Dog {
          public void sound() {
              System.out.println("멍멍");
          }
      }
      class Cat{
          public void sound() {
              System.out.println("야옹");
          }
      }
  ```
- 불변 객체 VS 가변 객체
  - String
      - 불변 객체
      - new 키워드를 사용하지 않은 중복된 문자열은 문자열 풀을 사용한다.
          - Tip! 문자열 풀은 힙 영역을 사용한다.
          - Tip! 해시 알고리즘을 사용하여 문자열을 빠르게 찾아간다.
      - 문자열을 더할 때 새로운 인스턴스를 만들어야 한다.
    ```java
    public class Main {
        public static void main(String[] args){
          String a = "TEST"; 
          String b = "TEST"; // a랑 동일한 문자열로 힙 영역에 같은 주소를 참조한다.
          String c = a + b; // 불변은 새로운 인스턴스를 만들어 반환한다. 예시) new StringBuilder().append(a).append(b).toString();
        }
    }
    ```
  - StringBuilder
    - 가변 객체
      - 메서드 체이닝 기법 사용
      - 문자열을 더할 때 새로운 인스턴스를 만들기 싫어 위 클래스를 사용한다.
      - 사이드 이펙트를 방지하기 위해 마지막에 toString()을 사용해 String(불변) 인스턴스에 값을 담아준다.
      ```java
      public class Main {
        public static void main(String[] args){
          StringBuilder sb = new StringBuilder();
          sb.append("Hello ").append("World");
          String result = sb.toString();
          System.out.println(result);
        }
      }
      ```
- 메서드 체이닝
  - 메서드에서 자기 참조값을 반환하면 연속하여 메서드를 호출할 수 있다.
    ```java
    public class MethodChainingMain {
      public static void main(String[] args){
          Address address = new Address();
          // 자신의 인스턴스를 반환하여 메서드를 연속적으로 사용이 가능하다.
          System.out.println(address.add(50).add(40));
      }
    }
    class Address {
      private int value;
      public Address add(int value) {
          this.value += value;
          return this;
      }
    }
    ```
- 래퍼 클래스
  - AutoBoxing 지원한다.
  - 기본형보단 속도는 느리다.
      - 기본형은 스택 영역, 래퍼는 힙 영역에 저장 및 GC 대상
  - Class Class
    - 클래스의 메타 정보를 가져올 수 있다.
    - 주요기능 : 모든 필드, 메서드, 부모, 인터페이스 조회
    - 클래스의 정보를 읽고 사용하는 것을 리플렉션이라고 한다.
  - System Class
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
  - Math Class
  - Random Class
  - Type-safe Enum Pattern
  ```java
  public class Grade {
  private int point;
  private Grade(int point) {
      this.point = point;
  }
  private static final Grade GOLD = new Grade(10);
  private static final Grade DIAMOND = new Grade(20);
  public int getPoint() {
      return this.point;
  }
  }
  ```
- Enum
```java
    enum Grade {
      GOLD(10), DIAMOND(20);
      private int point;
      Grade(int point) {
          this.point;
      }
      public int getPoint() {
          return this.point;
      }
    }
``` 
- 날짜와 시간
  - 중첩 클래스, 내부 클래스
    - 사용 이유
      - 논리적 그룹화
      - 캡슐화
      - 정적 중첩 클래스
    ```java
    public class Main {
        public static void main(String[] args){
            // 중첩 클래스 접근 시, 아래와 같이 사용
            Human.Heart humanHeart = new Human.Heart();
            humanHeart.print();
        }
    }
    class Human {
        private static int outClassValue = 3;
        private int outInstanceValue = 2;
            static class Heart { 
                private int innerInstanceValue = 1;
                public void print() {
                // 바깥 클래스 변수 접근O
                System.out.println(innerInstanceValue);
                // 바깥 클래스 인스턴스 변수 접근X
                // System.out.println(outInstanceValue);
                // 내부 클래스 인스턴스 변수 접근O
                System.out.println(outClassValue);
            }
        }
    }
    ```
- 내부 클래스
  - 주의점: 내부 클래스는 정적이 아닌 이상, 메모리 누수가 발생할 수 있다.
  - 내부 클래스
  ```java
    public class Main {
        public static void main(String[] args){ 
            // 내부 클래스 접근 시, 외부 클래스 인스턴스를 생성 후 참조하여 접근해야 한다.
            Human human = new Human();
            Human.Heart heart = human.new Heart();
        }
    }
    class Human {
        private static int outClassValue = 3;
        private int outInstanceValue = 2;
        class Heart {
            private int innerInstanceValue = 1;
            public void print() {
                // 바깥 클래스 변수 접근O
                System.out.println(innerInstanceValue);
                // 바깥 클래스 인스턴스 변수 접근O
                System.out.println(outInstanceValue);
                // 내부 클래스 인스턴스 변수 접근O
                System.out.println(outClassValue);
            }
        }
    }
    ```
- 지역 클래스
```java
public class LocalOuter {
    private int outerVar = 3;
    public Printer process(final int paramVar) {
        // fianl이 필수는 아니지만, 사실상 final로 값이 바뀌면 안된다.
        // 캡쳐 변수는 인스턴스 변수 영역에 생성된다.
        final int localVar = 1;
        class Inner implements Printer {
            @Override
            public void print() {
                int value = 0;
                System.out.println(value);
                System.out.println(localVar);
                System.out.println(paramVar);
                System.out.println(outerVar);
            }
        }
        return new Inner();
    }
    public static void main(String[] args){
        LocalOuter outer = new LocalOuter();
        // 인스턴스에 지역 변수를 캡쳐하여 넣어둔다.
        Printer printer = outer.process(2);
        printer.print();
    }
}
interface Printer {
    void print();
}
```
- 익명 클래스
```java
public class AnonymousOuter {
    public void process() {
        // 생성과 동시에 구현이 가능하다.
        Printer printer = new Printer() {
            public void print() {
                System.out.println("TEST");
            }
        }; 
        printer.print();
    }
    public static void main(String[] args){
        AnonymousOuter anonymousOuter = new AnonymousOuter();
        anonymousOuter.process();
    }
}
interface Printer {
    void print();
}
```
- 예외처리
    - 예외 계층
        - Object
            - Throwable
                - Exception
                    - 애플리케이션 로직에서 사용할 수 있는 실질적인 최상위 예외이다.
                    - 체크 예외
                        - 장점
                            - 개발자가 실수로 예외를 누락하지 않도록 컴파일러를 통해 문제를 잡아주는 안전 장치이다.
                        - 단점
                            - 개발자가 모든 체크 예외를 반드시 잡거나 던지도록 처리해야 하기 때문에, 크게 신경쓰고 싶지 않은 예외까지 모두 챙겨야 한다.
                        - 컴파일러가 체크하는 체크 예외이다.
                        - Exception 상속 받으면 체크 예외가 된다.
                            - SQLException
                            - IOException
                    - 언체크 예외, 런타임 예외
                        - 장점
                            - 신경쓰고 싶지 않은 언체크 예외를 무시할 수 있다.
                        - 단점
                            - 개발자가 실수로 예외를 누락할 수 있다.
                        - 컴파일러가 체크 하지 않는 언체크 예외이다.
                        - 런타임 에러와 그 하위 에러를 런타임 예외라고 많이 부른다.
                        - RunTimeException 상속 받으면 언체크 예외가 된다.
                            - NullPointerException
                            - IllegalArgumentException
                        - throws 키워드를 생략할 수 있다.
                - Error
                    - 시스템 에러
                        - 애플리케이션에서 복구가 불가능한 시스템 예외이다.
                        - 애플리케이션 개발자는 이 예외를 잡으려고 해서는 안된다.
                    - OutOfMemoryError
    - 예외 기본 규칙
        - 예외는 잡아서 처리하거나, 밖으로 던져야 한다.
        - 예외를 잡거나 던질 때, 지정한 예외뿐 아니라 그 예외의 자식들도 함께 처리할 수 있다.
            - Exception catch 잡으면 그 하위 예외도 모두 잡을 수 있다.
            - Exception throws 던지면 그 하위 예외도 모두 던질 수 있다.
    - 예외
        - try
            - 정상 흐름
        - catch
            - 예외 흐름
        - finally
            - 반드시 호출해야 하는 마무리 흐름
    - try-with-resources
        - 자바7에서 돌입
        - AutoCloseable 인터페이스 사용
        - Exception 발생 시, 자동으로 close 메서드 호출
            - catch 보다 먼저 실행
        - 장점
            - 리소스 누수 방지
                - 모든 리소스가 제대로 닫히도록 보장한다.
            - 코드 간결성 및 가독성 향상
                - 명시적인 close() 호출이 필요 없어 코드가 더 간결하고 읽기 쉬워진다.
            - 스코프 범위 한정
                - 하단의 코드와 같이 try 블럭 안으로 Resource 인서턴스가 한정된다.
            - 더 빠른 자원 해제
                - 기존 : try -> catch -> finally 로 catch 이후에 자원을 반납했다.
                - 변경 : try -> close -> catch 를 호출한다.
      ```java
      public class Main {
        public static void main(String[] args){
          try (Resource resource = new Resource()) {
              throw new RuntimeException();
          }
        }
      }
      class Resources extends AutoCloseable {
        @Override
        public void close() {
            System.out.println("finally");
        }
      }
      ```