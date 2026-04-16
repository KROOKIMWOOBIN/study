## 스레드 상태 관리 (Thread State & Control)

### 왜 쓰는지

스레드는 **생성에서 종료까지 여러 상태**를 거칩니다:
- 언제 실행되는가 (RUNNABLE)?
- 언제 멈추는가 (BLOCKED, WAITING)?
- 다른 스레드를 어떻게 제어하는가?

상태를 이해하고 제어해야 **예측 가능한 멀티스레드 프로그램**을 만들 수 있습니다.

<div class="concept-box" markdown="1">

**핵심**: 스레드는 **NEW → RUNNABLE → 실행/대기 → TERMINATED**의 6가지 상태를 가지며, 상태 전이를 제어하는 메서드(`join()`, `sleep()`, `wait()`, `notify()`)가 있습니다.

</div>

### 어떻게 쓰는지

#### 스레드 상태 (Thread State)

```java
public enum State {
    NEW,           // 생성됨, 아직 시작 안됨
    RUNNABLE,      // 실행 가능 상태 (CPU 할당 대기 또는 실행 중)
    BLOCKED,       // synchronized 락 대기
    WAITING,       // 다른 스레드의 신호 무기한 대기
    TIMED_WAITING, // 다른 스레드의 신호 또는 타임아웃 대기
    TERMINATED     // 실행 완료
}

// 상태 확인
Thread thread = new Thread(() -> {
    // 작업
});

System.out.println(thread.getState());  // NEW
thread.start();
System.out.println(thread.getState());  // RUNNABLE
```

#### 스레드 상태 전이 다이어그램

```text
NEW
 ↓
[start()]
 ↓
RUNNABLE (실행 대기 또는 실행 중)
 ├─→ [synchronized 진입] ──→ BLOCKED ──→ [락 획득] ──→ RUNNABLE
 ├─→ [sleep()] ──────────→ TIMED_WAITING ──→ [시간 경과] ──→ RUNNABLE
 ├─→ [wait()] ──────────→ WAITING ──→ [notify()] ──→ RUNNABLE
 ├─→ [join()] ──────────→ WAITING ──→ [대상 스레드 종료] ──→ RUNNABLE
 └─→ [run() 완료] ─────→ TERMINATED
```

#### join(): 다른 스레드 완료 대기

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                System.out.println("작업 시작");
                Thread.sleep(2000);  // 2초 대기
                System.out.println("작업 완료");
            } catch (InterruptedException e) {
                System.out.println("작업 중단됨");
            }
        });
        
        worker.start();
        System.out.println("메인: worker 완료 대기");
        worker.join();  // worker가 종료될 때까지 메인 스레드 대기
        System.out.println("메인: 완료");
    }
}

// 출력:
// 메인: worker 완료 대기
// 작업 시작
// 작업 완료
// 메인: 완료
```

#### sleep(): 스레드 일시 정지

```java
// 1️⃣ 고정 시간 대기
try {
    Thread.sleep(1000);  // 1초 대기
} catch (InterruptedException e) {
    System.out.println("수면 중단");
}

// 2️⃣ 진행률 표시
for (int i = 0; i <= 100; i += 20) {
    System.out.println("진행 중: " + i + "%");
    try {
        Thread.sleep(500);  // 500ms 대기
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  // 인터럽트 상태 복원
        break;
    }
}

// 3️⃣ 재시도 (Retry with delay)
int retries = 3;
for (int i = 0; i < retries; i++) {
    try {
        callApi();
        break;  // 성공
    } catch (Exception e) {
        if (i < retries - 1) {
            Thread.sleep(1000 * (long) Math.pow(2, i));  // 지수 백오프
        }
    }
}
```

#### wait() / notify(): 스레드 간 신호

```java
public class Buffer {
    private Queue<String> queue = new LinkedList<>();
    private int capacity = 5;
    
    // 아이템 추가
    public synchronized void put(String item) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();  // 버퍼 가득 차면 대기
        }
        queue.add(item);
        System.out.println("추가: " + item);
        notifyAll();  // 대기 중인 스레드 깨우기
    }
    
    // 아이템 제거
    public synchronized String get() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();  // 버퍼 비면 대기
        }
        String item = queue.poll();
        System.out.println("제거: " + item);
        notifyAll();  // 대기 중인 스레드 깨우기
        return item;
    }
}

// 사용
Buffer buffer = new Buffer();

// Producer (추가하는 스레드)
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            buffer.put("아이템 " + i);
            Thread.sleep(100);
        } catch (InterruptedException e) {}
    }
}).start();

