## Thread State

### NEW
- 스레드가 생성되고 아직 시작되지 않은 상태이다.

### RUNNABLE
- 스레드가 실행될 준비가 된 상태이다.

### BLOCKED
- 스레드가 다른 스레드에 의해 동기화 락을 얻기 위해 기다리는 상태이다.

### WAITING
- 스레드가 다른 스레드의 특정 작업이 완료되기를 무기한 기다리는 상태이다.

### TIMED WAITING
- 스레드가 특정 시간동안 다른 스레드의 작업이 완료되기를 기다리는 상태이다.

### TERMINATED
- 스레드의 실행이 완료된 상태이다.

## Thread Control

### join()
- 다른 스레드의 작업 완료를 기다리기 위해 사용
  - 현재 실행 중인 스레드가 대상 스레드가 종료될 때까지 대기하도록 만드는 메서드다.

#### 실행 절차
```markdown
1. join()을 호출한 현재 스레드가 BLOCKED 상태로 전환됨
2. 대상 thread가 TERMINATED 상태가 되면
3. 현재 스레드가 다시 RUNNABLE 상태로 복귀
```
```java
Thread worker = new Thread(() -> {
    // 작업 수행
});

worker.start();
worker.join(); // worker가 끝날 때까지 대기

// worker 작업 완료 이후 실행
```

### sleep()
- 현재 스레드를 일정 시간 동안 멈추기 위해 사용
  - 자기 자신을 강제로 대기 상태로 만드는 메서드
  - CPU를 점유하지 않음

### 실행 절차
```markdown
1. 호출한 현재 스레드가 TIMED_WAITING 상태로 전환
2. 지정한 시간이 지나면 자동으로 RUNNABLE 상태로 복귀
```
```java
Thread.sleep(1000);
```


### 코드 예시
```java
public class Main {
    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();
        Thread thread = new Thread(runnable, "Thread-1"); // NEW
        thread.start(); // RUNNABLE
        thread.join(); // WAITING => Thread-1이 끝날때까지 대기
        // thread.join(1_000); // TIMED WAITING => Thread-1이 끝날때까지 1초 기다린 후 풀림
        System.out.println("종료"); // TERMINATED
    }
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    System.out.println("TEST: " + i);
                    sleep(1_000); // TIMED WAITING
                } catch (InterruptedException e) {
                    System.out.println("아직 비밀~");
                }
            }
        }
    }
}
```
