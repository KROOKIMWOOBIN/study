## 명령형 vs 선언형 프로그래밍

### 왜 구분하는지

같은 결과를 얻지만 **코드를 작성하는 방식이 완전히 다릅니다**:
- **명령형**: "어떻게 할 것인가"에 초점 → 절차를 상세히 기술
- **선언형**: "무엇을 할 것인가"에 초점 → 결과만 표현

Java 8 이전에는 **명령형만 가능**했지만, 람다와 스트림 도입 후 **선언형 작성 가능**해졌습니다.

<div class="concept-box" markdown="1">

**핵심**: 두 방식은 같은 문제의 다른 표현 방식입니다. 선언형이 더 간결하고 안전합니다.

</div>

### 어떻게 쓰는지

#### 명령형 (Imperative) - HOW

```java
// 문제: 리스트에서 길이 3 초과인 이름을 대문자로 변환

// ❌ 명령형: 절차를 상세히 기술
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");
List<String> result = new ArrayList<>();

for (String name : names) {
    if (name.length() > 3) {  // 1단계: 필터링
        result.add(name.toUpperCase());  // 2단계: 변환
    }
}

System.out.println(result);  // [ALICE, CHARLIE, DIANA]
```

**설명:**
1. 빈 List 생성
2. 반복문으로 각 요소 순회
3. 조건 확인
4. 조건 만족 시 결과에 추가

#### 선언형 (Declarative) - WHAT

```java
// ✅ 선언형: 원하는 결과만 표현
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");

List<String> result = names.stream()
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)
    .toList();

System.out.println(result);  // [ALICE, CHARLIE, DIANA]
```

**설명:**
- "길이 3 초과인 이름을 대문자로 변환해서 리스트로 만들어"
- HOW(어떻게)는 스트림 라이브러리가 담당

#### 더 복잡한 예시

**요구사항:** "모든 주문에서 금액 > 100인 항목만 찾아, 카테고리별로 그룹화하고, 카테고리별 합계 계산"

```java
List<Order> orders = ...; // 주문 목록

// ❌ 명령형: 복잡함
Map<String, Long> result = new HashMap<>();
for (Order order : orders) {
    for (Item item : order.getItems()) {
        if (item.getPrice() > 100) {
            String category = item.getCategory();
            if (!result.containsKey(category)) {
                result.put(category, 0L);
            }
            result.put(category, result.get(category) + item.getPrice());
        }
    }
}

// ✅ 선언형: 간결함
Map<String, Long> result = orders.stream()
    .flatMap(order -> order.getItems().stream())
    .filter(item -> item.getPrice() > 100)
    .collect(Collectors.groupingBy(
        Item::getCategory,
        Collectors.summingLong(Item::getPrice)
    ));
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **컬렉션 처리** | ✅ 선언형 (Stream) | 간결하고 버그 적음 |
| **데이터 변환/필터링** | ✅ 선언형 (Stream) | 의도 명확 |
| **성능 최적화 필요** | ✅ 선언형 (Stream) | 병렬화 자동 지원 |
| **매우 간단한 루프** | ❌ 명령형 (for-each) | for-each가 더 읽기 쉬움 |
| **상태 변경 추적** | ❌ 명령형 | 선언형으로는 중간 상태 추적 어려움 |

### 장점

#### 명령형 장점
| 장점 | 설명 |
|------|------|
| **직관적** | 원리가 명확함 (초보자 친화적) |
| **디버깅** | 단계별 상태 추적 가능 |
| **세밀한 제어** | 중간 결과에 접근 가능 |

#### 선언형 장점
| 장점 | 설명 |
|------|------|
| **간결함** | 코드 줄 수 크게 감소 |
| **안전성** | 불변성 강조로 버그 감소 |
| **병렬화** | `parallelStream()`으로 자동 병렬 처리 |
| **의도 명확** | 무엇을 하려는지 한눈에 파악 |

### 단점

#### 명령형 단점
| 단점 | 설명 |
|------|------|
| **길고 복잡** | 간단한 작업도 코드 길어짐 |
| **상태 변경** | 버그 가능성 높음 |
| **가독성** | 로직이 절차에 묻혀서 의도 불명확 |
| **병렬화 어려움** | 수동으로 처리해야 함 |

#### 선언형 단점
| 단점 | 설명 |
|------|------|
| **학습곡선** | 함수형 개념 이해 필요 |
| **디버깅** | 라이브러리 내부 동작 파악 어려움 |
| **오버헤드** | 함수 객체 생성 비용 |
| **중간 상태 접근** | 각 단계 중간값 확인 어려움 |

### 특징

#### 명령형 특징

1. **상태 변경 기반** → Mutation에 의존
```java
result.add(...);  // result 상태 계속 변경
```

2. **제어 흐름 직접 관리**
```java
for (...) {  // 반복 시점 명시
    if (...) {  // 조건 명시
        // 실행 시점 명시
    }
}
```

3. **외부 변수 수정 가능**
```java
int count = 0;
for (...) {
    count++;  // 수정 가능
}
```

#### 선언형 특징

1. **불변성 기반** → 새로운 값 생성
```java
stream.filter(...).map(...);  // 원본 변경 없음
```

2. **의도 명확** → 체이닝으로 단계 표현
```java
stream
    .filter(...)  // "필터링해"
    .map(...)     // "변환해"
    .collect(...) // "수집해"
