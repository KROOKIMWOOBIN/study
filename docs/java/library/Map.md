## Map
- Key–Value 쌍으로 데이터를 저장하는 컬렉션 구조
- 하나의 `Key`는 반드시 유일(unique) 해야 한다.
- 동일한 `Key`로 값을 저장하면 기존 값이 덮어쓴다.

### HashMap
- `Key`의 hashCode() 값을 이용해 버킷 위치를 계산하여 데이터를 저장한다.

### 특징
- 입력 순서를 보장하지 않는다.
- Key와 Value에 null 허용 (Key는 1개만 가능)
- 동기화를 제공하지 않는다. (Thread-safe 아님)
- 충돌 발생 시 LinkedList → Red-Black Tree(Java8+) 구조로 관리

### Linked HashMap
- LinkedHashMap 은 HashMap + LinkedList 구조를 결합한 `Map`이다.
- 이중 연결 리스트이다.

### 특징
- 입력 순서를 유지
- `HashMap`과 거의 동일한 성능
- 내부적으로 `LinkedList`로 순서를 관리

### 순서 정리 방식
| 모드              | 설명          | 인자    |
| --------------- | ----------- |-------|
| insertion-order | 입력 순서 유지    | false |
| access-order    | 최근 접근 순서 유지 | true |
```markdown
new LinkedHashMap<>(16, 0.75f, true); // [사이즈], [리사이즈 크기], [순서]
```

### TreeMap
- TreeMap 은 Red-Black Tree 기반 Map 구현체다.
- 데이터를 Key 기준으로 정렬하여 저장한다.

### 특징
- 자동 정렬
- `Key`는 Comparable 구현 또는 Comparator 필요
- null key 허용하지 않음