package javacore.thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static javacore.util.MyLogger.log;

public class BoundedQueueV5 implements BoundedQueue {

    private final Lock lock = new ReentrantLock();
    private final Condition producerCond = lock.newCondition();
    private final Condition consumerCond = lock.newCondition();

    private final Queue<String> queue = new ArrayDeque<>();
    private final int MAX;

    public BoundedQueueV5(int max) {
        this.MAX = max;
    }

    @Override
    public void put(String data) {
        lock.lock();
        try {
            while (queue.size() == MAX) {
                log("[put] 큐가 가득 참, 생산자 대기");
                try {
                    producerCond.await();
                    log("[put] 생산자 깨어남");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.offer(data);
            log("[put] 생산자 데이터 저장, 소비자 스레드 호출");
            consumerCond.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                log("[take] 큐에 데이터가 없음, 소비자 대기");
                try {
                    consumerCond.await();
                    log("[take] 소비자 깨어남");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String data =  queue.poll();
            log("[take] 소비자 데이터 획득, 생산자 스레드 호출");
            producerCond.signal();
            return data;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}
