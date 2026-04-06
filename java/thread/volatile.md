## 메모리 가시성
- 한 스레드가 변경한 값이 다른 스레드에서 언제, 어떻게 보이느냐의 문제

### 메모리 구조
```markdown
- CPU CORE
    - L1 Cache
    - L2 Cache
    - L3 Cache
- Main Memory (RAM)
```

### 메모리 가시성 왜 발생하냐?
- 각 CPU 코어는 메인 메모리가 아니라 자기 캐시 메모리에 있는 값을 기준으로 실행한다.
- 이 캐시는 성능을 위해 사용되며, 캐시 간 동기화는 즉시 강제되지 않는다.

#### 왜 이렇게 설계 되었는가?
- 메인 메모리는 너무 느리다.
  - RAM 접근: 수백 CPU 사이클

## volatile
- volatile은 메모리 가시성(visibility)을 보장하기 위한 키워드다.
  - 한 스레드가 값을 변경하면 다른 스레드가 반드시 최신 값을 보게 만든다.
- volatile 변수에 대한 쓰기(write)는 즉시 메인 메모리에 반영되고, 읽기(read)는 CPU 캐시가 아닌 메인 메모리 기준으로 수행되도록 보장된다.
```java
volatile boolean running = true;
```

## JMM(Java Memory Model)
- 자바에서 스레드 간 메모리 접근이 어떻게 보이고, 어떤 순서로 보장되는지를 정의한 공식 규칙

### JMM 정의
- 가시성 (Visibility)
  - 한 스레드의 쓰기 결과가 다른 스레드에서 언제 보이는가?
    - CPU 캐시 문제
    - 해결 수단: volatile, synchronized, join(), start()
- 원자성 (Atomicity)
  - 연산이 중간에 끊기지 않고 하나의 단위로 실행되는가?
    - int, reference의 단일 read/write는 원자적
    - 복합 연산 (i++, check-then-act) 은 원자적이지 않음
    - 해결 수단: synchronized
- 순서 보장 (Ordering)
  - 코드 작성 순서대로 실행되는 것처럼 보이는가?
    - 컴파일러 / JIT / CPU는 성능을 위해 재정렬 가능
    - 해결 수단: volatile, synchronized, happens-before 규칙
    

## happens-before
- A happens-before B
  - A의 모든 결과는 B에서 반드시 보인다.

### 예시1
```java
int a = 1;
int b = 2;
```
- a = 1  happens-before  b = 2

### 예시2
```java
int a = 0;
int b = 0;

Thread t1 = new Thread(() -> {
    a = 1;
    b = 2;
});

Thread t2 = new Thread(() -> {
    System.out.print(b + ", " + a);
});
```
- 가능한 출력
```markdown
2, 0
```
- 왜? 이렇게 나오냐?
  - t1과 t2 사이에는 happens-before 관계가 없으므로 따라서 스레드 간 실행 순서 및 가시성 보장이 되지 않는다.
  - JMM은 실행 순서 자체를 보장하지 않는다.
- 해결 방안 코드
```java
volatile int b = 0;
int a = 0;

Thread t1 = new Thread(() -> {
    a = 1;   // (1)
    b = 2;   // (2) volatile write
});

Thread t2 = new Thread(() -> {
    if (b == 2) {          // (3) volatile read
        System.out.println(a); // 반드시 1
    }
});
```