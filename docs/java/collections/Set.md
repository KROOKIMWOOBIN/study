# Set

## 왜 쓰는지

`List`는 같은 값을 여러 번 저장할 수 있습니다. 하지만 다음처럼 **중복이 의미 없는 데이터**도 많습니다.

- 가입한 이메일 목록
- 이미 방문한 URL
- 사용자가 가진 권한 목록
- 게시글에 달린 태그
- 배치에서 이미 처리한 주문 ID

이때 매번 직접 중복 검사를 하면 코드가 길어지고 실수하기 쉽습니다. `Set`은 **중복 없는 집합**을 컬렉션으로 다루기 위한 인터페이스입니다.

<div class="concept-box" markdown="1">

**핵심**: `Set`은 중복을 허용하지 않는 컬렉션입니다. 같은 값을 다시 넣으면 새로 추가하지 않고, 구현체에 따라 순서 없음, 삽입 순서 유지, 정렬 순서 유지 중 하나를 선택할 수 있습니다.

</div>

## 어떻게 쓰는지

### 기본 사용

```java
Set<String> emails = new HashSet<>();

emails.add("a@example.com");
emails.add("b@example.com");
emails.add("a@example.com"); // 중복이므로 false 반환

System.out.println(emails.size()); // 2
System.out.println(emails.contains("a@example.com")); // true

emails.remove("b@example.com");
```

`add()`는 실제로 값이 추가되면 `true`, 이미 값이 있어서 추가되지 않으면 `false`를 반환합니다.

```java
boolean added = emails.add("a@example.com");

if (!added) {
    System.out.println("이미 존재하는 이메일입니다");
}
```

### 집합 연산

```java
Set<Integer> a = new HashSet<>(Set.of(1, 2, 3));
Set<Integer> b = new HashSet<>(Set.of(2, 3, 4));

Set<Integer> union = new HashSet<>(a);
union.addAll(b); // 합집합: [1, 2, 3, 4]

Set<Integer> intersection = new HashSet<>(a);
intersection.retainAll(b); // 교집합: [2, 3]

Set<Integer> difference = new HashSet<>(a);
difference.removeAll(b); // 차집합: [1]
```

원본을 유지하려면 새 `HashSet`으로 복사한 뒤 연산합니다. `addAll()`, `retainAll()`, `removeAll()`은 호출한 Set 자체를 변경합니다.

## 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **중복 제거만 필요** | `HashSet` | 가장 일반적이고 평균 `O(1)` |
| **중복 제거 + 삽입 순서 유지** | `LinkedHashSet` | 입력한 순서대로 순회 |
| **중복 제거 + 정렬 유지** | `TreeSet` | 항상 정렬된 상태 유지 |
| **빠른 존재 여부 확인** | `HashSet` | `contains()` 평균 `O(1)` |
| **범위 검색 필요** | `TreeSet` | `subSet()`, `headSet()`, `tailSet()` 사용 |
| **인덱스로 접근 필요** | `List` | `Set`은 인덱스 개념이 없음 |

## 장점

| 장점 | 설명 |
|------|------|
| **중복 자동 제거** | 같은 값을 다시 넣어도 하나만 유지 |
| **존재 여부 확인이 쉬움** | `contains()`로 포함 여부 확인 |
| **집합 연산 가능** | 합집합, 교집합, 차집합 구현이 간단 |
| **구현체 선택 가능** | 순서/정렬/성능 요구에 맞게 선택 |

## 단점

| 단점 | 설명 |
|------|------|
| **인덱스 접근 불가** | `get(0)` 같은 접근이 없음 |
| **중복 데이터 저장 불가** | 같은 값이 여러 번 등장한 횟수는 보존하지 않음 |
| **HashSet 순서 미보장** | 순회 순서가 입력 순서와 다를 수 있음 |
| **동등성 설계 필요** | 직접 만든 객체는 `equals()`/`hashCode()` 기준이 중요 |

## 특징

### 1. Set은 인터페이스

```java
Set<String> set = new HashSet<>();
```

변수 타입은 `Set`으로 두고, 실제 구현체는 요구사항에 맞게 선택하는 것이 일반적입니다.

```java
Set<String> tags = new HashSet<>();       // 순서 불필요
Set<String> menus = new LinkedHashSet<>(); // 삽입 순서 필요
Set<String> names = new TreeSet<>();       // 정렬 필요
```

### 2. 구현체 비교

| 구현체 | 순서 | 주요 성능 | 내부 구조 | 언제 쓰는가 |
|--------|------|-----------|-----------|-------------|
| `HashSet` | 보장 안 함 | 평균 `O(1)` | 해시 테이블 | 기본 선택, 빠른 중복 제거 |
| `LinkedHashSet` | 삽입 순서 | 평균 `O(1)` | 해시 테이블 + 연결 리스트 | 중복 제거 후 입력 순서 유지 |
| `TreeSet` | 정렬 순서 | `O(log n)` | Red-Black Tree | 정렬/범위 검색 필요 |

### 3. HashSet

`HashSet`은 가장 많이 쓰는 Set 구현체입니다. 내부적으로 `HashMap`을 사용하며, 요소를 `HashMap`의 Key처럼 저장합니다.

```java
Set<Long> processedIds = new HashSet<>();

if (processedIds.add(orderId)) {
    process(orderId);
}
```

이 코드는 `orderId`가 처음 등장한 경우에만 처리합니다. 이미 처리한 값이면 `add()`가 `false`를 반환합니다.

