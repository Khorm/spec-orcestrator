package com.petra.lib.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadController {

    private final ExecutorService unlimitedExecutor = Executors.newCachedThreadPool();
    private final ExecutorService limitedExecutor ;

    public ThreadController(int threadPoolSize) {
        this.limitedExecutor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void executeLimitedPoolTask(Runnable runnable){
        limitedExecutor.execute(runnable);
    }

    public void executeUnlimitedPoolTask(Runnable runnable){
        unlimitedExecutor.execute(runnable);
    }
}
