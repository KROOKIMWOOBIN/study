## List

<div class="concept-box" markdown="1">

==List==: **순서가 있고 중복을 허용**하는 컬렉션. 인덱스로 요소에 접근한다.

</div>

### 왜 사용하는가?
- 순서가 있고 중복을 허용하는 데이터를 다룰 때
- 인덱스를 통한 임의 접근이 필요할 때
- 배열의 고정 크기 한계를 극복하고 동적으로 크기를 조절해야 할 때

### 특징
- 순서 유지: 요소가 추가된 순서를 보장
- 중복 허용: 동일한 값을 여러 번 저장 가능
- 인덱스 접근: 0부터 시작하는 인덱스로 요소 접근

### 장점
| 항목 | 설명 |
| --- | --- |
| 동적 크기 | 자동으로 크기 조절 |
| 순서 보장 | 삽입 순서 유지 |
| 중복 허용 | 같은 값을 여러 번 저장 가능 |

### 단점
| 항목 | 설명 |
| --- | --- |
| 중복 제거 불가 | 직접 처리해야 함 |
| 삽입/삭제 비용 | 중간 위치 조작 시 O(n) |

### 어떻게 사용하는가?
```java
List<String> list = new ArrayList<>();

// 추가
list.add("A");
list.add(0, "B"); // 인덱스 위치에 삽입

// 조회
String value = list.get(0);
int size = list.size();

// 검색
boolean contains = list.contains("A");
int index = list.indexOf("A");

// 삭제
list.remove(0);         // 인덱스로 삭제
list.remove("A");       // 값으로 삭제

// 순회
for (String s : list) {
    System.out.println(s);
}

// 정렬
Collections.sort(list);
list.sort(Comparator.naturalOrder());
```

### 성능 비교
| 연산 | ArrayList | LinkedList |
| --- | --- | --- |
| 인덱스 조회 | O(1) | O(n) |
| 검색 | O(n) | O(n) |
| 앞에 추가/삭제 | O(n) | O(1) |
| 뒤에 추가/삭제 | O(1) | O(n) |
| 중간 추가/삭제 | O(n) | O(n) |

### 어떨 때 많이 쓰는가?
| 상황 | 선택 |
| --- | --- |
| 조회 위주, 크기 변경 적음 | `ArrayList` |
| 앞/중간 삽입·삭제가 잦음 | `LinkedList` |
| 순서 있는 데이터 목록 관리 | `ArrayList` |
| 큐/덱으로도 사용 | `LinkedList` or `ArrayDeque` |

> 실무에서는 대부분 `ArrayList`를 기본으로 사용한다.
