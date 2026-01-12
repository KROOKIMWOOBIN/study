package javacore.thread.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static javacore.thread.executor.ExecutorUtil.printState;
import static javacore.thread.util.MyLogger.log;
import static javacore.thread.util.ThreadUtils.sleep;

public class ExecutorBasicMain {

    public static void main(String[] args) {
        ExecutorService es = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        log("== 초기 상태 ==");
        printState(es);
        es.execute(new RunnableTask("taskA"));
        es.execute(new RunnableTask("taskB"));
        es.execute(new RunnableTask("taskC"));
        es.execute(new RunnableTask("taskD"));
        log("== 작업 수행중 ==");
        printState(es);

        sleep(3_000);
        log("== 작업 수행 완료 ==");
        printState(es);

        es.shutdown();
        log("== shutdown 완료 ==");
        printState(es);
    }

}
