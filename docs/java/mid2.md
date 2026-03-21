# Java 중급 2편

제네릭, 배열, 컬렉션 프레임워크 — 데이터를 효율적으로 저장하고 다루는 도구들.

---

## 제네릭 & 배열

| 주제 | 한 줄 설명 |
|------|-----------|
| [제네릭](./library/Generic.md) | 타입을 매개변수화하여 재사용성·타입 안전성 확보 |
| [배열](./library/Array.md) | 고정 크기 연속 메모리 자료구조 |
| [빅오 표기법](./library/O.md) | 알고리즘 시간/공간 복잡도 표기 |

---

## 컬렉션 프레임워크

```text
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

Map (별도 계층)
 ├─ HashMap            ← 기본 Map, 순서 미보장
 ├─ LinkedHashMap      ← 삽입 순서 유지
 └─ TreeMap            ← Key 기준 정렬
```

| 주제 | 한 줄 설명 |
|------|-----------|
| [컬렉션](./library/Collection.md) | 컬렉션 프레임워크 개요 |
| [리스트](./library/List.md) | 순서 유지, 중복 허용 |
| [배열리스트](./library/ArrayList.md) | 동적 배열 기반 List |
| [연결리스트](./library/LinkedList.md) | 노드 참조 기반 List |
| [세트(Set)](./library/Set.md) | 중복 불허, 순서 미보장 |
| [해시](./library/Hash.md) | 해시 알고리즘과 충돌 처리 |
| [맵](./library/Map.md) | Key-Value 쌍 저장 |
| [스택](./library/Stack.md) | LIFO 자료구조 |
| [큐](./library/Queue.md) | FIFO 자료구조 |
| [순회](./library/순회.md) | Iterator, for-each |
| [비교](./library/비교.md) | Comparable, Comparator |
