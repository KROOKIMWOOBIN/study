# 해시 (Hash)

## 왜 쓰는지

배열은 인덱스를 알면 값을 바로 찾을 수 있습니다.

```java
String[] names = new String[10];
names[3] = "kim";

String name = names[3]; // 인덱스를 알면 O(1)
```

하지만 실무에서는 보통 `3` 같은 숫자 인덱스보다 `"user-100"`, `"kim@example.com"`, `Member(id=1)` 같은 값을 기준으로 데이터를 찾고 싶습니다. 해시는 이런 값을 **배열 인덱스로 바꿔서 빠르게 찾기 위한 방법**입니다.

<div class="concept-box" markdown="1">

**핵심**: 해시는 임의의 값을 해시 코드로 바꾸고, 그 해시 코드를 배열의 버킷 인덱스로 변환해 평균 `O(1)` 조회를 가능하게 하는 방식입니다. Java의 `HashSet`, `HashMap`이 이 원리를 사용합니다.

</div>

## 어떻게 쓰는지

### HashSet

```java
Set<String> emails = new HashSet<>();

emails.add("a@example.com");
emails.add("b@example.com");
emails.add("a@example.com"); // 중복이므로 저장되지 않음

boolean exists = emails.contains("a@example.com"); // 평균 O(1)
```

`HashSet`은 값 자체를 해시 기준으로 저장합니다. 그래서 **중복 제거**와 **존재 여부 확인**에 자주 사용합니다.

### HashMap

```java
Map<String, Integer> loginCount = new HashMap<>();

loginCount.put("kim", 1);
loginCount.put("lee", 3);

int count = loginCount.get("kim"); // 평균 O(1)
```

`HashMap`은 Key를 해시 기준으로 저장하고, Key에 연결된 Value를 찾습니다.

### 직접 만든 객체를 해시 컬렉션에 넣기

```java
public class Member {
    private final Long id;
    private final String email;

    public Member(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member member)) {
            return false;
        }
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

`HashSet`과 `HashMap`은 같은 값인지 판단할 때 `hashCode()`와 `equals()`를 함께 사용합니다. 직접 만든 객체를 Key나 Set 요소로 쓸 때는 둘을 같이 재정의해야 합니다.

## 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **중복 제거** | `HashSet` | 같은 값은 한 번만 저장 |
| **존재 여부 확인** | `HashSet` | `contains()` 평균 `O(1)` |
| **Key로 빠른 조회** | `HashMap` | `get(key)` 평균 `O(1)` |
| **빈도 계산** | `HashMap<T, Integer>` | 값별 카운트 저장 |
| **방문 여부 기록** | `HashSet` | 이미 처리한 값인지 빠르게 확인 |
| **정렬/범위 검색 필요** | `TreeSet`, `TreeMap` | 해시는 정렬과 범위 검색에 부적합 |
| **삽입 순서 필요** | `LinkedHashSet`, `LinkedHashMap` | 해시 성능에 순서 정보를 추가 |

## 장점

| 장점 | 설명 |
|------|------|
| **빠른 조회** | 평균적으로 삽입, 삭제, 조회가 `O(1)` |
| **유연한 Key** | 문자열, 숫자, 직접 만든 객체 모두 사용 가능 |
| **중복 처리에 강함** | `Set`과 결합하면 중복 제거를 쉽게 구현 |
| **구현체가 풍부함** | `HashSet`, `HashMap`, `LinkedHashMap`, `ConcurrentHashMap` 등 |

## 단점

| 단점 | 설명 |
|------|------|
| **순서 보장 없음** | `HashSet`, `HashMap` 순회 순서는 입력 순서와 다를 수 있음 |
| **충돌 가능성** | 서로 다른 값이 같은 버킷에 들어갈 수 있음 |
| **메모리 오버헤드** | 버킷 배열과 노드 객체를 유지해야 함 |
| **최악 성능 저하** | 해시가 나쁘거나 충돌이 많으면 조회 성능이 나빠짐 |
| **가변 Key 위험** | Key의 필드가 바뀌면 다시 찾지 못할 수 있음 |

## 특징

### 1. 해시 테이블 구조

해시 테이블은 내부적으로 배열을 사용합니다. 값을 바로 배열에 넣는 것이 아니라, 해시 함수를 통해 어느 버킷에 넣을지 정합니다.

```text
값: "kim"
  ↓ hashCode()
