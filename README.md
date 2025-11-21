# 기초부터 시작하는 개발자 생활

## 자바

### 1. 김영한의 실전 자바 - 기본편
- 핵심
  - 캡슐화
    - 목적 : 내부는 숨기고, 필요한 것만 공개해서 시스템을 안정적으로 유지하는 것
    - 기능
      1. 데이터 보호(무분별한 수정 방지)
      2. 객체 상태를 일관되게 유지
      3. 내부 구현을 숨겨서 변경에 강한 구조
      4. 보안, 안정성 증가
      ```java
      public class Animal {
      
        private String name;
        private int age;
      
        Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }
      
        public void setAge(int age) {
            if(checkAge(age)) {
                this.age = age;
            } else {
                System.out.println("0 이상의 나이로만 설정할 수 있습니다.");
            }
        }
      
        private boolean checkAge(int age) {
            return age >= 0;
        }
      }
      ```
  - 다형성
    - 목적 : 코드를 유현하게 만들고, 변경에 강하게 만들고, 재사용성을 높이기 위해서
    - 기능
      1. 코드 확장성이 좋아진다.
      2. 구현을 숨기고 역할만 바라보게 설계가 가능하다.
      3. 한 타입으로 여러 객체를 다룰 수 있어 코드가 단순해 진다.
      4. 중복 코드 제거
  - 추상화(Abstract, Interface)
    - 목적 : 구현이 아닌, 역할 중심으로 설계를 하여 변경에 강하고, 확장에 유연한 구조를 만들기 위해 
    - 기능
      - OCP(Open-Closed Principle)
        - 오픈 개방 원칙
        - (Open for extension)새로운 기능의 추가나 변경 사항이 생겼을 때, 기존 코드는 확장할 수 있어야 한다.
        - (Closed for modification)기존의 코드는 수정되지 않아야 한다.
  ```java
  public class Main {
    public static void main(String[] args){
        Animal[] animals = {new Dog(), new Cat()};
        for(Animal animal : animals) {
            animal.move();
            if(animal instanceof Dog) {
                ((Dog) animal).sound();
            }
        }
    }
  }
  interface AnimalInterface { 
    void move();
  }
  class Animal implements AnimalInterface {
    @Override
    public void move() {
        System.out.println("동물이 움직입니다.");
    }
  }
  class Dog extends Animal {
    @Override
    public void move() {
        System.out.println("강아지가 움직입니다.");
    }
    public void sound() {
        System.out.println("멍멍");
    }
  }
  class Cat extends Animal {
    @Override
    public void move() {
        System.out.println("고양이가 움직입니다.");
    }
  }
  ```
### 2. 김영한의 실전 자바 - 중급 1편
- 용어
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
      - 주의점 : 내부 클래스는 정적이 아닌 이상, 메모리 누수가 발생할 수 있다. 
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
## 스프링

### 1. 스프링의 핵심 원리 - 기본편
- 용어
  - SOLID
    - SRP(Single Responsibility Principle)
      - 단일 책임 원칙
      - 한 클래스는 하나의 책임만 가져야 한다.
    - OCP(Open/Closed Principle)
      - 개방-폐쇄 원칙
      - 확장에는 열려 있으나, 변경에는 닫혀 있어야 한다.
    - LSP(Liskov Substitution Principle)
      - 리스코프 치환 원칙
      - 인터페이스로 설계한 기능을 위반하지 않아야 한다. 
        - 예시) 자동차를 앞으로 가는 기능을 만들었으면 느리더라도 앞으로 가는 기능이어야 한다.
    - ISP(Interface segregation Principle)
      - 인터페이스 분리 원칙
      - 클라이언트를 위한 범용 인터페이스보다 여러 개의 인터페이스가 낫다.
    - DIP(Dependency Inversion Principle)
      - 의존관 역전 원칙
      - 구현 클래스에 의존하지 않고 인터페이스를 의존해야 한다.