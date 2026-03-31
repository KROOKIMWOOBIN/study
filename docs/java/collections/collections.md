> [← 홈](/README.md) · [Java](/docs/java/java.md)

# Java 중급 2편

제네릭, 배열, 컬렉션 프레임워크 — 데이터를 효율적으로 저장하고 다루는 도구들.

---

## 제네릭 & 배열

| 주제 | 한 줄 설명 |
|------|-----------|
| [제네릭](./Generic.md) | 타입을 매개변수화하여 재사용성·타입 안전성 확보 |
| [배열](./Array.md) | 고정 크기 연속 메모리 자료구조 |
| [빅오 표기법](./BigO.md) | 알고리즘 시간/공간 복잡도 표기 |

---

## 컬렉션 프레임워크

### 왜 쓰는가?

배열은 크기가 고정되고 삽입·삭제 시 직접 인덱스를 관리해야 한다. 컬렉션 프레임워크는 **동적 크기 조절, 중복 제거, 순서 보장, 키-값 매핑** 등 다양한 요구에 맞는 자료구조를 표준 인터페이스로 제공한다.

### 특징

- `Collection` 인터페이스 아래 List, Set, Queue가 통합된 계층 구조
- `Map`은 `Collection`을 상속하지 않는 별도 계층 (키-값 구조라 성격이 다름)
- 모든 구현체는 `Iterable`을 구현 → for-each, Iterator 통일 사용 가능
- 인터페이스 기반 설계 → 구현체를 바꿔도 코드 변경 최소화

```markdown
// 인터페이스로 선언 — 구현체 교체 시 선언부만 수정
List<String> list = new ArrayList<>();   // → LinkedList로 교체해도 나머지 코드 무변경
Set<String>  set  = new HashSet<>();
Map<String, Integer> map = new HashMap<>();
```

### 언제 어떤 구현체를 쓰는가?

| 상황 | 선택 |
|---|---|
| 순서 있고, 인덱스 조회 많음 | `ArrayList` |
| 앞/중간 삽입·삭제가 잦음 | `LinkedList` |
| 중복 없는 집합, 빠른 조회 | `HashSet` |
| 중복 없고 삽입 순서 유지 | `LinkedHashSet` |
| 중복 없고 정렬 유지 | `TreeSet` |
| 키-값 매핑, 순서 불필요 | `HashMap` |
| 키-값 매핑, 삽입 순서 유지 | `LinkedHashMap` |
| 키-값 매핑, 키 정렬 유지 | `TreeMap` |
| FIFO 큐 / 덱 | `ArrayDeque` |

### 장점

- 표준 인터페이스로 구현체를 자유롭게 교체 가능
- `Collections` 유틸 클래스로 정렬·검색·동기화 등 편의 메서드 제공
- Stream API와 자연스럽게 연동

### 단점

- `int`, `double` 등 기본형 직접 저장 불가 → `Integer`, `Double`로 박싱 필요 (성능 비용)
- 기본 구현체들은 스레드 안전하지 않음 → 멀티스레드 환경에서 `ConcurrentHashMap` 등 별도 사용

### 주의할 점

- `Stack` 클래스는 레거시 — 스택이 필요하면 `ArrayDeque`를 사용한다
- `LinkedList`는 List이면서 Deque도 구현하지만, 실무에서 Queue/Deque 용도라면 `ArrayDeque`가 성능상 유리하다
- 크기가 확실히 고정이면 배열이 컬렉션보다 빠르다 (박싱 없음, 오버헤드 없음)
- `HashMap`은 `null` 키/값을 허용하지만 `TreeMap`은 `null` 키를 허용하지 않는다 (정렬 비교 불가)

---

### 계층 구조

```markdown
Collection
 ├─ List
 │   ├─ ArrayList      ← 인덱스 조회 O(1), 실무 기본값
 │   └─ LinkedList     ← 앞 삽입·삭제 O(1)
 ├─ Set
 │   ├─ HashSet        ← 기본 Set, 순서 미보장
 │   ├─ LinkedHashSet  ← 삽입 순서 유지
 │   └─ TreeSet        ← 정렬 유지 (Red-Black Tree)
 └─ Queue
     └─ Deque
         ├─ ArrayDeque ← 권장 (Stack + Queue 모두 가능)
         └─ LinkedList

Map (별도 계층 — Collection 미상속)
 ├─ HashMap            ← 기본 Map, 순서 미보장
 ├─ LinkedHashMap      ← 삽입 순서 유지
 └─ TreeMap            ← Key 기준 정렬
```

---

### 목차

| 주제 | 한 줄 설명 |
|------|-----------|
| [컬렉션](./Collection.md) | 컬렉션 프레임워크 개요 |
| [리스트](./List.md) | 순서 유지, 중복 허용 |
| [배열리스트](./ArrayList.md) | 동적 배열 기반 List |
| [연결리스트](./LinkedList.md) | 노드 참조 기반 List |
| [세트(Set)](./Set.md) | 중복 불허, 순서 미보장 |
| [해시(HashSet)](./HashSet.md) | 해시 알고리즘과 충돌 처리 |
| [맵](./Map.md) | Key-Value 쌍 저장 |
| [스택](./Stack.md) | LIFO 자료구조 |
| [큐](./Queue.md) | FIFO 자료구조 |
| [순회](./순회.md) | Iterator, for-each |
| [비교](./비교.md) | Comparable, Comparator |
