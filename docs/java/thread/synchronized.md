## 임계 영역
- 여러 스레드가 공유 자원(shared resource) 에 동시에 접근할 경우 데이터 불일치 또는 예측 불가능한 결과가 발생할 수 있는 코드 영역

### 경쟁 상태를 유발하는 코드 예시
- 아래 로직을 여러 스레드가 실행하면 [경쟁 상태]가 발생하며, 값 손실이 발생한다.
#### 코드
```java
public class RaceConditionExample {

    static int count = 0;

    static void increment() {
        int temp = count;        // 1. 읽기
        sleep(10);               // 컨텍스트 스위칭 유도
        count = temp + 1;        // 2. 계산 및 쓰기
    }

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(new IncrementTask());
        Thread t2 = new Thread(new IncrementTask());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("최종 count = " + count);
    }

    static class IncrementTask implements Runnable {
        @Override
        public void run() {
            increment();
        }
    }
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}
```
#### 실행 흐름
```markdown
1. t1이 카운트를 읽음 -> 0
2. 컨택스트 스위칭 발생
3. t2가 카운트를 읽음 -> 0
4. t1이 count = 1 저장
5. t2가 count = 1 저장 (t1의 결과를 덮어씀)
6. 결과는 [경쟁 상태]로 인해 1
```

#### 값 손실이 발생하는 이유
- count++ 는 하나의 문장처럼 보이지만, 읽기 → 계산 → 쓰기의 복합 연산이며 이 전체가 보호되지 않으면 스레드 간에 끼어들 수 있다.

## synchronized
- 자바에서 임계 영역에 대한 동시 접근을 방지하기 위한 키워드다.
- JVM 수준에서 모니터 락(monitor lock) 사용
- 객체(Object) 또는 클래스(Class)에 락을 건다

### 장점
1. 상호 배제
   - 동시에 못 들어오게 막는 것뿐 아니라 읽기 → 수정 → 쓰기 전체를 하나의 원자적 구간으로 만든다.
2. 가시성
   - 모니터 락을 해제(unlock) 한 스레드의 모든 쓰기 → 같은 락을 획득(lock) 한 다음 스레드에서 반드시 보인다.

### 단점
1. 순서 보장을 하지 않는다. (공정성)
   - 락 획득 순서에 대한 공정성(fairness)을 보장하지 않는다.
   - 먼저 대기한 스레드가 항상 먼저 락을 획득한다는 보장이 없다.
   - 그 결과 기아 상태(starvation)가 발생할 수 있다.
2. 제어 불가 (무한대기)
    - 락을 직접 해제할 수 없다.
    - 인터럽트로 락 대기를 중단할 수 없다.
3. 속도가 느려진다.
   - 모니터 락 획득·해제에 따른 오버헤드가 발생한다.
   - 임계 영역이 길수록 병렬성이 크게 감소한다.

### 인스턴스 메서드 synchronized
- 락 대상
  - this (해당 인스턴스의 monitor)
- 보장 범위
  - 같은 인스턴스를 공유하는 스레드 간 상호 배제
```java
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```
### synchronized 블록
- 락 대상
    - this (해당 인스턴스의 monitor)
- 보장 범위
    - 같은 인스턴스를 공유하는 스레드 간 상호 배제
```java
class Counter {
    private int count = 0;

    public void increment() {
        synchronized (this) {
            count++;
        }
    }

    public int getCount() {
        synchronized (this) {
            return count;
        }
    }
}
```
### static synchronized 메서드
- 락 대상
  - Counter.class (Class 객체의 monitor)
- 보장 범위
  - 모든 인스턴스 + static 상태 간 상호 배제
```java
class GlobalCounter {
    private static int count = 0;
    
    public static synchronized void increment() {
        count++;
    }

    public static synchronized int getCount() {
        return count;
    }
}
```
### 전용락
- 락 대상
  - this도, 다른 인스턴스도 아님
  - 해당 인스턴스가 소유한 별도의 락 객체
- 보장 범위
  - 보장 범위는 “전용 락을 사용하는 synchronized 구간”
```java
class Counter {
    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) {
            count++;
        }
    }

    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}
```