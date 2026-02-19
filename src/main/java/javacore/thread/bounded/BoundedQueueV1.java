package javacore.thread.bounded;

import java.util.ArrayDeque;
import java.util.Queue;

import static javacore.util.MyLogger.log;

public class BoundedQueueV1 implements BoundedQueue {

    private final Queue<String> queue = new ArrayDeque<>();
    private final int MAX;

    public BoundedQueueV1(int max) {
        this.MAX = max;
    }

    @Override
    public synchronized void put(String data) {
        if (queue.size() == MAX) {
            log("[put] 큐가 가득참, 버림: " + data);
            return;
        }
        queue.offer(data);
    }

    @Override
    public synchronized String take() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.poll();
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}
