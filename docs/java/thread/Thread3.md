## 스레드 제어 고급 (Interrupt & Yield)

### 왜 쓰는지

스레드를 **시작한 후에 제어해야 하는 상황**이 있습니다:
- 장시간 작업 중인 스레드를 중단하고 싶음
- CPU 자원을 다른 스레드에 양보하고 싶음
- 사용자가 취소 버튼 클릭 시 즉시 중단

`interrupt()`와 `yield()`로 이런 제어를 할 수 있습니다.

<div class="concept-box" markdown="1">

**핵심**: `interrupt()`는 **스레드에 중단 요청을 보내는 협력적 메커니즘**이고, `yield()`는 **CPU를 다른 스레드에 양보하는 힌트**입니다.

</div>

### 어떻게 쓰는지

#### interrupt(): 스레드 중단 요청

```java
public class InterruptExample {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                System.out.println("작업 시작");
                for (int i = 0; i < 10; i++) {
                    System.out.println("진행 중: " + i);
                    Thread.sleep(1000);  // 1초 대기
                }
                System.out.println("작업 완료");
            } catch (InterruptedException e) {
                System.out.println("작업이 중단되었습니다");
                Thread.currentThread().interrupt();  // 인터럽트 상태 복원
            }
        });
        
        worker.start();
        
        // 3초 후 중단 요청
        Thread.sleep(3000);
        System.out.println("메인: 작업 중단 요청");
        worker.interrupt();  // InterruptedException 발생
        
        worker.join();
    }
}

// 출력:
// 작업 시작
// 진행 중: 0
// 진행 중: 1
// 메인: 작업 중단 요청
// 작업이 중단되었습니다
```

#### 인터럽트 플래그 확인

```java
public class InterruptFlagExample {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("작업 중...");
            
            // 방법 1: Thread.interrupted() (플래그 소비)
            while (!Thread.interrupted()) {  // 플래그 확인 & 초기화
                System.out.println("계속 진행");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // 예외 발생 시 플래그 초기화되므로 복원 필요
                    Thread.currentThread().interrupt();
                }
            }
            
            System.out.println("중단됨");
        });
        
        worker.start();
        Thread.sleep(500);
        worker.interrupt();
        worker.join();
    }
}
```

#### yield(): CPU 양보

```java
public class YieldExample {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("T1: " + i);
                if (i % 10 == 0) {
                    Thread.yield();  // CPU 양보 (권고)
                }
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("T2: " + i);
                if (i % 10 == 0) {
                    Thread.yield();  // CPU 양보 (권고)
                }
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {}
    }
}
```

#### 스레드 중단 패턴 (Graceful Shutdown)

```java
public class TaskRunner {
    private volatile boolean running = true;  // 중단 신호
    
    public void start() {
        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // 인터럽트 요청 처리
                    System.out.println("작업 중단됨");
                    break;
                }
            }
            
            cleanup();  // 리소스 정리
        });
        
        worker.start();
        return worker;
    }
    
    public void stop() {
        running = false;  // 플래그로 신호
    }
    
    private void doWork() {
        System.out.println("작업 중...");
    }
    
    private void cleanup() {
        System.out.println("리소스 정리 중...");
    }
}

// 사용
public static void main(String[] args) throws InterruptedException {
    TaskRunner runner = new TaskRunner();
    Thread t = runner.start();
    
    Thread.sleep(3000);
    runner.stop();  // 중단 신호
    t.join();  // 종료 대기
}
```

### 언제 쓰는지

| 상황 | 선택 | 이유 |
|------|------|------|
| **긴 작업 중단** | ✅ interrupt() | 협력적 중단 |
| **sleep 중인 스레드 중단** | ✅ interrupt() | InterruptedException 발생 |
| **CPU 양보** | ✅ yield() | 공정한 스케줄링 |
| **취소/종료 요청** | ✅ volatile 플래그 + interrupt() | 안전한 중단 |

### 장점

| 장점 | 설명 |
|------|------|
| **협력적 중단** | 스레드가 스스로 정리 후 종료 |
| **안전성** | 강제 종료(stop())보다 안전 |
| **리소스 정리** | try-finally로 정리 코드 실행 가능 |
| **응답성** | UI 스레드 차단하지 않음 |

### 단점

| 단점 | 설명 |
|------|------|
| **복잡성** | interrupt() 처리 로직 필요 |
| **보증 없음** | yield()는 OS가 무시할 수 있음 |
| **플래그 초기화** | InterruptedException 발생 시 플래그 초기화됨 |
| **즉시성** | 협력적이므로 즉시 중단되지 않을 수 있음 |

