package javacore.thread.bounded;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javacore.util.MyLogger.log;

public class BoundedQueueV6_2 implements BoundedQueue {

    private final BlockingQueue<String> queue;

    public BoundedQueueV6_2(int maxSize) {
        this.queue = new ArrayBlockingQueue<>(maxSize);
    }

    @Override
    public void put(String data) {
        boolean result = queue.offer(data);
        log("저장 시도 결과: " + result);
    }

    @Override
    public String take() {
        return queue.poll();
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}
