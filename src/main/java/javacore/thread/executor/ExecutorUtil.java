package javacore.thread.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static javacore.thread.util.MyLogger.log;

public abstract class ExecutorUtil {

    public static void printState(ExecutorService executorService) {
        if (executorService instanceof ThreadPoolExecutor poolExecutor) {
            int pool = poolExecutor.getPoolSize();                      // 스레드 풀에서 관리하는 스레드의 숫자
            int active = poolExecutor.getActiveCount();                 // 작업을 수행하는 스레드의 숫자
            int queuedTasks = poolExecutor.getQueue().size();           // 큐에 대기중인 작업의 숫자
            long completedTask = poolExecutor.getCompletedTaskCount();  // 완료된 작업의 숫자
            log("[pool = " + pool + ", active = " + active + ", queuedTasks = " + queuedTasks + "completedTask = " + completedTask + "]");
        } else {
            log(executorService);
        }
    }

}
