package javacore.thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;

import static javacore.util.MyLogger.log;

public class BoundedQueueV3 implements BoundedQueue {

    private final Queue<String> queue = new ArrayDeque<>();
    private final int MAX;

    public BoundedQueueV3(int max) {
        this.MAX = max;
    }

    @Override
    public synchronized void put(String data) {
        while (queue.size() == MAX) {
            log("[put] 큐가 가득 참, 생산자 대기");
            try {
                wait(); // RUNNABLE -> WAITING, 락 반납
                log("[put] 생산자 깨어남");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.offer(data);
        notify(); // 대기 스레드, WAITING -> BLOCKED
    }

    @Override
    public synchronized String take() {
        while (queue.isEmpty()) {
            try {
                wait(); // RUNNABLE -> WAITING, 락 반납
                log("[take] 소비자 깨어남");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        String data =  queue.poll();
        log("[take] 소비자 데이터 획득, notify() 호출");
        notify(); // 대기 스레드, WAITING -> BLOCKED
        return data;
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}
