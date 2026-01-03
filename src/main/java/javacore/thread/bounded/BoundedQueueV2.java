package javacore.thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;

import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class BoundedQueueV2 implements BoundedQueue {

    private final Queue<String> queue = new ArrayDeque<>();
    private final int MAX;

    public BoundedQueueV2(int max) {
        this.MAX = max;
    }

    @Override
    public synchronized void put(String data) {
        while (queue.size() == MAX) {
            log("[put] 큐가 가득 참, 생산자 대기");
            sleep(1_000);
        }
        queue.offer(data);
    }

    @Override
    public synchronized String take() {
        while (queue.isEmpty()) {
            log("[take] 큐에 데이터가 없음, 소비자 대기");
            sleep(1_000);
        }
        return queue.poll();
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}