### 특징

#### 1. interrupt()와 InterruptedException

```java
// sleep() 중인 스레드를 interrupt() 하면?
Thread t = new Thread(() -> {
    try {
        System.out.println("대기 중...");
        Thread.sleep(5000);  // 5초 대기
        System.out.println("대기 완료");
    } catch (InterruptedException e) {
        System.out.println("대기 중단됨");
    }
});

t.start();
Thread.sleep(1000);
t.interrupt();  // 즉시 InterruptedException 발생

// 출력:
// 대기 중...
// 대기 중단됨
```

#### 2. Thread.interrupted() vs isInterrupted()

```java
// Thread.interrupted() (정적)
// - 현재 스레드의 인터럽트 플래그 확인
// - 플래그를 초기화 (소비)
if (Thread.interrupted()) {
    System.out.println("중단 요청받음");
}
// 플래그가 false로 재설정됨

// isInterrupted() (인스턴스)
// - 대상 스레드의 플래그 확인
// - 플래그를 초기화하지 않음
Thread t = ...;
if (t.isInterrupted()) {
    System.out.println("스레드 t 중단됨");
}
// 플래그는 유지됨
```

#### 3. yield()와 스케줄링

```java
// yield()는 JVM과 OS 스케줄러에 보내는 힌트
public class CPUIntensive {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1_000_000_000; i++) {
                // 무거운 계산
                if (i % 100_000_000 == 0) {
                    Thread.yield();  // "다른 스레드도 실행해줄래?" (권고)
                }
            }
        });
        
        t1.start();  // t1 실행 중
        
        Thread t2 = new Thread(() -> {
            System.out.println("T2도 실행됩니다!");  // yield() 덕분에 기회 얻음
        });
        
        t2.start();
    }
}
```

#### 4. volatile 플래그 vs interrupt()

```java
// 방법 1: volatile 플래그
volatile boolean stop = false;

// 중단 신호
Thread.sleep(1000);
stop = true;

// 스레드에서 확인
while (!stop) {
    doWork();
}

// 방법 2: interrupt() + InterruptedException
// 중단 신호
Thread.sleep(1000);
thread.interrupt();

// 스레드에서 처리
try {
    while (true) {
        doWork();
        Thread.sleep(100);  // 여기서 interrupt 감지
    }
} catch (InterruptedException e) {
    // 중단 처리
}

// 차이:
// - volatile 플래그: 주기적 확인 필요
// - interrupt(): sleep/wait 중에 즉시 반응
```

### 주의할 점

<div class="danger-box" markdown="1">

**❌ Thread.stop() 사용 금지**

```java
// ❌ 절대 금지: 스레드 강제 종료
thread.stop();  // @Deprecated

// 문제:
// 1. 리소스 정리 안됨
// 2. 데이터 불일치 발생 가능
// 3. 데드락 위험
```

**✅ 올바른 방식:**
```java
// interrupt() 또는 플래그로 협력적 중단
thread.interrupt();
```

</div>

<div class="warning-box" markdown="1">

**⚠️ InterruptedException 무시하지 않기**

```java
// ❌ 나쁜 예: 예외 무시
while (!Thread.interrupted()) {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        // 무시!
    }
}

// ✅ 올바른 방식: 플래그 복원 및 종료
while (!Thread.interrupted()) {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  // 플래그 복원
        break;  // 루프 종료
    }
}
```

</div>

<div class="warning-box" markdown="1">

**⚠️ yield()에만 의존하지 않기**

```java
// ❌ yield()는 보증이 없음
for (int i = 0; i < 100; i++) {
    Thread.yield();  // OS가 무시할 수 있음
}

// ✅ 진정한 양보가 필요하면 sleep()
Thread.sleep(1);  // 1ms 양보 (더 신뢰성 있음)
```

</div>

### 정리

| 항목 | 설명 |
|------|------|
| **interrupt()** | 스레드에 중단 요청 (협력적) |
| **InterruptedException** | 중단 요청 받은 신호 |
| **Thread.interrupted()** | 현재 스레드 플래그 확인 & 소비 |
| **isInterrupted()** | 스레드 플래그 확인만 |
| **yield()** | CPU를 다른 스레드에 양보 (권고) |
| **volatile 플래그** | 중단 신호 전달 |
| **주의** | stop() 금지, 협력적 종료 |

---

**관련 파일:**
- [Thread2](Thread2.md) — 스레드 상태 관리
- [synchronized](synchronized.md) — 동기화 메커니즘