// Consumer (제거하는 스레드)
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            buffer.get();
            Thread.sleep(300);
        } catch (InterruptedException e) {}
    }
}).start();
```

#### yield(): CPU 양보

```java
// CPU 시간을 다른 스레드에 양보
for (int i = 0; i < 10_000_000; i++) {
    if (i % 100_000 == 0) {
        Thread.yield();  // 다른 스레드가 실행될 기회 제공
    }
}
```

### 언제 쓰는지

| 상황 | 메서드 | 이유 |
|------|--------|------|
| **스레드 완료 대기** | `join()` | 순차 실행 필요 |
| **일시 정지** | `sleep()` | 작업 간격, 재시도 대기 |
| **조건 대기** | `wait()` | 조건 충족까지 대기 |
| **신호 전송** | `notify()` | 대기 중인 스레드 깨우기 |
| **CPU 양보** | `yield()` | 다른 스레드 우선순위 |

### 장점

| 장점 | 설명 |
|------|------|
| **순차 제어** | `join()`으로 스레드 실행 순서 제어 |
| **효율적 대기** | `sleep()`, `wait()`로 불필요한 CPU 사용 방지 |
| **스레드 간 통신** | `wait()/notify()`로 동기화 |
| **상태 파악** | `getState()`로 상태 확인 |

### 단점

| 단점 | 설명 |
|------|------|
| **복잡성** | 6가지 상태와 메서드 관계 이해 필요 |
| **오버헤드** | 컨텍스트 스위칭, 동기화 비용 |
| **데드락** | 잘못된 `wait()/notify()` 사용 시 교착 가능 |
| **스핀락** | 지속적 상태 확인은 CPU 낭비 |

### 특징

#### 1. join()의 활용

```java
// 모든 워커 스레드가 완료될 때까지 대기
Thread[] workers = new Thread[10];
for (int i = 0; i < 10; i++) {
    workers[i] = new Thread(() -> {
        System.out.println("작업 중: " + Thread.currentThread().getName());
    });
    workers[i].start();
}

// 모든 워커 완료 대기
for (Thread worker : workers) {
    worker.join();
}
System.out.println("모든 작업 완료");
```

#### 2. 타임아웃이 있는 join()

```java
Thread worker = new Thread(() -> {
    try {
        Thread.sleep(10000);  // 10초
    } catch (InterruptedException e) {}
});

worker.start();

// 3초만 대기
try {
    worker.join(3000);
} catch (InterruptedException e) {}

if (worker.isAlive()) {
    System.out.println("워커가 아직 실행 중");
}
```

#### 3. InterruptedException 처리

```java
Thread worker = new Thread(() -> {
    try {
        for (int i = 0; i < 100; i++) {
            System.out.println("작업: " + i);
            Thread.sleep(1000);
        }
    } catch (InterruptedException e) {
        System.out.println("작업 중단됨");
        // 인터럽트 상태 복원
        Thread.currentThread().interrupt();
    }
});

worker.start();

// 5초 후 중단
Thread.sleep(5000);
worker.interrupt();  // InterruptedException 발생
```

#### 4. wait() vs sleep() vs yield()

| 메서드 | 목적 | 락 | 깨우기 |
|--------|------|-----|--------|
| `sleep()` | 시간 경과 대기 | 유지 | 타임아웃 |
| `wait()` | 조건 대기 | 해제 | `notify()` |
| `yield()` | CPU 양보 | 유지 | 즉시 |

```java
synchronized void example() {
    Thread.sleep(1000);    // 락 유지하면서 1초 대기
    
    while (condition == false) {
        wait();            // 락 해제하고 대기, notify 시 다시 획득
    }
    
    Thread.yield();        // 락 유지하면서 다른 스레드에 양보
}
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ notify()와 notifyAll() 혼동**

```java
synchronized void wrongNotification() {
    notify();  // ❌ 대기 중인 여러 스레드 중 1개만 깨움
    // 깨어난 스레드가 조건을 만족하지 않으면, 다른 스레드는 영구 대기
}

synchronized void correctNotification() {
    notifyAll();  // ✅ 모든 대기 스레드 깨우기
    // 각 스레드가 다시 조건 확인 (while 루프)
}
```

</div>

<div class="danger-box" markdown="1">

**❌ wait()를 if로 사용**

```java
synchronized void wrong() {
    if (queue.isEmpty()) {
        wait();  // ❌ 깨어나면 바로 계속 (조건 미확인)
    }
    String item = queue.poll();  // 여기서 NPE 발생 가능
}

synchronized void correct() {
    while (queue.isEmpty()) {
        wait();  // ✅ 깨어난 후 다시 조건 확인
    }
    String item = queue.poll();  // 안전
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ sleep() 중 리소스 정리 미흡**

```java
// ❌ sleep 중 락 점유로 다른 스레드 블로킹
synchronized void slowOperation() {
    Thread.sleep(5000);  // 5초 동안 락 점유
}

// ✅ 락 범위 최소화
void betterOperation() {
    synchronized(this) {
        // 필요한 작업만
    }
    Thread.sleep(5000);  // 락 해제 후 대기
    synchronized(this) {
        // 결과 처리
    }
}
```

</div>

<div class="tip-box" markdown="1">

**💡 스레드 안전한 대기 패턴**

```java
synchronized void waitForCondition() {
    while (!conditionMet) {  // 항상 while 사용
        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // 인터럽트 상태 복원
            break;
        }
    }
}

synchronized void signalCondition() {
    conditionMet = true;
    notifyAll();  // 모든 대기 스레드 깨우기
}
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **NEW** | 생성됨, 아직 미시작 |
| **RUNNABLE** | 실행 가능 |
| **BLOCKED** | 동기화 락 대기 |
| **WAITING** | 신호 무기한 대기 |
| **TIMED_WAITING** | 신호 또는 타임아웃 대기 |
| **TERMINATED** | 실행 완료 |
| **join()** | 스레드 완료 대기 |
| **sleep()** | 시간 대기 |
| **wait()/notify()** | 조건 동기화 |

---

**관련 파일:**
- [Thread 기본개념](기본개념.md) — 멀티스레드의 이해
- [synchronized](synchronized.md) — 동기화 메커니즘
- [Thread1](Thread1.md) — 스레드 생성 방법
