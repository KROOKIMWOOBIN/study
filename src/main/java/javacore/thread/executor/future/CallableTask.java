package javacore.thread.executor.future;

import java.util.concurrent.Callable;

import static javacore.thread.util.MyLogger.log;

public class CallableTask implements Callable<Integer> {

    private final String name;
    private int sleepMs = 1_000;

    public CallableTask(String name) {
        this.name = name;
    }

    public CallableTask(String name, int sleepMs) {
        this.name = name;
        this.sleepMs = sleepMs;
    }

    @Override
    public Integer call() throws Exception {
        log(name + " 실행");
        log(name + " 완료");
        return sleepMs;
    }

}
