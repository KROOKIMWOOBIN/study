> [← 홈](/index.md) · [Java](/java/java.md) · [중급 2편](/java/collections/collections.md)

## 스택 (Stack)

> **후입선출(LIFO, Last In First Out)** 구조의 자료구조

---

### 왜 사용하는가?

가장 최근에 추가한 데이터를 먼저 꺼내야 하는 상황에서 사용한다.
재귀 호출 스택, 되돌리기(Undo), 괄호 검사 등 **역순 처리 패턴**에 자연스럽게 맞는다.

### 어떻게 쓰는가?

> Java의 `Stack` 클래스는 레거시 — `ArrayDeque`를 사용하는 것이 권장된다.

```java
// 권장 방식 (ArrayDeque를 스택으로 사용)
Deque<Integer> stack = new ArrayDeque<>();

stack.push(1);    // 추가 (맨 위에 쌓기)
stack.push(2);
stack.push(3);

stack.pop();      // 꺼내기 → 3 (마지막에 추가된 것)
stack.peek();     // 확인만 → 2 (제거하지 않음)
stack.isEmpty();  // 비어있는지 확인
```

### 어디 상황에서 자주 쓰는가?

| 상황 | 예시 |
|------|------|
| 함수 호출 추적 | JVM 콜 스택 |
| 되돌리기 (Undo) | 텍스트 에디터, Ctrl+Z |
| 괄호 짝 검사 | 코딩 테스트 단골 문제 |
| DFS (깊이 우선 탐색) | 그래프/트리 탐색 |
| 역순 처리 | 문자열 뒤집기 |

### 장점

- 구조가 단순하여 이해·구현이 쉬움
- `push` / `pop` 모두 **O(1)**

### 단점

- 중간 요소에 접근 불가 (Stack 특성상)
- Java의 `java.util.Stack`은 `Vector` 기반으로 동기화(synchronized) — 성능 오버헤드 발생

> **⚠️ 주의:** `java.util.Stack`은 레거시 클래스다. 실무에서는 `ArrayDeque<>()`를 사용할 것
