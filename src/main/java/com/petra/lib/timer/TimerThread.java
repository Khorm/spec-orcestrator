package com.petra.lib.timer;

import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.executor.BlockContextExecutor;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Log4j2
public class TimerThread extends Thread{

    volatile boolean isRunning = true;
    final ContextRepo contextRepo;
    final int TIMER_SECONDS = 5 * 60;
    final String serviceName;
    final TransactionManager transactionManager;
    final BlockContextExecutor blockContextExecutor;

    @Override
    public void run() {
        try {
            while (isRunning) {
                TimeUnit.SECONDS.sleep(TIMER_SECONDS);
                try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
                    List<ContextEntity> notFinishedList = contextRepo.getNotFinishedContexts(serviceName, TIMER_SECONDS, transaction);
                    for (ContextEntity entity : notFinishedList) {
                        boolean isStarted = blockContextExecutor.startContext(entity);
                        if (!isStarted) {
                            log.warn("{} Failed to start", entity.getScenarioId());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error in timer thread", e);
                    throw new RuntimeException(e);
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopTimer(){
        isRunning = false;
        this.interrupt();
    }
}
