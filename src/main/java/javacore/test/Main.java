package javacore.test;

import java.util.ArrayDeque;
import java.util.Queue;

import static javacore.thread.util.MyLogger.log;

public class Main {

    private final Queue<String> queue = new ArrayDeque<>(); // 버퍼
    private final int MAX; // 버퍼 최대 크기

    public Main(int max) {
        this.MAX = max;
    }

    public synchronized void put(String data) { // 생산자
        while (queue.size() == MAX) { // 버퍼 가득참
            try {
                wait(); // RUNNABLE -> WAITING, 락 반납
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.offer(data); // 락 획득 후 데이터 인입
        notify(); // 대기 스레드, WAITING -> BLOCKED -> RUNNABLE
    }

    public synchronized String take() { // 소비자
        while (queue.isEmpty()) { // 버퍼 비었음
            try {
                wait(); // RUNNABLE -> WAITING, 락 반납
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        String data =  queue.poll(); // 락 획득 후 데이터 꺼냄
        notify(); // 대기 스레드, WAITING -> BLOCKED -> RUNNABLE
        return data;
    }

}