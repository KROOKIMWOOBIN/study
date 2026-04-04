> [← 홈](/) · [Java](/java/java/) · [중급 2편](/java/collections/collections/)

## 연결 리스트 (LinkedList)

> 노드(Node)들이 다음 노드의 참조를 통해 연결된 리스트 구조. 내부 배열 없이 포인터로 연결된다.

---

### 왜 사용하는가?

`ArrayList`는 앞/중간 삽입·삭제 시 요소를 이동시켜야 해서 O(n)이 발생한다.
`LinkedList`는 노드 간 참조를 변경하는 방식이라 **첫 번째 위치 삽입·삭제가 O(1)**이다.

### 어떻게 쓰는가?

```java
LinkedList<String> list = new LinkedList<>();

list.add("A");          // 마지막에 추가
list.addFirst("B");     // 맨 앞에 추가 O(1)
list.addLast("C");      // 맨 뒤에 추가

list.removeFirst();     // 맨 앞 삭제 O(1)
list.removeLast();      // 맨 뒤 삭제

String s = list.get(1); // 인덱스 조회 O(n)
```

### 어디 상황에서 자주 쓰는가?

- 앞·중간에 **삽입·삭제가 빈번**한 경우
- `Deque` / `Queue`가 필요한 경우 (양쪽 끝 O(1) 접근)
- 데이터 순서가 자주 바뀌는 경우

> 실무에서는 `ArrayList`가 대부분 더 빠르다 (CPU 캐시 효율). 앞쪽 삽입·삭제가 많은 특수한 경우에만 `LinkedList`를 선택한다.

### 시간 복잡도

| 연산 | 메서드 | 위치 | 시간복잡도 | 설명 |
|------|--------|------|-----------|------|
| 조회 | `get(index)` | - | O(n) | 첫 노드부터 순차 탐색 |
| 추가 | `add(e)` | 마지막 | O(n) | 마지막 노드까지 탐색 필요 |
| 추가 | `addFirst(e)` | 첫 번째 | O(1) | 앞에만 연결 변경 |
| 추가 | `add(index, e)` | 중간 | O(n) | 위치까지 탐색 필요 |
| 삭제 | `removeFirst()` | 첫 번째 | O(1) | 앞 연결만 변경 |
| 삭제 | `remove(index)` | 중간 | O(n) | 위치까지 탐색 필요 |
| 변경 | `set(index, e)` | - | O(n) | 위치까지 탐색 필요 |
| 검색 | `indexOf(o)` | - | O(n) | 전체 순회 |

### 장점

- 앞/첫 번째 위치 삽입·삭제 **O(1)**
- 크기 변경 시 배열 복사(resize) 없음
- `Deque` 인터페이스 구현 → Queue / Stack 모두 사용 가능

### 단점

- 인덱스 조회 **O(n)** — 임의 접근이 느림
- 각 노드마다 참조(포인터) 저장으로 **메모리 오버헤드** 발생
- 메모리가 분산 배치 → CPU 캐시 미스 발생, 실제 성능이 `ArrayList`보다 느린 경우가 많음
