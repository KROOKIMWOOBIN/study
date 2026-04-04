> [← 홈](/study/) · [Java](/java/java/) · [중급 2편](/java/collections/collections/)

## 해시 알고리즘 (Hash)

### 왜 사용하는가?
- 데이터를 O(1)에 저장/검색하기 위해
- 배열의 인덱스 방식 + 충돌 처리를 결합하여 빠른 조회 구현
- `HashSet`, `HashMap`의 내부 동작 원리

### 장점
| 항목 | 설명 |
| --- | --- |
| 빠른 검색 | 평균 O(1) 조회/삽입/삭제 |
| 유연한 키 | 문자열, 객체 등 다양한 타입 키 사용 가능 |
| 중복 제거 | 해시 기반으로 중복 자동 처리 |

### 단점
| 항목 | 설명 |
| --- | --- |
| 순서 없음 | 삽입 순서 보장 안 됨 |
| 해시 충돌 | 다른 값이 같은 해시 인덱스를 가질 수 있음 |
| 최악의 경우 | 충돌이 많으면 O(n)으로 성능 저하 |

### 특이점
- 해시 충돌이 75% 이상 발생하면 자바의 `HashMap`/`HashSet`은 **재해싱(Rehashing)** 수행
  - 배열 크기를 2배로 늘리고 모든 요소의 해시 인덱스 재계산

---

### 인덱스 직접 사용 (O(1))
- 자기 자신의 값을 인덱스로 사용하면 검색 속도 O(1)
```java
Integer[] intList = new Integer[100];
intList[1] = 1;
intList[8] = 8;

int search = 8;
Integer result = intList[search]; // O(1)
```

### 해시 인덱스 (Hash Index)
- 원래 값을 계산해서 나온 인덱스를 해시 인덱스라 한다
```java
private static int hashIndex(Object value) {
    return Math.abs(value.hashCode()) % CAPACITY;
}
```
```text
값 14 → hashIndex(14) = 14 % 10 = 4
값 99 → hashIndex(99) = 99 % 10 = 9
```

### 해시 충돌 (Hash Collision)
- 다른 값을 입력했지만 같은 해시 인덱스가 나오는 현상
- **체이닝(Chaining)** 방식으로 해결 → 충돌 위치에 LinkedList 사용
```java
LinkedList<Integer>[] buckets = new LinkedList[CAPACITY];
// [[], [1], [2], [], [14], [5], [], [], [8], [99, 9]]
// 인덱스 9에 99와 9가 충돌 → LinkedList로 관리
```

### 해시 코드 (Hash Code)
```java
static int hashCode(String str) {
    char[] charArrays = str.toCharArray();
    int sum = 0;
    for (char c : charArrays) {
        sum += c;
    }
    return sum;
}
```

### 해시 함수 (Hash Function)
- 임의의 길이 데이터를 입력받아 고정된 길이의 해시값을 출력하는 함수
- 같은 데이터를 입력하면 항상 같은 해시 코드가 출력된다

---

### hashCode와 equals를 재정의하는 이유
1. `hashCode()`를 재정의하지 않으면 `Object.hashCode()`로 주소 기반 해시값 사용
   - 같은 필드 값이어도 다른 객체면 다른 해시 인덱스 → `HashSet`에서 중복으로 인식하지 못함
2. `equals()`를 재정의하지 않으면 동일성(`==`)만 비교
   - 같은 필드 값의 다른 인스턴스를 다른 객체로 판단

```java
class Member {
    String id;
    String name;

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member m)) return false;
        return Objects.equals(id, m.id) && Objects.equals(name, m.name);
    }
}
```

### 어떨 때 많이 쓰는가?
- `HashSet`에 객체를 넣을 때 중복 판별이 필요한 경우
- `HashMap`의 키로 객체를 사용할 때
- 빠른 데이터 검색이 필요한 캐시, 인덱싱 구현
