## Thread
- 스레드는 프로세스 안에서 독립적인 실행 흐름을 가진 실행 단위이다.
- 각 스레드별로 하나의 실행 스택이 생성된다.

### 탄생 이유
- 프로세스는 생성, 전환 비용이 크고 메모리 공유가 어려워, 하나의 가상 주소 공간을 공유하며 더 가볍게 실행 흐름을 나누기 위해 스레드가 탄생했다.

### Thread 실행 방법
```java
public class Main {
    public static void main(String[] args) {
        /**
         * 스레드에 전달하는 작업을 변경하기 쉽다.
         */
        MyRunnable runnable = new MyRunnable();
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();

        /**
         * 스레드에 작업을 변경하기 어렵다.
         */
        MyThread myThread = new MyThread();
        myThread.start();
    }

    /**
     * Runnable -> 실행과 분리된 작업, 
     * 상속 제약이 없고 실행 방식이 독립적
     */
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Hello");
        }
    }

    /**
     * Thread -> 작업을 실제로 실행하는 주체, 
     * 상속에 제약이 있고 작업과 실행 방식이 결합됨
     */
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello");
        }
    }
}
```

### Thread 상속 방식 VS Runnable 구현 방식

#### Thread 상속 방식
- 장점
  - 간단한 구현: Thread 클래스를 상속 받아 run() 메서드만 재정의하면 된다.
- 단점
  - 상속의 제한
  - 유연성 부족

#### Runnable 구현 방식
- 장점
  - 상속의 자유로움
  - 코드의 분리
  - 여러 스레드가 동일한 Runnable 객체를 공유할 수 있어 자원 관리를 효율적으로 할 수 있다.
- 단점
  - 코드가 약간 복잡할 수 있다. Runnable 객체를 생성하고 이를 Thread에 전달하는 과정이 추가된다.

### Thread.run() VS Thread.start()

#### Thread.run()
- 현재 스레드(main)에서 실행
- 멀티 스레드가 아니다.

#### Thread.start()
- JVM에게 새 스레드 요청
- 새로운 실행(스택) 생성
- OS 스케쥴러 등록
- 새 스레드에서 run() 호출

### Thread 종류

#### 사용자 스레드(non-daemon thread)
- 프로그램의 주요 작업을 수행한다.
- 작업이 완료될 때까지 실행된다.
- 모든 사용자 스레드가 종료되면 JVM도 종료된다.

#### 데몬 스레드(daemon thread)
- 백그라운드에서 보조적인 작업을 수행한다.
- 모든 사용자 스레드가 종료되면 데몬 스레도 자동으로 종료된다.
- JVM은 데몬 스레드의 실행 완료를 기다리지 않고 종료된다. 데몬 스레드가 아닌 모든 스레드가 종료되면, 자바 프로그램도 종료된다.