```

3. **내부 구현 추상화**
```java
stream.collect(Collectors.groupingBy(...));
// 어떻게 그룹화하는지는 라이브러리가 처리
```

### 주의할 점

<div class="warning-box" markdown="1">

**⚠️ 명령형과 선언형 섞지 않기**

```java
// ❌ 나쁜 패턴: 섞음
List<String> result = new ArrayList<>();
names.stream()
    .filter(name -> name.length() > 3)
    .forEach(name -> result.add(name.toUpperCase()));  // ❌ 상태 변경
    
System.out.println(result);

// ✅ 올바른 방식: 선언형으로 통일
List<String> result = names.stream()
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)
    .toList();
```

이유: forEach + add = 명령형 스타일이 섞임, 병렬화 시 위험

</div>

<div class="warning-box" markdown="1">

**⚠️ 성능 고려하기**

```java
// ❌ 작은 데이터셋에서는 오버헤드
List<Integer> numbers = Arrays.asList(1, 2, 3);
numbers.stream()
    .filter(x -> x > 0)
    .forEach(System.out::println);

// ✅ 단순한 경우는 for-each 더 빠름
for (int num : numbers) {
    if (num > 0) {
        System.out.println(num);
    }
}
```

**선택 기준:**
- 요소 수 많음 (> 100) → 선언형
- 변환/필터링 복잡 → 선언형
- 단순 순회 → for-each

</div>

<div class="danger-box" markdown="1">

**❌ 선언형에서 외부 변수 수정 금지**

```java
int[] count = {0};

// ❌ 반복문 스타일 → 선언형이 아님
list.stream()
    .forEach(x -> count[0]++);

// ✅ 진정한 선언형
int newCount = list.stream()
    .filter(x -> x > 0)
    .count();
```

</div>

### 정리

| 항목 | 명령형 | 선언형 |
|------|--------|--------|
| **관점** | HOW (어떻게) | WHAT (무엇) |
| **코드 스타일** | 절차형 | 함수형 |
| **가독성** | 낮음 (복잡함) | 높음 (간결) |
| **병렬화** | 수동 | 자동 (`parallelStream`) |
| **버그** | 상태 변경으로 높음 | 불변성으로 낮음 |
| **언제 쓰는가** | 간단한 루프 | 복잡한 데이터 처리 |

<div class="success-box" markdown="1">

**권장**: Java 8+에서는 **선언형(스트림)을 기본으로 사용**하되, 매우 간단한 경우만 명령형 사용

</div>

---

**관련 내용:**
- [람다 기초](start.md) — 선언형을 가능하게 하는 문법
- [스트림](stream.md) — 선언형의 핵심 도구
- [Stream API](streamApi.md) — 선언형 구체적 사용법