해시 코드: 106079
  ↓ 버킷 인덱스 계산
인덱스: 7
  ↓
table[7]에 저장
```

```text
table
┌───────┬────────────────────┐
│ index │ bucket             │
├───────┼────────────────────┤
│ 0     │                    │
│ 1     │ "lee"              │
│ 2     │                    │
│ 3     │ "park"             │
│ 4     │                    │
│ 5     │                    │
│ 6     │                    │
│ 7     │ "kim"              │
└───────┴────────────────────┘
```

### 2. 해시 코드, 해시 함수, 해시 인덱스

비슷해 보이지만 서로 다른 개념입니다.

| 용어 | 설명 |
|------|------|
| **해시 함수** | 입력값을 해시 코드로 바꾸는 함수 |
| **해시 코드** | `hashCode()`가 반환하는 정수 값 |
| **해시 인덱스** | 해시 코드를 배열 크기에 맞게 변환한 버킷 위치 |

개념적으로는 다음과 같이 생각할 수 있습니다.

```java
int hashCode = key.hashCode();
int index = Math.floorMod(hashCode, table.length);
```

Java의 `HashMap`은 실제로는 배열 길이를 2의 거듭제곱으로 맞추고 비트 연산을 사용합니다.

```java
int index = (table.length - 1) & hash;
```

그래서 단순히 `%`만 쓰는 예시는 원리 이해용으로 보면 됩니다.

### 3. 해시 충돌

해시 충돌은 **서로 다른 값이 같은 버킷 인덱스를 갖는 상황**입니다.

```text
"kim"  -> index 7
"moon" -> index 7
```

충돌은 버그가 아니라 해시 테이블에서 자연스럽게 발생하는 현상입니다. 입력값의 개수는 사실상 무한하지만, 버킷 배열 크기는 제한되어 있기 때문입니다.

Java의 `HashMap`은 충돌이 생기면 같은 버킷 안에 노드를 연결합니다.

```text
table[7]
  ↓
["kim"] -> ["moon"] -> ["choi"]
```

충돌이 적으면 거의 `O(1)`처럼 동작하지만, 한 버킷에 값이 몰리면 연결된 노드를 비교해야 하므로 느려집니다.

### 4. Java HashMap의 충돌 최적화

Java 8 이후 대표적인 `HashMap` 구현은 한 버킷에 노드가 너무 많이 몰리면 연결 리스트를 트리 구조로 바꿀 수 있습니다.

| 기준 | 설명 |
|------|------|
| **기본 초기 용량** | `16` |
| **기본 로드 팩터** | `0.75` |
| **리사이즈 기준** | 저장 개수 > `capacity * loadFactor` |
| **트리화 기준** | 한 버킷의 노드가 `8`개 이상이고 테이블 크기가 충분히 클 때 |
| **트리화 최소 테이블 크기** | `64`보다 작으면 트리화보다 리사이즈를 우선 |
| **리스트 복귀 기준** | 트리 노드 수가 `6`개 이하로 줄면 다시 리스트화 가능 |
| **트리 구조** | Red-Black Tree |

<div class="warning-box" markdown="1">

**주의**: `0.75`는 충돌률이 아니라 로드 팩터입니다.

즉 "충돌이 75% 이상이면 리해시"가 아니라, **저장된 엔트리 수가 전체 버킷 용량의 75%를 넘으면 리사이즈** 대상이 됩니다.

</div>

### 5. 리사이즈와 리해싱

해시 테이블에 값이 너무 많이 들어가면 버킷 하나당 평균 데이터 수가 늘고 충돌 가능성이 커집니다. 그래서 일정 기준을 넘으면 내부 배열을 키웁니다.

```text
capacity = 16
loadFactor = 0.75
threshold = 12

