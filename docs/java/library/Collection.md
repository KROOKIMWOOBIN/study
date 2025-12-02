## 컴파일 타임 의존관계 VS 런타임 의존관계
- 컴파일 타임 의존관계
  - 코드 컴파일 시점
  - 컴파일 시점에 어떤 클래스/인터페이스를 의존하는지
  ```java
    public class Batch { 
        // 컴파일 시점에 의존성 주입이 된다.
        private final List<?> list;
        Batch(List<?> list) {
            this.list = list;
        }
    }
  ```
- 런타임
  - 프로그램 실행 시점
  - 실제로 어떤 구현체 인스턴스를 의존하는지
  ```java
  public class Main { 
    public static void main(String[] args){
        List<Integer> intList = new ArrayList<>();
        // 런타임 시점에 의존성 주입이 된다.
        Batch batch = new Batch(intList);
    }
  }
  ```
## 컬렉션
### 사용 예시
- List
  - 장바구니 목록, 순서가 중요한 일련의 이벤트 목록
- Set
  - 회원 ID 집합, 고유한 항목의 집합