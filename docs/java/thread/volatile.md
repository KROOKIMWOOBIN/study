## volatile 키워드 & 메모리 가시성

### 왜 쓰는지

멀티코어 CPU에서 **각 코어가 자신의 캐시에 값을 보관**하므로:
- 스레드 A가 변경한 값을 스레드 B가 못 보는 경우 발생
- 메인 메모리와 캐시가 동기화되지 않음

`volatile`로 **메모리 가시성을 보장**할 수 있습니다.

<div class="concept-box" markdown="1">

**핵심**: `volatile` 키워드는 **해당 변수의 모든 읽기/쓰기를 메인 메모리와 직접 수행**하여, 한 스레드의 변경사항이 다른 스레드에서 즉시 보이게 합니다.

</div>

### 어떻게 쓰는지

#### 메모리 가시성 문제

```java
public class MemoryVisibilityProblem {
    // ❌ 문제: volatile 없음
    private boolean running = true;
    
    public void start() {
        // 스레드 1: 계속 실행
        new Thread(() -> {
            while (running) {  // 캐시 값 읽음
                // 작업
            }
            System.out.println("종료됨");
        }).start();
        
        // 메인: 값 변경
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        running = false;  // 메인 메모리에 쓰임
        // 하지만 스레드 1의 캐시에는 여전히 true
        // → 루프가 계속 실행됨 (또는 시간 지나서 어느 순간 감지)
    }
}

// ✅ 해결: volatile 사용
private volatile boolean running = true;
// 이제 running의 모든 접근이 메인 메모리와 직접 수행
// → 즉시 변경사항 인지
```

#### volatile 기본 사용

```java
public class VolatileExample {
    // volatile: 메인 메모리와 직접 소통
    private volatile boolean flag = false;
    private volatile int count = 0;
    
    public void writerThread() {
        flag = true;          // 메인 메모리에 즉시 쓰기
        count = 100;          // 메인 메모리에 즉시 쓰기
    }
    
    public void readerThread() {
        while (!flag) {       // 메인 메모리에서 매번 읽기
            // 대기
        }
        System.out.println(count);  // 메인 메모리에서 읽기 (항상 100)
    }
    
    public static void main(String[] args) throws InterruptedException {
        VolatileExample example = new VolatileExample();
        
        Thread writer = new Thread(example::writerThread);
        Thread reader = new Thread(example::readerThread);
        
        reader.start();
        writer.start();
        
        writer.join();
        reader.join();
    }
}

// 출력: 100 (volatile이 있으므로 항상 최신 값)
```

#### volatile vs synchronized

```java
// 1️⃣ volatile: 가시성만 보장 (원자성 X)
private volatile int counter = 0;

// ❌ 여전히 race condition 발생
counter++;  // 세 단계: 읽기 → 증가 → 쓰기

// 2️⃣ synchronized: 원자성도 보장
private int counter = 0;

// ✅ 원자적으로 실행
synchronized void increment() {
    counter++;
}

// 3️⃣ 필요한 것만 선택
private volatile boolean running = true;  // volatile: 단순 플래그
private int data = 0;                    // synchronized: 복잡한 상태

synchronized void updateData(int value) {
    data = value;
    running = false;
}
```

#### 해피포닝-비포 (Happens-Before) 규칙

```java
public class HappensBeforeExample {
    private int a = 0;
    private volatile int b = 0;  // volatile 변수
    
    public void writer() {
        a = 1;      // (1)
        b = 2;      // (2) volatile write
    }
    
    public void reader() {
        if (b == 2) {  // (3) volatile read
            // (3)이 (2)를 보면, (2)보다 전에 일어난 모든 변수 (a 포함)도 보임
            System.out.println(a);  // 반드시 1이 출력됨
        }
    }
    
    // Happens-Before 관계:
    // (1) happens-before (2) happens-before (3)
    // → (3)에서 b를 보면 a도 업데이트된 값을 봄
}
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **플래그 변수** | ✅ volatile | 단순 가시성만 필요 |
| **상태 변수** | ✅ volatile | 여러 스레드가 읽음 |
| **복합 연산** | ❌ synchronized | 원자성도 필요 |
| **카운터** | ❌ AtomicInteger | 원자적 증가 필요 |

### 장점

| 장점 | 설명 |
|------|------|
| **가시성** | 메모리 가시성 보장 |
| **경량** | synchronized보다 오버헤드 작음 |
| **성능** | 캐시 동기화만 수행 |
| **간편성** | 단순 플래그에 적합 |

### 단점

| 단점 | 설명 |
|------|------|
| **원자성 없음** | 복합 연산은 보호 안됨 (i++ 등) |
| **혼동 가능** | synchronized와 다른 의미 |
| **캐시 비용** | 메인 메모리 접근이 캐시보다 느림 |
| **제한적** | 플래그나 상태 변수에만 적합 |

### 특징

#### 1. CPU 캐시 구조

```text
Core 1               Core 2
  │                   │
  L1 Cache            L1 Cache
  │                   │
  L2 Cache            L2 Cache
  └────────┬──────────┘
           │
        Main Memory (RAM)

