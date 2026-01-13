## 스레드를 직접 사용할 때 단점
1. 생산 비용
   - 스레드를 생성하면 운영체제 차원에서 메모리와 커널 자원을 사용합니다.
   - 특히, 자바의 경우 스레드 하나가 스택 메모리(기본 1MB 정도)를 가지고, 운영체제의 스레드 제어 블록(TCB, Thread Control Block)도 필요합니다.
   - 즉, 스레드를 많이 생성하면 메모리 사용량과 CPU 관리 비용이 커지고, GC 부담까지 늘어납니다.
   - 결과적으로 짧은 시간만 실행하고 버리는 스레드가 많으면 성능 저하가 발생합니다.
2. 스레드 관리
   - 스레드를 직접 생성하고 관리하면 실행 순서, 동기화, 종료, 예외 처리까지 전부 신경써야 합니다.
   - 예시
     - 언제 시작/종료할지
     - 여러 스레드 간 경쟁 상태(race condition) 방지
     - 예외 발생 시 스레드 재시작
   - 이런 관리가 코드 복잡도를 폭발적으로 늘립니다.
3. **Runnable** 인터페이스의 불편함
   - Runnable 인터페이스는 단순히 run() 메서드 하나만 제공합니다.
     - 리턴값이 없음 → 작업 결과를 받을 수 없음
     - 예외 처리 어려움 → run() 내에서 예외를 직접 처리해야 함
     - 작업 취소, 상태 조회 불가 → 스레드 제어 기능이 별도로 필요

## Thread Pool
- 미리 일정 개수의 스레드를 생성해두고, 작업이 들어오면 이미 만들어진 스레드에게 일을 맡기는 구조입니다.
  - 즉, 스레드를 재사용해서 새로운 스레드를 매번 생성/삭제하는 비용을 줄이는 것.

## Executor Framework
- 스레드 생성·관리·종료를 애플리케이션 코드에서 분리하여 작업 단위(Task) 중심으로 동시성을 다루기 위한 표준 `API`다.

### 핵심 개념 분리
- Task 제출
  - Runnable, Callable
- Task 실행
  - Executor, ExecutorService
- Thread 관리
  - ThreadPoolExecutor

### ThreadPoolExecutor
- `Executor Framework`의 실질적인 구현체이며, 스레드 풀 기반으로 작업을 실행한다.

#### 예시 코드
```java
public class Main {

    public static void main(String[] args) {
        ExecutorService es = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        log("== 초기 상태 ==");
        printState(es);
        es.execute(new RunnableTask("taskA"));
        es.execute(new RunnableTask("taskB"));
        es.execute(new RunnableTask("taskC"));
        es.execute(new RunnableTask("taskD"));
        log("== 작업 수행중 ==");
        printState(es);

        sleep(3_000);
        log("== 작업 수행 완료 ==");
        printState(es);

        es.shutdown();
        log("== shutdown 완료 ==");
        printState(es);
    }

    private static void printState(ExecutorService executorService) {
        if (executorService instanceof ThreadPoolExecutor poolExecutor) {
            int pool = poolExecutor.getPoolSize();                      // 스레드 풀에서 관리하는 스레드의 숫자
            int active = poolExecutor.getActiveCount();                 // 작업을 수행하는 스레드의 숫자
            int queuedTasks = poolExecutor.getQueue().size();           // 큐에 대기중인 작업의 숫자
            long completedTask = poolExecutor.getCompletedTaskCount();  // 완료된 작업의 숫자
            log("[pool = " + pool + ", active = " + active + ", queuedTasks = " + queuedTasks + ", completedTask = " + completedTask + "]");
        } else {
            log(executorService);
        }
    }

    private static class RunnableTask implements Runnable {
        
        private int sleepMs = 1_000;

        @Override
        public void run() {
            log(name + " 시작");
            sleep(sleepMs);
            log(name + " 완료");
        }

    }
    
}
```

