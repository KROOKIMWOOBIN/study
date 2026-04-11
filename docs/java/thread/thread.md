# 멀티스레드와 동시성

Java 고급 1편 — 스레드 생성, 동기화, 락, 스레드풀 전반 정리.

---

## 왜 멀티스레드가 필요한가?

단일 스레드는 한 번에 하나의 작업만 처리한다. CPU 코어가 여러 개인 환경이나 I/O 대기가 긴 작업에서는 자원이 낭비된다.

| 상황 | 단일 스레드 | 멀티스레드 |
| --- | --- | --- |
| CPU 집약 작업 | 코어 1개만 사용 | 여러 코어 병렬 활용 |
| I/O 대기 (네트워크, 파일) | 대기 동안 CPU 낭비 | 대기 중 다른 작업 처리 |
| UI / 응답성 | 작업 중 화면 멈춤 | 백그라운드 스레드 분리 |

## 특징

- 같은 프로세스 내 스레드는 <span class="text-blue">힙·메서드 영역을 공유</span>한다. → 데이터 공유가 쉽지만 동시 접근 충돌 위험이 있다.
- 각 스레드는 **독립적인 스택**을 가진다. → 지역 변수는 스레드 안전하다.
- 프로세스보다 생성·전환 비용이 낮다.

## 장점 / 단점

| 구분 | 내용 |
| --- | --- |
| 장점 | CPU 활용률 향상, I/O 대기 중 다른 작업 처리, 응답성 개선 |
| 단점 | 공유 자원 동기화 필요, 데드락·레이스 컨디션 위험, 디버깅 어려움 |

## 주의할 점

- 스레드 수를 무작정 늘리면 컨텍스트 스위칭 오버헤드로 오히려 성능이 떨어진다.
- 공유 객체에 접근할 때는 반드시 동기화(`synchronized`, `Lock`, 원자적 연산 등)를 고려해야 한다.
- 스레드를 직접 생성하기보다 <span class="text-green">스레드풀(Executor)</span>을 사용하는 것이 일반적이다.

---

## 학습 목록

| 주제 | 한 줄 설명 |
| --- | --- |
| [기본 개념](./기본개념.md) | 멀티태스킹, 프로세스, 스레드, 스케줄링, 컨텍스트 스위칭 |
| [Thread 생성과 실행](./Thread1.md) | Thread 클래스 사용법 |
| [스레드 제어와 생명주기](./Thread2.md) | Thread State, sleep, join, interrupt |
| [interrupt와 yield](./Thread3.md) | 인터럽트 처리, yield로 CPU 양보 |
| [volatile](./volatile.md) | 메모리 가시성, JMM, happens-before |
| [synchronized](./synchronized.md) | 임계 영역, synchronized 블록/메서드 |
| [고급 동기화 (Lock)](./Lock.md) | LockSupport, ReentrantLock |
| [생산자-소비자 문제](./생산자소비자문제.md) | wait/notify, Condition, BlockingQueue |
| [동기화와 원자적 연산](./동기화와원자적연산.md) | 원자적 연산, CAS, SpinLock |
| [동시성 컬렉션](./동시성컬렉션.md) | Thread-Safe 컬렉션, Proxy, BlockingQueue |
| [스레드풀 / Executor 1](./ThreadPool_Executor_Overview1.md) | ThreadPoolExecutor, Runnable vs Callable, Future |
| [스레드풀 / Executor 2](./ThreadPool_Executor_Overview2.md) | 우아한 종료, 스레드풀 관리, Executor 전략, 예외 정책 |
