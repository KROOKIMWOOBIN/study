## ExecutorService 우아한 종료

### shutdown()
- 새 작업은 더 이상 받지 않음
- 이미 제출된 작업은 끝까지 실행
- 호출 즉시 종료되지 않음

### showdownNow()
- 새 작업 거절
- 실행 중인 작업에 interrupt() 시도
- 대기 중인 작업 큐를 반환

### 예시 코드
```java
public class Main {

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.execute(new RunnableTask("taskA"));
        es.execute(new RunnableTask("taskB"));
        es.execute(new RunnableTask("taskC"));
        es.execute(new RunnableTask("longTask", 100_000));
        printState(es);
        log("== shutdown start ==");
        shutdownAndAwaitTermination(es);
        log("== shutdown finish ==");
        printState(es);
    }

    private static void shutdownAndAwaitTermination(ExecutorService es) {
        es.shutdown(); // non-blocking, 새로운 작업을 받지 않는다. 처리 중이거나 큐에 이미 대기중인 작업은 처리한다. 이 후에 풀의 스레드를 종료한다.
        try {
            // 이미 대기중인 작업들을 모두 완료할 때 까지 10초 기다린다.
            if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                log("서비스 정상 종료 실패 -> 강제 종료 시도");
                es.shutdownNow();
                // 작업이 취소할 때 까지 대기한다.
                if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                    log("서비스가 종료되지 않았습니다.");
                }
            }
        } catch (InterruptedException e) {
            // awaitTermination()으로 대기중인 현재 스레드가 인터럽트 될 수 있다.
            es.shutdownNow();
        }
    }

}
```

## Executor Thread Pool 관리
- 스레드를 “필요할 때 늘리고, 필요 없으면 줄인다”

1. corePoolSize
   - 풀의 기본 유지 스레드 수
   - 작업이 없어도 항상 살아 있음
   - 성능의 하한선
```markdown
작업 수 ≤ corePoolSize → 즉시 스레드 실행
```
2. maximumPoolSize
  - 생성 가능한 최대 스레드 수
  - 큐가 가득 찼을 때만 확장
```markdown
corePoolSize 초과 + 큐 가득 → 스레드 증가
```
3. keepAliveTime
   - `corePoolSize`를 초과해 생성된 스레드의 유지 시간
   - 작업 없이 시간 초과 시 스레드 제거
```markdown
확장 스레드 → idle → keepAliveTime 초과 → 제거
```

### 스레드 생성 흐름
```markdown
1. 스레드 수 < corePoolSize
   → 새 스레드 생성

2. 스레드 수 ≥ corePoolSize
   → 큐에 적재

3. 큐가 가득 참
   → `maxPoolSize`까지 스레드 확장

4. maxPoolSize 도달
   → 작업 거절 (RejectedExecutionHandler)
```

## Executor 전략
- 스레드를 언제 생성하고, 얼마나 유지할 것인가에 대한 정책

### 고정 풀 전략
- 스레드 개수가 고정
- 초과 작업은 큐에서 대기
```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

#### 장점
- 스레드 수 예측 가능
- 안정적
- 설정 단순

#### 단점
- 큐가 무한 → 메모리 위험(OOM)
- 작업이 쌓이면 응답 지연 폭증
- 장애 시 폭발적으로 누적

### 캐시 풀 전략
- 필요 시 스레드를 무제한 생성 사용하지 않으면 일정 시간 후 제거

```java
import java.util.concurrent.SynchronousQueue;

ExecutorService executor =
        new ThreadPoolExecutor(
                10,                     // corePoolSize
                20,                     // maxPoolSize
                60,                     // keepAliveTime
                TimeUnit.SECONDS, // 시간
                new SynchronousQueue<>() // 큐가 들어오자마자 쌓이지 않고 바로 바로 실행된다.
        );
```

#### 장점
- 짧은 작업에 빠른 응답
- 유휴 스레드 자동 정리

#### 단점 (치명적)
- 스레드 수 폭증 가능
- DB / 외부 API 호출 시 장애 유발
- 운영 환경에서는 매우 위험

### 사용자 정의 풀 전략
- `ThreadPoolExecutor`를 직접 구성 가장 권장되는 실무 전략

```java
ExecutorService executor =
    new ThreadPoolExecutor(
        10,                     // corePoolSize
        20,                     // maxPoolSize
        60,                     // keepAliveTime
        TimeUnit.SECONDS, // 시간
        new ArrayBlockingQueue<>(100),  // 큐 크기
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
```

#### 장점
1. 스레드 수 통제 가능
2. 큐 크기 제한 가능
3. 거절 정책으로 장애 전파 방지 
4. 시스템 특성에 맞게 튜닝 가능

#### 단점
1. 설정이 복잡
2. 설계 이해 필요

## Executor 예외 정책

### 거절 정책
- `ThreadPoolExecutor`가 새로운 작업을 더 이상 수용할 수 없을 때, 해당 작업을 어떻게 처리할지 정의하는 정책이다.

### 거절이 발생하는 조건
1. 스레드 풀의 모든 스레드가 사용 중
2. 작업 큐(BlockingQueue)가 가득 참
3. `maximumPoolSize`까지 스레드 확장도 불가능

#### AbortPolicy (기본 정책)
- `ThreadPoolExecutor`가 새로운 작업을 더 이상 수용할 수 없을 때, 새로운 작업을 제출할 때 `RejectedExecutionException`을 발생 시킨다.
```java
public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        executor.execute(new MyJob());
        try {
            executor.execute(new MyJob());
        } catch (RejectedExecutionException e) {
            System.out.println("더 이상 작업을 제출할 수 없음");
        }
        System.out.println("종료");
        executor.shutdown();
    }

    private static class MyJob implements Runnable {
        @Override
        public void run() {
            System.out.println("실행");
        }
    }

}
```

#### DiscardPolicy
- `ThreadPoolExecutor`가 새로운 작업을 더 이상 수용할 수 없을 때, 새로운 작업을 조용히 버린다. (실행하지 않고 넘어간다.)
```java
public class Main {

   public static void main(String[] args) {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
              new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
      executor.execute(new MyJob());
      executor.execute(new MyJob()); // 여기서 작업을 더 이상 넣을 수 없으면, 아무런 반응 없이 조용히 버린다.
      System.out.println("종료");
      executor.shutdown();
   }

   private static class MyJob implements Runnable {
      @Override
      public void run() {
         System.out.println("실행");
      }
   }

}
```

#### CallerRunsPolicy
- `ThreadPoolExecutor`가 새로운 작업을 더 이상 수용할 수 없을 때, 새로운 작업을 제출한 스레드가 직접 작업을 실행한다.
```java
public class Main {

   public static void main(String[] args) {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
              new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
      executor.execute(new MyJob());
      executor.execute(new MyJob()); // 여기서 작업을 더 이상 넣을 수 없으면, 호출한 스레드(main)가 대신 실행한다.
      System.out.println("종료");
      executor.shutdown();
   }

   private static class MyJob implements Runnable {
      @Override
      public void run() {
         System.out.println("실행");
      }
   }

}
```