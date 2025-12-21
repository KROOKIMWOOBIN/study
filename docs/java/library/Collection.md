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

### 필요성
1. 일관성
2. 재사용성
3. 확장성
4. 다형성

### 구조
- Iterable
- Collection
  - List
    - ArrayList
    - LinkedList
  - Queue
    - Deque
      - LinkedList
      - ArrayDeque
  - Set
    - HashSet
      - LinkedHashSet
    - TreeSet
- Map
  - HashMap
    - LinkedHashMap
  - TreeMap

### 핵심 인터페이스
- Collection
  - 단일 루트 인터페이스로, 모든 컬렉션 클래스가 이 인터페이스를 상속받는다.
  - List, Set, Queue 등의 인터페이스가 여기에 포함된다.
- List
  - 순서가 있는 컬렉션을 나타내며, 중복 요소를 허용한다. 인덱스를 통해 인덱스에 접근할 수 있다.
  - 예: ArrayList, LinkedList
- Set
  - 중복 요소를 허용하지 않는 컬렉션을 나타낸다. 특정 위치가 없기 때문에 인덱스를 통해 요소에 접근할 수 없다.
  - 예: HashSet, LinkedHashSet, TreeSet
- Queue
  - 요소가 처리되기 전에 보관되는 컬렉션을 나타낸다.
  - 예: ArrayDeque, LinkedList, PriorityQueue
- Map
  - 키와 값 쌍으로 저장되는 객체이다. Map은 Collection 인터페이스를 상속 받지 않는다.
  - 예: HashMap, LinkedHashMap, TreeMap

### 구현

#### List
ArrayList는 내부적으로 배열을 사용하며, LinkedList는 연결 리스트를 사용한다.

#### Set
HashSet은 해시 테이블을, LinkedHashSet은 해시 테이블과 연결 리스트를, TreeSet은 레드-블랙 트리를 사용한다.

#### Map
HashMap은 해시 테이블을, LinkedHashMap은 해시 테이블과 연결 리스트를, TreeMap은 레드-블랙 트리를 사용한다.

#### Queue
LinkedList는 연결 리스트를 사용한다. ArrayDeque는 배열 기반의 원형 큐를 사용한다. 대부분의 경우 ArrayDeque가 빠르다.
