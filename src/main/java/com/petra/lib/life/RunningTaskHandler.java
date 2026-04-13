package com.petra.lib.life;

import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunningTaskHandler implements SmartLifecycle {
    boolean isRunning = false;
//    final LifeController lifeController;

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void stop() {

    }


    @Override
    public void stop(Runnable callback) {
        System.out.println("End signal received. Waiting for tasks to complete");

        // 1. Логика ожидания: например, проверяем счетчик активных задач
//        while (lifeController.hasActiveTasks()) {
//            try {
//                Thread.sleep(1000); // Ждем завершения
//                System.out.println("Tasks in progress...");
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }

        System.out.println("All tasks completed. Shutting down");
        isRunning = false;
        callback.run(); // Только после этого вызова Spring пойдет дальше
    }

    @Override
    public boolean isRunning() { return isRunning; }

    @Override
    public int getPhase() {
        // Чем выше число, тем раньше начнется остановка этого бина
        return Integer.MAX_VALUE;
    }
}
