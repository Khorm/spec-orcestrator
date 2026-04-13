package com.petra.lib.controller;

import com.petra.lib.context.executor.BlockContextExecutor;
import com.petra.lib.context.executor.WorkflowAnswerExecutor;
import com.petra.lib.context.source.SourceContextExecutor;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.operation.actor.RemoteProducer;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.SmartLifecycle;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PetraControllerImpl implements PetraController, HealthIndicator, SmartLifecycle {
    final BlockContextExecutor blockContextExecutor;
    final SourceContextExecutor sourceContextExecutor;
    final WorkflowAnswerExecutor workflowAnswerExecutor;

    final ThreadController threadController;
    final WorkflowContextRepo workflowContextRepo;
    volatile boolean isRunning = true;


    /**
     * Starts a context for the given Message
     *
     * @param messageDto
     */
    public boolean requestBlock(MessageDto messageDto) {
        if (!isRunning) throw new IllegalStateException();
        return blockContextExecutor.startContext(messageDto.getScenarioId(), createProducer(messageDto));
    }

    /**
     * Starts an execution of source
     *
     * @param messageDto
     * @return
     */
    public SourceResponseDto requestSource(SourceRequestDto messageDto) {
        if (!isRunning) throw new IllegalStateException();
        ValueContainer valueContainer = sourceContextExecutor.startContext(messageDto);
        return messageDto.toOutput(valueContainer);
    }

    /**
     * Handles answer from remote consumer
     *
     * @param messageDto
     */
    public void blockAnswer(MessageDto messageDto) {
        if (!isRunning) throw new IllegalStateException();
        Identifier blockId = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier workflowId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());

        workflowAnswerExecutor.handleAnswerFromBlock(ValueContainerFactory.getSimpleContainer(messageDto.getTransmittedValues()),
                blockId, messageDto.getScenarioId(), workflowId, messageDto.getStatus());
    }

    private RemoteProducer createProducer(MessageDto messageDto) {

        Identifier identifier = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier consumerId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());
        return new RemoteProducer(identifier, messageDto.getSenderServiceURL(),
                ValueContainerFactory.getSimpleContainer(messageDto.getTransmittedValues()), consumerId);
    }

    @Override
    public void executeWorkflow(String workflowName, String version, Map<String, Object> params) {
        if (!isRunning) throw new IllegalStateException();
        blockContextExecutor.startWorkflowByUser(workflowName, version, params);
    }

    @Override
    public void executeWorkflow(String workflowName, String version, Map<String, Object> params, UUID scenarioId) {
        if (!isRunning) throw new IllegalStateException();
        blockContextExecutor.startWorkflowByUser(workflowName, version, params, scenarioId);
    }

    @Override
    public Result getResult(UUID scenarioId) {
        if (!isRunning) throw new IllegalStateException();

        Optional<WorkflowContextEntity> optional = workflowContextRepo.findFinishedContext(scenarioId);
        if (optional.isEmpty()) {
            throw new NullPointerException("No result");
        }
        return new Result(optional.get());
    }


    @Override
    public Health health() {
        // 1. Ваша логика проверки (например, количество живых потоков)

        if (isRunning) {
            return Health.up()
                    .withDetail("status", "Работаем штатно")
                    .build();
        } else {
            return Health.down()
                    .withDetail("error", "Слишком много задач в очереди!")
                    .build();
        }
    }

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void stop() {
        System.out.println("End signal received. Waiting for tasks to complete");
        isRunning = false;
        // 1. Логика ожидания: например, проверяем счетчик активных задач
        do {
            try {
                Thread.sleep(1000); // Ждем завершения
                System.out.println("Tasks in progress...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } while (0 < threadController.getActiveThreadCount());

        System.out.println("All tasks completed. Shutting down");
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        // Чем выше число, тем раньше начнется остановка этого бина
        return Integer.MAX_VALUE;
    }
}
