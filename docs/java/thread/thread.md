> [← 홈](/study/) · [Java](/java/java/)

# 멀티스레드와 동시성

Java 고급 1편 — 스레드 생성, 동기화, 락, 스레드풀 전반 정리.

---

| 주제 | 한 줄 설명 |
| --- | --- |
| [기본 개념](./thread/기본개념.md) | 멀티태스킹, 프로세스, 스레드, 스케줄링, 컨텍스트 스위칭 |
| [Thread 생성과 실행](./thread/Thread1.md) | Thread 클래스 사용법 |
| [스레드 제어와 생명주기](./thread/Thread2.md) | Thread State, sleep, join, interrupt |
| [interrupt와 yield](./thread/Thread3.md) | 인터럽트 처리, yield로 CPU 양보 |
| [volatile](./thread/volatile.md) | 메모리 가시성, JMM, happens-before |
| [synchronized](./thread/synchronized.md) | 임계 영역, synchronized 블록/메서드 |
| [고급 동기화 (Lock)](./thread/Lock.md) | LockSupport, ReentrantLock |
| [생산자-소비자 문제](./thread/생산자소비자문제.md) | wait/notify, Condition, BlockingQueue |
| [동기화와 원자적 연산](./thread/동기화와원자적연산.md) | 원자적 연산, CAS, SpinLock |
| [동시성 컬렉션](./thread/동시성컬렉션.md) | Thread-Safe 컬렉션, Proxy, BlockingQueue |
| [스레드풀 / Executor 1](./thread/ThreadPool_Executor_Overview1.md) | ThreadPoolExecutor, Runnable vs Callable, Future |
| [스레드풀 / Executor 2](./thread/ThreadPool_Executor_Overview2.md) | 우아한 종료, 스레드풀 관리, Executor 전략, 예외 정책 |