size가 13이 되면 resize 대상
```

리사이즈가 발생하면 배열 크기가 커지고, 기존 데이터의 버킷 위치가 다시 배치됩니다.

```text
기존 table length = 16
"kim" index = 7

리사이즈 후 table length = 32
"kim" index가 달라질 수 있음
```

이 작업은 순간적으로 비용이 큽니다. 그래서 대량 데이터를 넣을 것을 알고 있다면 초기 용량을 적절히 지정하는 것이 좋습니다.

```java
int expectedSize = 10_000;
Set<Long> ids = new HashSet<>(expectedSize * 4 / 3 + 1);
```

### 6. `hashCode()`와 `equals()` 관계

Java 해시 컬렉션은 보통 다음 순서로 값을 찾습니다.

```text
1. hashCode()로 버킷 위치를 찾는다
2. 같은 버킷 안에서 equals()로 실제 같은 값인지 확인한다
```

그래서 두 메서드는 다음 규칙을 지켜야 합니다.

| 규칙 | 설명 |
|------|------|
| `equals()`가 `true`이면 | `hashCode()`는 반드시 같아야 함 |
| `hashCode()`가 같아도 | `equals()`는 `false`일 수 있음 |
| 객체 상태가 같으면 | 같은 실행 중에는 `hashCode()`가 일관되어야 함 |

<div class="danger-box" markdown="1">

**위험**: `equals()`만 재정의하고 `hashCode()`를 재정의하지 않으면 `HashSet`에서 중복 제거가 깨질 수 있습니다.

</div>

```java
class BadMember {
    private final Long id;

    BadMember(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BadMember member)) {
            return false;
        }
        return Objects.equals(id, member.id);
    }
}

Set<BadMember> members = new HashSet<>();
members.add(new BadMember(1L));
members.add(new BadMember(1L));

System.out.println(members.size()); // 2가 될 수 있음
```

### 7. 가변 객체를 Key로 쓰면 위험한 이유

해시 컬렉션에 들어간 뒤 `hashCode()` 계산에 쓰이는 필드가 바뀌면, 객체가 저장된 버킷과 조회할 버킷이 달라질 수 있습니다.

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

UserKey key = new UserKey("a@example.com");
Map<UserKey, Integer> map = new HashMap<>();
map.put(key, 1);

key.email = "b@example.com";

System.out.println(map.get(key)); // null이 나올 수 있음
```

Key로 사용할 객체는 불변으로 만드는 것이 안전합니다.

```java
public record UserKey(String email) {
}
```

### 8. HashSet과 HashMap의 관계

`HashSet`은 내부적으로 `HashMap`을 사용합니다. Set에는 값만 저장하는 것처럼 보이지만, 내부적으로는 값을 `HashMap`의 Key로 넣고 더미 값을 Value로 둡니다.

```text
HashSet.add("kim")
  ↓
HashMap.put("kim", PRESENT)
```

그래서 `HashSet`에서 중복 여부를 판단하는 방식도 `HashMap`의 Key 판단 방식과 같습니다.

| 컬렉션 | 해시 기준 | 중복 판단 |
|--------|-----------|-----------|
| `HashSet<E>` | 요소 `E` | 같은 요소는 하나만 저장 |
| `HashMap<K, V>` | Key `K` | 같은 Key는 Value를 덮어씀 |

### 9. 평균 O(1)과 최악 O(n)

해시 컬렉션을 설명할 때 `O(1)`이라고 말하지만, 정확히는 **평균 O(1)** 입니다.

| 연산 | 평균 | 충돌이 심한 경우 |
|------|------|------------------|
| `add()` / `put()` | `O(1)` | 리스트 버킷 `O(n)`, 트리 버킷 `O(log n)` |
| `contains()` / `get()` | `O(1)` | 리스트 버킷 `O(n)`, 트리 버킷 `O(log n)` |
| `remove()` | `O(1)` | 리스트 버킷 `O(n)`, 트리 버킷 `O(log n)` |

충돌이 심하게 몰리거나 해시 함수가 값을 잘 분산하지 못하면 성능이 나빠집니다. Java의 트리화 최적화가 있더라도, 해시가 좋을수록 성능이 안정적입니다.

