package com.petra.lib.timer;

import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.operation.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Log4j2
public class TimerThread extends Thread {

    volatile boolean isRunning = true;
    final int TIMER_SECONDS = 5 * 60;
    final ContextService contextService;
    final OperationService blockOperationService;

    @Override
    public void run() {
        try {
            while (isRunning) {
                TimeUnit.SECONDS.sleep(TIMER_SECONDS);
                Collection<Context> notFinishedList = contextService.getNotFinishedContexts(TIMER_SECONDS);
                for (Context context : notFinishedList) {
                    blockOperationService.executeState(context);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopTimer() {
        isRunning = false;
        this.interrupt();
    }
}
