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