<div class="tip-box" markdown="1">

`HashSet`의 자세한 내부 원리는 [Hash](Hash.md) 문서에서 따로 정리합니다.

</div>

### 4. LinkedHashSet

`LinkedHashSet`은 중복을 제거하면서 **삽입 순서**를 유지합니다.

```java
Set<String> steps = new LinkedHashSet<>();
steps.add("login");
steps.add("select");
steps.add("pay");
steps.add("login");

System.out.println(steps); // [login, select, pay]
```

`HashSet`보다 연결 정보가 추가되므로 메모리는 조금 더 쓰지만, 순서가 필요한 결과를 만들 때 유용합니다.

### 5. TreeSet

`TreeSet`은 값을 정렬된 상태로 유지합니다.

```java
Set<Integer> scores = new TreeSet<>();
scores.add(30);
scores.add(10);
scores.add(20);

System.out.println(scores); // [10, 20, 30]
```

직접 만든 객체를 넣으려면 `Comparable`을 구현하거나 `Comparator`를 전달해야 합니다.

```java
Set<Member> members = new TreeSet<>(
    Comparator.comparing(Member::name)
);
```

### 6. 중복 판단 기준

`HashSet`은 `hashCode()`와 `equals()`로 중복을 판단합니다. `TreeSet`은 정렬 기준인 `compareTo()` 또는 `Comparator` 결과로 중복을 판단합니다.

```java
record Member(Long id, String name) {
}

Set<Member> members = new HashSet<>();
members.add(new Member(1L, "kim"));
members.add(new Member(1L, "kim"));

System.out.println(members.size()); // 1
```

record는 주요 필드를 기준으로 `equals()`와 `hashCode()`를 자동 생성하므로 값 객체를 Set에 넣을 때 편합니다.

## 주의할 점

<div class="warning-box" markdown="1">

**HashSet 순서에 의존하지 말 것**

출력했을 때 우연히 삽입 순서처럼 보여도 보장된 동작이 아닙니다. 순서가 필요하면 `LinkedHashSet`, 정렬이 필요하면 `TreeSet`을 사용합니다.

</div>

<div class="danger-box" markdown="1">

**가변 객체를 HashSet에 넣지 말 것**

`hashCode()` 계산에 쓰이는 필드가 저장 후 바뀌면 `contains()`나 `remove()`가 실패할 수 있습니다.

</div>

```java
class UserKey {
    String email;

    UserKey(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserKey key)) {
            return false;
        }
        return Objects.equals(email, key.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

Set<UserKey> keys = new HashSet<>();
UserKey key = new UserKey("a@example.com");

keys.add(key);
key.email = "b@example.com";

System.out.println(keys.contains(key)); // false가 나올 수 있음
```

<div class="warning-box" markdown="1">

**TreeSet의 Comparator는 equals 기준과 다를 수 있습니다.**

`Comparator`가 같다고 판단하면 `equals()`가 달라도 중복으로 처리될 수 있습니다.

</div>

```java
Set<String> names = new TreeSet<>(String::compareToIgnoreCase);
names.add("kim");
names.add("KIM");

System.out.println(names.size()); // 1
```

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| **변수 타입은 `Set`으로 선언** | 구현체 교체가 쉬움 |
| **순서가 필요 없으면 `HashSet` 기본 선택** | 가장 단순하고 평균 성능이 좋음 |
| **순서가 필요하면 `LinkedHashSet`** | 중복 제거와 삽입 순서를 동시에 만족 |
| **정렬/범위 검색이 필요하면 `TreeSet`** | 항상 정렬된 상태를 유지 |
| **Set 요소는 불변 객체 권장** | 중복 판단 기준이 흔들리지 않음 |
| **대량 데이터는 초기 용량 지정** | `HashSet` 리사이즈 비용을 줄일 수 있음 |
| **빈도 계산은 Set이 아니라 Map 사용** | Set은 등장 횟수를 보존하지 않음 |

## 실무에서는?

| 상황 | 사용 예 |
|------|---------|
| **중복 요청 차단** | 이미 처리한 요청 ID를 `HashSet`에 저장 |
| **권한 확인** | `Set<Role>`로 특정 권한 포함 여부 확인 |
| **태그 관리** | 게시글 태그 중복 제거 |
| **CSV/로그 중복 제거** | 파일에서 읽은 ID를 Set에 모아 유일값 추출 |
| **탐색 알고리즘** | 방문한 노드 기록 |
| **결과 순서 유지** | 중복 제거 후 원래 순서를 유지하려고 `LinkedHashSet` 사용 |
| **자동완성/랭킹 정렬** | 정렬 상태가 필요하면 `TreeSet` 사용 |

## 정리

| 항목 | 설명 |
|------|------|
| **Set** | 중복 없는 컬렉션 |
| **HashSet** | 순서 불필요, 평균 `O(1)`, 기본 선택 |
| **LinkedHashSet** | 삽입 순서 유지 |
| **TreeSet** | 정렬 순서 유지, `O(log n)` |
| **주의** | HashSet 순서 의존 금지, 가변 객체 저장 주의 |

---

**관련 파일:**
- [Hash](Hash.md) — HashSet/HashMap 내부 원리
- [Map](Map.md) — Key-Value 저장
- [Collection](Collection.md) — 컬렉션 프레임워크 개요
- [비교](비교.md) — Comparable, Comparator
