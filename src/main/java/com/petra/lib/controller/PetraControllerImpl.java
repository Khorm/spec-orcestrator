package com.petra.lib.controller;

import com.petra.lib.actor.remote.RemoteProducer;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.executor.BlockContextExecutor;
import com.petra.lib.executor.RepeatException;
import com.petra.lib.executor.WorkflowAnswerExecutor;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.timer.TimerThread;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
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
public class PetraControllerImpl implements PetraController, HealthIndicator, SmartLifecycle, RequestController {
    final BlockContextExecutor blockContextExecutor;
    final WorkflowAnswerExecutor workflowAnswerExecutor;
    final TransactionManager transactionManager;
    final ThreadController threadController;
    final WorkflowContextRepo workflowContextRepo;
    final TimerThread timer;
    volatile boolean isRunning = true;


    /**
     * Starts a context for the given Message
     *
     * @param messageDto
     */
    @Override
    public MessageResponse requestBlock(MessageDto messageDto) {
        if (!isRunning) throw new IllegalStateException();
        RemoteProducer remoteProducer = createProducer(messageDto);
        return blockContextExecutor.start(messageDto.getScenarioId(), remoteProducer);
    }

    /**
     * Starts an execution of source
     *
     * @param messageDto
     * @return
     */
    @Override
    public SourceResponseDto requestSource(SourceRequestDto messageDto) {
        if (!isRunning) throw new IllegalStateException();

        Identifier consumerId = new Identifier(messageDto.getConsumerSourceId(), messageDto.getConsumerSourceVersion());
        RemoteProducer remoteProducer = new RemoteProducer(null, null, messageDto.getInputValues(), consumerId);
        MessageResponse response = blockContextExecutor.start(messageDto.getScenarioId(), remoteProducer);
        return new SourceResponseDto(messageDto.getScenarioId(),
                messageDto.getConsumerSourceId(),
                messageDto.getConsumerSourceVersion(),
                response.getResultValues());
    }

    /**
     * Handles answer from remote consumer
     *
     * @param messageDto
     */
    @Override
    public void blockAnswer(MessageDto messageDto) {
        if (!isRunning) throw new IllegalStateException();
        Identifier blockId = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier workflowId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());

        workflowAnswerExecutor.handleAnswerFromBlock(ValueContainerFactory.getSimpleContainerByDtos(messageDto.getTransmittedValues()),
                blockId, messageDto.getScenarioId(), workflowId, messageDto.getStatus());
    }

    private RemoteProducer createProducer(MessageDto messageDto) {

        Identifier identifier = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier consumerId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());
        return new RemoteProducer(identifier, messageDto.getSenderServiceURL(),
                ValueContainerFactory.getSimpleContainerByDtos(messageDto.getTransmittedValues()), consumerId);
    }

    @Override
    public UUID executeWorkflow(String workflowName, String version, Map<String, Object> params) throws RepeatException {
        if (!isRunning) throw new IllegalStateException();
        UUID scenarioID = UUID.randomUUID();
        blockContextExecutor.startFromUser(scenarioID, workflowName, version, params);
        return scenarioID;
    }

    @Override
    public void executeWorkflow(String workflowName, String version, Map<String, Object> params, UUID scenarioId) throws RepeatException {
        if (!isRunning) throw new IllegalStateException();
        blockContextExecutor.startFromUser(scenarioId, workflowName, version, params);
    }

    @Override
    public Result getResult(UUID scenarioId) {
        if (!isRunning) throw new IllegalStateException();
        Optional<WorkflowContextEntity> optional;

        try (Transaction tr = transactionManager.createNewTransaction(true, null)) {
            optional = workflowContextRepo.findFinishedContext(scenarioId, tr);
            if (optional.isEmpty()) {
                throw new NullPointerException("No result");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        timer.start();
    }

    @Override
    public void stop() {
        System.out.println("End signal received. Waiting for tasks to complete");
        isRunning = false;
        timer.stopTimer();
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
