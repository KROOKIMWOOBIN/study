## Map (맵)

### 왜 쓰는지

배열/List는 **인덱스(0, 1, 2...)**로 접근합니다. 하지만:
- 사용자 정보를 이름으로 찾고 싶다면?
- 설정값을 키값으로 접근하고 싶다면?

<div class="concept-box" markdown="1">

**핵심**: Map은 **Key-Value 쌍**으로 데이터를 저장하는 자료구조입니다. Key로 빠르게 Value를 검색할 수 있습니다.

</div>

### 어떻게 쓰는지

#### 기본 사용법

```java
// 1. 생성
Map<String, Integer> ages = new HashMap<>();

// 2. 데이터 추가
ages.put("Alice", 25);
ages.put("Bob", 30);
ages.put("Charlie", 28);

// 3. 데이터 조회
System.out.println(ages.get("Alice"));  // 25

// 4. 데이터 수정
ages.put("Alice", 26);  // 같은 Key로 put하면 값 덮어쓰기

// 5. 데이터 삭제
ages.remove("Bob");

// 6. 존재 여부 확인
if (ages.containsKey("Alice")) {
    System.out.println("Alice 있음");
}

// 7. 전체 순회
for (Map.Entry<String, Integer> entry : ages.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

#### 생성 방식

```java
// 빈 Map 생성
Map<String, Integer> map1 = new HashMap<>();
Map<String, Integer> map2 = new LinkedHashMap<>();
Map<String, Integer> map3 = new TreeMap<>();

// 초기값과 함께 생성
Map<String, Integer> map4 = Map.of(
    "Alice", 25,
    "Bob", 30,
    "Charlie", 28
);

// 다른 Map 복사
Map<String, Integer> map5 = new HashMap<>(map1);
```

#### 고급 메서드

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 85);
scores.put("Bob", 90);

// getOrDefault: key 없으면 기본값 반환
System.out.println(scores.getOrDefault("Charlie", 0));  // 0

// putIfAbsent: key 없을 때만 put
scores.putIfAbsent("Charlie", 75);  // Charlie 추가
scores.putIfAbsent("Alice", 100);   // Alice는 이미 있으므로 무시

// computeIfPresent: key 있으면 값 변환
scores.computeIfPresent("Alice", (k, v) -> v + 5);  // 85 → 90

// computeIfAbsent: key 없으면 값 계산하여 put
scores.computeIfAbsent("Diana", k -> 88);  // Diana 추가

// values(), keySet(), entrySet()
System.out.println(scores.values());  // [90, 90, 75, 88]
System.out.println(scores.keySet());  // [Alice, Bob, Charlie, Diana]
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **Key로 빠르게 값 찾기** | ✅ HashMap | O(1) 성능 |
| **입력 순서 유지 필요** | ✅ LinkedHashMap | 순서 보장 + HashMap 성능 |
| **정렬된 상태 유지** | ✅ TreeMap | 자동 정렬 |
| **빈번한 정렬 필요** | ❌ HashMap 후 정렬 | TreeMap보다 빠를 수 있음 |
| **스레드 안전 필요** | ✅ ConcurrentHashMap | 멀티스레드 환경 |

### 장점

| 장점 | 설명 |
|------|------|
| **빠른 조회** | Key로 O(1) 시간에 검색 |
| **간편한 관계 표현** | Key-Value로 자연스러운 매핑 |
| **유연한 Key 타입** | 어떤 타입이든 Key로 가능 |
| **내장 메서드 풍부** | getOrDefault, compute 등 편의 메서드 |

### 단점

| 단점 | 설명 |
|------|------|
| **메모리 오버헤드** | 해시 테이블 유지에 메모리 사용 |
| **순서 보장 안됨** (HashMap만) | 입력 순서와 다를 수 있음 |
| **정렬 비용** | TreeMap은 O(log n) 비용 |
| **null key 제약** | TreeMap은 null key 불가 |

### 특징

#### HashMap — 가장 빠른 검색

```java
// 특징
Map<String, Integer> map = new HashMap<>();

// 1. 해시 테이블 기반 → O(1) 조회
map.put("Alice", 25);
map.get("Alice");  // 매우 빠름

// 2. 순서 보장 안함
map.put("Bob", 30);
map.put("Charlie", 28);
// 저장 순서와 다를 수 있음

// 3. null key/value 허용
map.put(null, -1);
map.put("Unknown", null);

// 4. 동기화 안됨 (멀티스레드 환경에서 위험)
```

| 특징 | 설명 |
|------|------|
| **성능** | O(1) 조회, O(1) 삽입 |
| **순서** | 보장하지 않음 |
| **null** | Key/Value 모두 허용 |
| **스레드 안전** | 아니오 |
| **정렬** | 자동 정렬 안됨 |

#### LinkedHashMap — 순서를 유지하는 HashMap

```java
// 특징
Map<String, Integer> map = new LinkedHashMap<>();

// 1. 입력 순서 유지
map.put("Alice", 25);
map.put("Bob", 30);
map.put("Charlie", 28);
// 순회 시: Alice → Bob → Charlie (입력 순서)

