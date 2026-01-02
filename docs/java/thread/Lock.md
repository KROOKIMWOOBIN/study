## LockSupport
1. 스레드를 잠재우고(park) 깨우는(unpark) 저수준 도구 
2. CPU를 점유하지 않고 스레드를 대기시킨다.
3. unpark()는 park()보다 먼저 호출돼도 유효하다.
4. 락(synchronized)이 필요 없다.
5. interrupt가 발생하면 park는 즉시 해제되며, 예외 없이 인터럽트 상태는 유지된다.

```markdown
LockSupport.park();                 // 현재 스레드 대기
LockSupport.parkNanos(nanos);       // 지정 시간 동안 대기
LockSupport.unpark(thread);         // 특정 스레드 깨움
```

## ReentrantLock

### 1. Reentrancy (재진입성)
- 동일 스레드가 이미 획득한 락을 다시 획득할 수 있는 성질
- 재진입 락은 동일 스레드가 획득할 때마다 내부 카운트를 증가시키고, unlock 시 카운트를 0으로 만들 때 실제 락을 해제하는 방식으로 가능하다.

#### 왜? 사용하는가?
- 재진입 락은 동일 스레드가 이미 가진 락을 다시 요청할 때 자기 자신을 기다리는 무한 대기를 방지하기 위해 허용된다.

#### 예시
```java
public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(new MyLock(), "myLock");
        thread.start();
    }
    static class MyLock implements Runnable {
        @Override
        public void run() {
            ReentrantLock lock = new ReentrantLock();
            lock.lock(); // +1 lock = 1
            try {
                System.out.println("첫 번째 획득");
                lock.lock(); // 재진입을 허용하지 않으면 여기서 무한 대기가 걸린다. +1 lock = 2
                try {
                    System.out.println("두 번째 획득");
                } finally {
                    lock.unlock(); // lock = 1
                }
            } finally {
                lock.unlock(); // lock = 0
            }
        }
    }
}
```

### 2. Fairness(공정성)
- 공정 모드
  - 락 요청 순서대로 스레드에게 락 배분 (FIFO)
  - 성능 저하 있음
- 비공정 모드
  - 락 요청 순서 무시, 먼저 대기중인 스레드라도 운 좋으면 락 획득 가능
  - 먼저 요청한 스레드라도 뒤로 밀릴 수 있음, 성능 조금 더 높음
```java
ReentrantLock fairLock = new ReentrantLock(true); // 공정 모드
ReentrantLock unfairLock = new ReentrantLock();   // 비공정 모드 (default)
```
### 3. monitor Lock VS Lock
|구분|synchronized| ReentrantLock          |
|--|--|------------------------|
|락 획득|암묵적| 명시적 lock()             |
|공정성|없음| 선택 가능                  |
|타임아웃|불가| 가능 tryLock(time, unit) |
|해제|블록 종료 시 자동| unlock() 명시적 해제 필요     |
### 4. lock() VS lockInterruptibly()
|메서드|대기방식|인터럽트 처리|
|--|--|--|
|lock()|락 획득될 때까지 무조건 대기|인터럽트 무시|
|lockInterruptibly()|락 획득될 때까지 대기 가능|대기 중 인터럽트 발생 시 InterruptedException 발생, 즉시 대기 종료|

### 5. tryLock() VS tryLock(timeout, unit)

#### tryLock()
- 락이 즉시 가능하면 획득, 아니면 실패

#### tryLock(timeout, unit)
- 지정한 시간 동안 락 획득 시도, 실패 시 false 반환

### 6. unlock() 사용 시 주의사항
- 반드시 try-finally 구조로 사용
- unlock 누락 시 데드락 발생