## 주의할 점

<div class="warning-box" markdown="1">

**순회 순서에 의존하지 말 것**

`HashSet`, `HashMap`의 출력 순서가 지금 보기에는 일정해 보여도 보장된 계약이 아닙니다. 순서가 필요하면 `LinkedHashSet`, `LinkedHashMap`, `TreeSet`, `TreeMap`을 사용합니다.

</div>

<div class="danger-box" markdown="1">

**해시 컬렉션의 Key는 불변으로 둘 것**

`HashMap`의 Key나 `HashSet`의 요소가 저장 후 바뀌면 조회, 삭제, 중복 판단이 깨질 수 있습니다.

</div>

<div class="warning-box" markdown="1">

**`Math.abs(hashCode) % length`는 조심**

`Math.abs(Integer.MIN_VALUE)`는 여전히 음수입니다. 직접 해시 인덱스를 만들 때는 `Math.floorMod(hashCode, length)`를 사용하거나 Java 컬렉션 구현체에 맡기는 편이 안전합니다.

</div>

<div class="tip-box" markdown="1">

**암호학적 해시와 컬렉션 해시는 목적이 다릅니다.**

`hashCode()`는 빠른 분산이 목적이고, SHA-256 같은 암호학적 해시는 위변조 검증과 역추적 난이도가 목적입니다. 비밀번호 저장, 서명, 무결성 검증에는 `hashCode()`를 쓰면 안 됩니다.

</div>

## 베스트 프랙티스

| 권장 방식 | 이유 |
|-----------|------|
| **Key는 불변 객체 사용** | 저장 후 해시 위치가 바뀌지 않음 |
| **`equals()`와 `hashCode()`는 함께 재정의** | 해시 컬렉션의 중복 판단 계약을 지킴 |
| **같은 필드를 기준으로 비교** | `equals()`와 `hashCode()`의 기준이 달라지는 실수를 방지 |
| **대량 삽입 시 초기 용량 지정** | 중간 리사이즈 비용을 줄일 수 있음 |
| **순서가 필요하면 Linked/Tree 계열 선택** | `HashSet`, `HashMap` 순서에 의존하지 않음 |
| **직접 해시 함수를 만들기보다 표준 구현 사용** | 충돌, 음수 인덱스, 리사이즈 같은 세부 처리를 줄임 |
| **객체 식별 기준을 먼저 정함** | `id` 기준인지, 전체 필드 기준인지 명확해짐 |

## 실무에서는?

| 상황 | 사용 예 |
|------|---------|
| **중복 요청 방지** | 처리한 요청 ID를 `HashSet`에 저장 |
| **캐시** | Key로 계산 결과나 외부 API 응답을 `HashMap`에 저장 |
| **집계** | 단어 빈도, 사용자별 카운트를 `HashMap<T, Integer>`로 계산 |
| **방문 체크** | 그래프 탐색, 배치 처리에서 이미 방문한 ID 기록 |
| **권한/태그 검사** | 사용자가 특정 권한이나 태그를 가졌는지 빠르게 확인 |
| **도메인 객체 비교** | Entity/Value Object의 `equals()`/`hashCode()` 설계 |
| **인메모리 인덱스** | DB 조회 전 임시로 ID -> 객체 매핑 |

## 정리

| 항목 | 설명 |
|------|------|
| **해시 목적** | 임의의 값을 배열 인덱스로 바꿔 빠르게 찾기 |
| **핵심 구조** | 버킷 배열 + 충돌 처리 |
| **대표 컬렉션** | `HashSet`, `HashMap` |
| **평균 성능** | 삽입, 삭제, 조회 `O(1)` |
| **핵심 계약** | `equals()`가 같으면 `hashCode()`도 같아야 함 |
| **주의** | 순서 보장 없음, 가변 Key 금지, 충돌 가능 |

---

**관련 파일:**
- [Set](Set.md) — 중복 없는 집합
- [Map](Map.md) — Key-Value 저장
- [Collection](Collection.md) — 컬렉션 프레임워크 개요
- [불변객체](../core/불변객체VS가변객체.md) — 안전한 Key 설계
