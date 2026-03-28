> [← 홈](/README.md) · [Java](/docs/java/java.md) · [중급 2편](/docs/java/collections/collections.md)

## 큐 (Queue)

> **선입선출(FIFO, First In First Out)** 구조의 자료구조

---

### 왜 사용하는가?

먼저 들어온 데이터를 먼저 처리해야 하는 **순서 보장**이 필요한 상황에서 사용한다.
메시지 처리, 작업 대기열, BFS 탐색 등에서 핵심 자료구조다.

### 어떻게 쓰는가?

```java
// 권장 방식 (ArrayDeque를 큐로 사용)
Queue<String> queue = new ArrayDeque<>();

queue.offer("A");   // 추가 (뒤에 넣기)
queue.offer("B");
queue.offer("C");

queue.poll();       // 꺼내기 → "A" (처음 들어온 것)
queue.peek();       // 확인만 → "B" (제거하지 않음)
queue.isEmpty();    // 비어있는지 확인
```

### Deque (Double-Ended Queue)

양쪽 끝에서 추가·제거가 모두 가능한 자료구조. Queue + Stack 역할을 동시에 할 수 있다.

```java
Deque<Integer> deque = new ArrayDeque<>();

deque.offerFirst(1);  // 앞에 추가
deque.offerLast(2);   // 뒤에 추가

deque.pollFirst();    // 앞에서 꺼내기
deque.pollLast();     // 뒤에서 꺼내기
```

### 컬렉션 계층

```text
Collection
 └─ Queue
     └─ Deque
         ├─ ArrayDeque   ← 권장
         └─ LinkedList
```

### 어디 상황에서 자주 쓰는가?

| 상황 | 예시 |
|------|------|
| 작업 대기열 | 프린터 인쇄 순서, 메시지 큐 |
| BFS (너비 우선 탐색) | 그래프/트리 탐색 |
| 이벤트 처리 | 이벤트 루프 |
| 스레드풀 작업 관리 | `LinkedBlockingQueue` |

### 장점

- `offer` / `poll` 모두 **O(1)**
- 순서 보장이 자연스러운 구조
- `Deque`로 확장 시 Stack 역할도 가능

### 단점

- 중간 요소에 직접 접근 불가
- `LinkedList` 기반 Queue는 노드 생성 오버헤드 존재 → `ArrayDeque` 권장
