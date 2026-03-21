> [← 홈](/README.md) · [Java](/docs/java/java.md) · [중급 2편](/docs/java/mid2.md)

## Set

### 왜 사용하는가?
- 중복 없는 유일한 데이터 집합이 필요할 때
- 특정 값의 존재 여부를 빠르게 확인해야 할 때
- 중복 제거 로직을 직접 구현하지 않으려 할 때

### 특징
- 유일성: 중복 요소를 저장하지 않음 (이미 있으면 무시)
- 순서 미보장: 대부분의 구현체에서 삽입 순서 보장 안 됨
- 빠른 검색: 해시 기반으로 O(1) 조회

### 장점
| 항목 | 설명 |
| --- | --- |
| 중복 자동 처리 | 중복 삽입 시 자동 무시 |
| 빠른 존재 확인 | `contains()` O(1) (HashSet 기준) |
| 집합 연산 | 교집합, 합집합, 차집합 지원 |

### 단점
| 항목 | 설명 |
| --- | --- |
| 순서 없음 | 인덱스 접근 불가 |
| 정렬 비용 | 정렬 필요 시 TreeSet 사용 (O(log n)) |

### 어떻게 사용하는가?
```java
Set<String> set = new HashSet<>();

// 추가
set.add("A");
set.add("A"); // 무시됨

// 존재 확인
boolean exists = set.contains("A"); // O(1)

// 삭제
set.remove("A");

// 순회
for (String s : set) {
    System.out.println(s);
}

// 집합 연산
Set<Integer> a = new HashSet<>(Set.of(1, 2, 3));
Set<Integer> b = new HashSet<>(Set.of(2, 3, 4));

a.retainAll(b); // 교집합 {2, 3}
a.addAll(b);    // 합집합
a.removeAll(b); // 차집합
```

### 구현체 비교
| 구현체 | 순서 | 성능 | 특징 |
| --- | --- | --- | --- |
| `HashSet` | 없음 | O(1) | 기본 선택, 가장 빠름 |
| `LinkedHashSet` | 삽입 순서 | O(1) | HashSet보다 약간 무거움 |
| `TreeSet` | 정렬 순서 | O(log n) | 레드-블랙 트리 기반 |

---

## HashSet
- 해시 테이블 기반
- 최적화: 데이터 양이 배열 크기의 **75% 초과** 시 배열 크기 2배 확장 후 **재해싱(rehashing)** 수행

## LinkedHashSet
- HashSet + 연결 리스트 조합 (양방향 Node 기반)
- HashSet보다 약간 무거움
- 데이터 삽입 순서가 보장됨
- 중복 없이 순서를 유지해야 할 때 사용

## TreeSet
- 이진탐색트리를 개선한 **레드-블랙 트리** 사용
- 요소들이 정렬된 순서로 저장됨
- 정렬 기준은 `Comparable` 또는 `Comparator`로 변경 가능
- 주요 연산: O(log n)
- 데이터를 정렬 상태로 유지하면서 집합 특성을 유지해야 할 때 사용

### 어떨 때 많이 쓰는가?
| 상황 | 선택 |
| --- | --- |
| 단순 중복 제거, 순서 불필요 | `HashSet` |
| 중복 제거 + 삽입 순서 유지 | `LinkedHashSet` |
| 중복 제거 + 정렬된 결과 필요 | `TreeSet` |
| 방문한 URL, ID 중복 체크 | `HashSet` |
| 태그, 카테고리 관리 | `HashSet` |
