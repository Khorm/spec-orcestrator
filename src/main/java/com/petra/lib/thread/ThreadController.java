package com.petra.lib.thread;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Log4j2
public class ThreadController {

    private final ExecutorService unlimitedExecutor = Executors.newCachedThreadPool();
    private final ExecutorService limitedExecutor ;

    public ThreadController(int threadPoolSize) {
        this.limitedExecutor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void executeLimitedPoolTask(Runnable runnable){
        try {
            limitedExecutor.execute(runnable);
        }catch (Throwable e){
            log.error("Execute task error",e);
        }
    }

    public void executeRequestPoolTask(Runnable runnable){
        unlimitedExecutor.execute(runnable);
    }

    public int getActiveThreadCount(){
        return ((ThreadPoolExecutor)unlimitedExecutor).getActiveCount() +
                ((ThreadPoolExecutor)limitedExecutor).getActiveCount();
    }
}
