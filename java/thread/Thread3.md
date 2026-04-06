## Interrupt
- 스레드에게 중단 요청을 전달한다.
- JVM에서 인터럽트 오류가 발생하면 인터럽트 플래그를 다시 초기화 해준다. (기본: false)

### InterruptedException
- sleep(), join()등 메서드는 스레드를 멈춰놓는 상태인데, 인터럽트를 만나면 즉시 깨어나도록 위 오류가 발생한다.

### 코드 예시
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new MyRunnable(), "myThread");
        thread.start();
        Thread.sleep(1_000);
        thread.interrupt(); // 1초 뒤 내 스레드에게 중단 요청 전송 인터럽트 상태 true
    }
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("실행 시작");
            while (!Thread.interrupted()) { // 중단 요청을 받은 뒤 다시 인터럽트 상태를 false로 돌려놓음
                System.out.println("실행중");
            }
            System.out.println("실행 완료");
        }
    }
}
```

## Thread.yield()
- 지금 CPU를 쓰고 있지만, 같은 우선순위 스레드가 있다면 먼저 실행해도 좋다라고 스케쥴러에게 힌트를 주는 메서드다.
- 양보를 진행해도 상태 변화 없이 계속 Runnable 상태이다.
- OS 스케줄러가 무시할 수 있다. (권고이기 때문에 꼭 지켜지지는 않는다.)