// 2. HashMap보다 약간 느림 (LinkedList 오버헤드)
// 3. null 허용
// 4. 동기화 안됨

// 5. 접근 순서 모드 (LRU 캐시 구현 용)
Map<String, Integer> lruMap = new LinkedHashMap<>(16, 0.75f, true);
lruMap.put("A", 1);
lruMap.put("B", 2);
lruMap.put("C", 3);
lruMap.get("A");  // A 접근
// 순회 시: B → C → A (최근 접근 순서)
```

| 특징 | 설명 |
|------|------|
| **성능** | HashMap과 거의 동일 |
| **순서** | 입력 순서 또는 접근 순서 유지 |
| **null** | Key/Value 모두 허용 |
| **스레드 안전** | 아니오 |
| **정렬** | 자동 정렬 안됨 |

#### TreeMap — 정렬된 맵

```java
// 특징
Map<String, Integer> map = new TreeMap<>();

// 1. Key 기준으로 자동 정렬
map.put("Charlie", 28);
map.put("Alice", 25);
map.put("Bob", 30);
// 순회 시: Alice → Bob → Charlie (Key 정렬 순)

// 2. O(log n) 성능 (해시보다 느림)
// 3. null key 허용하지 않음
// 4. Comparable 또는 Comparator 필요
// 5. 동기화 안됨

// 커스텀 정렬
Map<String, Integer> reverseMap = new TreeMap<>((a, b) -> b.compareTo(a));
reverseMap.put("Alice", 25);
reverseMap.put("Bob", 30);
// 순회 시: Bob → Alice (역순)
```

| 특징 | 설명 |
|------|------|
| **성능** | O(log n) 조회, O(log n) 삽입 |
| **순서** | Key 기준으로 자동 정렬 |
| **null** | Key는 허용 안함, Value는 허용 |
| **스레드 안전** | 아니오 |
| **정렬** | 자동 정렬 (커스텀 가능) |

### HashMap vs LinkedHashMap vs TreeMap 비교

| 구분 | HashMap | LinkedHashMap | TreeMap |
|------|---------|---------------|---------|
| **성능** | O(1) | O(1) | O(log n) |
| **순서 보장** | ❌ | ✅ (입력/접근) | ✅ (정렬) |
| **정렬** | ❌ | ❌ | ✅ (자동) |
| **null key** | ✅ 1개 | ✅ 1개 | ❌ |
| **null value** | ✅ | ✅ | ✅ |
| **스레드 안전** | ❌ | ❌ | ❌ |
| **메모리** | 보통 | 많음 (LinkedList) | 보통 |

### 실제 사용 시나리오

```java
// 1. HashMap: 일반적인 데이터 저장
Map<String, String> config = new HashMap<>();
config.put("database.url", "localhost:5432");
config.put("app.name", "MyApp");
String dbUrl = config.get("database.url");

// 2. LinkedHashMap: 설정값 순서 유지 필요
Map<String, String> orderedConfig = new LinkedHashMap<>();
// 저장한 순서대로 읽음

// 3. TreeMap: 결과를 정렬된 상태로 표시
Map<Integer, String> ranks = new TreeMap<>();
ranks.put(3, "Third");
ranks.put(1, "First");
ranks.put(2, "Second");
// 순회하면 1, 2, 3 순서로 출력

// 4. 접근 순서 기반 LRU 캐시
Map<String, Integer> cache = new LinkedHashMap<>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > 5;  // 최대 5개 유지, 가장 오래된 항목 제거
    }
};
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ TreeMap에 null key 저장 불가**
```java
Map<String, Integer> map = new TreeMap<>();
map.put(null, 10);  // ❌ NullPointerException
```

**✅ 올바른 방식:**
```java
Map<String, Integer> map = new HashMap<>();
map.put(null, 10);  // HashMap은 가능
```

</div>

<div class="warning-box" markdown="1">

**⚠️ Key는 불변 객체를 사용**
```java
// ❌ 위험: 변경 가능한 객체
List<String> key = new ArrayList<>();
key.add("user1");
map.put(key, 100);
key.add("user2");  // key 변경!
map.get(key);  // 조회 실패 가능

// ✅ 안전: 불변 객체 (String, Integer 등)
map.put("user1", 100);
map.put(123, "value");
```

이유: Key의 hashCode가 변경되면 Map에서 찾을 수 없음

</div>

<div class="warning-box" markdown="1">

**⚠️ TreeMap 커스텀 Comparator 사용 시 주의**
```java
// 대소문자 구분 안하는 정렬
Map<String, Integer> caseInsensitiveMap = new TreeMap<>(String::compareToIgnoreCase);
caseInsensitiveMap.put("Alice", 25);
caseInsensitiveMap.put("alice", 26);
// "alice"는 덮어써짐 (Comparator상 같은 key로 간주)
```

</div>

### 정리

| 선택 기준 | 추천 |
|----------|------|
| **가장 빠른 검색** | HashMap |
| **순서 중요** | LinkedHashMap |
| **항상 정렬된 상태** | TreeMap |
| **멀티스레드** | ConcurrentHashMap |

---

**관련 파일:** [Hash](Hash.md), [Collection](Collection.md), [Set](Set.md), [List](List.md)
