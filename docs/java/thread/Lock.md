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
- 