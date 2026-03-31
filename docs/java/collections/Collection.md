> [← 홈](/README.md) · [Java](/docs/java/java.md) · [중급 2편](/docs/java/collections/collections.md)

## 컴파일 타임 의존관계 VS 런타임 의존관계
- 컴파일 타임 의존관계
  - 코드 컴파일 시점
  - 컴파일 시점에 어떤 클래스/인터페이스를 의존하는지
  ```markdown
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
  ```markdown
  public class Main {
    public static void main(String[] args){
        List<Integer> intList = new ArrayList<>();
        // 런타임 시점에 의존성 주입이 된다.
        Batch batch = new Batch(intList);
    }
  }
  ```

## 컬렉션 (Collection)

### 왜 사용하는가?
- 배열의 한계(고정 크기, 수동 관리)를 극복하기 위해
- 다양한 자료구조(리스트, 셋, 큐, 맵)를 표준화된 인터페이스로 제공
- 다형성을 활용하여 구현체 교체가 쉬움

### 장점
| 항목 | 설명 |
| --- | --- |
| 동적 크기 | 데이터 추가/삭제 시 자동 크기 조정 |
| 일관성 | 표준 인터페이스로 동일한 방식의 조작 |
| 재사용성 | 다양한 자료구조를 라이브러리 형태로 제공 |
| 다형성 | 인터페이스 타입으로 구현체 교체 가능 |

### 특이점
- `Map`은 `Collection` 인터페이스를 상속받지 않는다 (키-값 구조가 다름)
- 모든 컬렉션은 `Iterable`을 구현하여 for-each 사용 가능 (Map 제외)

### 구조
```markdown
Iterable
 └─ Collection
     ├─ List
     │   ├─ ArrayList
     │   └─ LinkedList
     ├─ Queue
     │   └─ Deque
     │       ├─ LinkedList
     │       └─ ArrayDeque
     └─ Set
         ├─ HashSet
         │   └─ LinkedHashSet
         └─ TreeSet

Map (별도)
 ├─ HashMap
 │   └─ LinkedHashMap
 └─ TreeMap
```

### 핵심 인터페이스
| 인터페이스 | 특징 | 구현체 |
| --- | --- | --- |
| `List` | 순서 유지, 중복 허용, 인덱스 접근 | `ArrayList`, `LinkedList` |
| `Set` | 중복 불허, 순서 미보장 | `HashSet`, `LinkedHashSet`, `TreeSet` |
| `Queue` | FIFO, 처리 대기 | `ArrayDeque`, `LinkedList`, `PriorityQueue` |
| `Map` | 키-값 쌍, 키 중복 불허 | `HashMap`, `LinkedHashMap`, `TreeMap` |

### 구현체 선택 기준
| 상황 | 선택 |
| --- | --- |
| 순서 + 빠른 인덱스 접근 | `ArrayList` |
| 순서 + 빠른 삽입/삭제 (앞) | `LinkedList` |
| 중복 제거, 순서 불필요 | `HashSet` |
| 중복 제거 + 순서 유지 | `LinkedHashSet` |
| 중복 제거 + 정렬 | `TreeSet` |
| 키-값 빠른 검색 | `HashMap` |
| 키-값 + 삽입 순서 | `LinkedHashMap` |
| 키-값 + 정렬 | `TreeMap` |
| FIFO 큐 | `ArrayDeque` |

### 어떻게 사용하는가?
```markdown
// List
List<String> list = new ArrayList<>();
list.add("A");
list.get(0);

// Set
Set<String> set = new HashSet<>();
set.add("A");
set.contains("A");

// Queue (Deque)
Deque<String> queue = new ArrayDeque<>();
queue.offer("A");
queue.poll();

// Map
Map<String, Integer> map = new HashMap<>();
map.put("key", 1);
map.get("key");
```

### 어떨 때 많이 쓰는가?
- 배열 대신 동적으로 크기가 변하는 목록이 필요할 때 → `ArrayList`
- 중복 없는 집합이 필요할 때 → `HashSet`
- 키로 빠르게 데이터를 조회해야 할 때 → `HashMap`
- 작업 대기열, 이벤트 처리 순서 관리 → `ArrayDeque`