#### ThreadPoolExecutor 내부
> ThreadPoolExecutor는 단순한 큐 실행기가 아니라  
> **스레드 수 → 큐 → 최대 스레드 → 거절 정책** 순으로 판단한다
```markdown
[Task 제출]
    ↓
(corePoolSize 미만?)
    ├─ YES → Thread Pool (Worker 생성) → Task 실행
    └─ NO
        ↓
    BlockingQueue (작업 대기)
        ↓
(Queue full?)
    ├─ NO → Worker가 가져가 실행
    └─ YES
        ↓
(maxPoolSize 미만?)
    ├─ YES → Worker 생성 → Task 실행
    └─ NO → Reject
```
#### ThreadPoolExecutor 생성자 매개변수
| 매개변수 | 역할 |
|----------|------|
| corePoolSize | 즉시 실행 가능한 최소 스레드 수 |
| maximumPoolSize | 생성 가능한 최대 스레드 수 |
| keepAliveTime | core 초과 스레드 유지 시간 |
| TimeUnit | keepAliveTime 시간 단위 |
| BlockingQueue | 실행 대기 중인 Task 저장 |
| ThreadFactory | 스레드 생성 전략 |
| RejectedExecutionHandler | Task 수용 불가 시 처리 전략 |

### Runnable VS Callable

#### 공통점
- 실행 가능한 작업 단위(Task)
- `Thread / Executor`에 의해 실행됨

#### Runnable
- 반환값 없음
- 예외 처리 불가 (Checked Exception X)
- 단순한 비동기 작업에 적합
```java
public interface Runnable {
    void run();
}
```

#### Callable
- 반환값 존재
- Checked Exception 허용
- 결과가 필요한 비동기 작업에 적합
```java
public interface Callable<V> {
    V call() throws Exception;
}
```

### Future
- 비동기 작업의 미래 결과를 표현하는 핸들 객체
- 작업은 이미 다른 스레드에서 실행 중이거나 실행 예정
- 호출 스레드는 결과를 즉시 받지 않고, 필요 시점에 조회

#### Future 내부
```markdown
Future
 ├─ Task (Callable / Runnable)
 ├─ Result (T 또는 Exception)
 └─ State
     ├─ NEW
     ├─ RUNNING
     ├─ COMPLETED
     ├─ CANCELLED
```

#### Future 완료 VS 미완료
- 완료 상태
  - 작업이 정상 종료되었거나 예외로 종료됨
  - 결과를 안전하게 조회 가능
  - 동작 특징
    - get() 호출 시:
      - 즉시 반환
      - 호출 스레드는 RUNNABLE 유지
- 미완료 상태
  - 작업이 아직 종료되지 않음
  - 결과를 즉시 꺼낼 수 없음
  - 동작 특징
    - 호출 스레드는 WAITING / TIMED_WAITING
    - CPU를 점유하지 않고 대기

#### Method
- submit()
  - 작업을 스레드 풀에 제출
  - 즉시 반환
  - 반환 값은 결과 그 자체가 아닌 Future
```markdown
Future<T> future = executorService.submit(callable);
```

#### 예시 코드
```java
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        log("submit() call");
        Future<Integer> future = es.submit(new MyCallable());
        log("future now return, future = " + future);
        log("future.get() [BLOCKING] Method call start -> Main Thread WAITING");
        Integer result = future.get();
        log("future.get() [BLOCKING] Method call end -> Main Thread RUNNABLE");
        log("result value = " + result);
        log("future end, future = " + future);
        es.shutdown();
    }

    private static class MyCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            log("Callable Start");
            sleep(2_000);
            int value = new Random().nextInt(10);
            log("create value = " + value);
            log("Callable End");
            return value;
        }
    }

}
```

### Blocking Method
- 호출 스레드가 다른 스레드의 작업 완료를 기다리는 메서드
  - 대기 중에는 `CPU`를 점유하지 않음

#### 예시 메서드
| 메서드                    | 설명                                   |
|---------------------------|----------------------------------------|
| Thread.join()             | 대상 스레드 종료까지 대기              |
| Future.get()              | 비동기 작업 결과 대기                  |