volatile 변수는 캐시를 우회하고 Main Memory에 직접 접근
```

#### 2. Memory Barrier (메모리 배리어)

```java
// volatile write: store barrier
volatile int x = 1;  // 메인 메모리에 즉시 기록

// volatile read: load barrier
int y = x;          // 메인 메모리에서 읽기

// JVM/CPU가 메모리 배리어를 삽입하여 가시성 보장
```

#### 3. Java Memory Model (JMM)

```text
JMM이 정의하는 3가지 보장:

1️⃣ 가시성 (Visibility)
   한 스레드의 쓰기가 다른 스레드에서 언제 보이는가?
   → volatile, synchronized, lock

2️⃣ 원자성 (Atomicity)
   연산이 중간에 끊기지 않고 수행되는가?
   → synchronized, AtomicInteger, ...

3️⃣ 순서 보장 (Ordering)
   명령어가 재정렬되지 않는가?
   → volatile, synchronized, happens-before
```

#### 4. volatile vs AtomicInteger

```java
// 1️⃣ volatile: 가시성만
private volatile int count = 0;
count++;  // ❌ race condition

// 2️⃣ AtomicInteger: 가시성 + 원자성
private AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();  // ✅ 원자적

// 성능 비교
// volatile:          쓰기는 빠르지만 읽기-수정-쓰기는 위험
// AtomicInteger:    원자적 연산으로 안전 (약간 느림)
// synchronized:     가장 안전하지만 가장 느림
```

#### 5. Reordering 문제

```java
public class ReorderingExample {
    private int a = 0;
    private int b = 0;
    
    public void method1() {
        a = 1;
        b = 2;  // JIT 컴파일러가 순서 바꿀 수 있음
    }
    
    // ✅ 올바른 방식: volatile로 순서 보장
    private int a = 0;
    private volatile int b = 0;
    
    public void method1() {
        a = 1;
        b = 2;  // b는 volatile이므로 순서 보장됨
    }
}
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ volatile로 원자성 기대하기**

```java
private volatile int counter = 0;

// ❌ 여전히 race condition
counter++;  // 읽기 → 증가 → 쓰기 (3단계)

// 스레드 A: 읽기(5) → 증가(6) → 쓰기(6)
// 스레드 B:       읽기(5) → 증가(6) → 쓰기(6)
// 결과: 6 (7이 되어야 함!)

// ✅ 올바른 방식: synchronized 또는 AtomicInteger
private AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // 원자적
```

</div>

<div class="warning-box" markdown="1">

**⚠️ volatile 배열 원소**

```java
// volatile 배열
private volatile int[] array = new int[10];

// ❌ 착각: array[0]이 volatile이 되는 건 아님
array[0] = 5;  // array[0]은 가시성 보장 없음
               // (array 참조 자체만 volatile)

// ✅ 원소도 보장하려면:
// 1. 모든 접근을 synchronized로 보호
// 2. AtomicIntegerArray 사용

private AtomicIntegerArray array = new AtomicIntegerArray(10);
array.set(0, 5);  // 원자적이고 가시성 보장
```

</div>

<div class="warning-box" markdown="1">

**⚠️ 성능 영향**

```java
// ❌ 모든 변수를 volatile로 만들기 (안티패턴)
private volatile int a, b, c, d, e, f;

// 메인 메모리 접근 반복 → 성능 저하

// ✅ 필요한 것만 volatile
private volatile boolean flag;  // 플래그만
private int dataA, dataB;       // 데이터는 다른 방식으로 보호
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **volatile** | 메모리 가시성 보장 |
| **원자성** | 보장 안 함 (플래그 제외) |
| **사용처** | 플래그, 상태 변수 |
| **한계** | 복합 연산 불가 |
| **대안** | synchronized, AtomicInteger |
| **주의** | i++는 volatile로도 보호 안됨 |

---

**관련 파일:**
- [synchronized](synchronized.md) — 동기화 메커니즘
- [Thread2](Thread2.md) — 스레드 상태 관리
- [동시성컬렉션](동시성컬렉션.md) — 스레드 안전 자료구조
