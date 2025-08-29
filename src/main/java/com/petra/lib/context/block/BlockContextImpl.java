package com.petra.lib.context.block;

import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.operation.OperationServiceImpl;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextModel;
import org.springframework.transaction.annotation.Isolation;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockContextImpl implements BlockContext {
    private ContextEntity contextEntity;
    private final OperationServiceImpl<BlockContext> operationService;
    private final TransactionManager transactionManager;
    private final ContextRepo contextRepo;
    private final ValueContextModel valueContextModel;
    private final LocalConsumer localConsumer;
    private final ContextState[] states = {ContextState.CREATED, ContextState.STARTED, ContextState.EXECUTED, ContextState.ANSWERED};

    public BlockContextImpl(ContextEntity contextEntity, OperationServiceImpl<BlockContext> operationService, TransactionManager transactionManager,
                            ContextRepo contextRepo, LocalConsumer localConsumer) {
        this.contextEntity = contextEntity;
        this.operationService = operationService;
        this.transactionManager = transactionManager;
        this.contextRepo = contextRepo;
        this.valueContextModel = new ValueContextModel(localConsumer.getValuesCount(), localConsumer.getStartedLoaders());
        this.localConsumer = localConsumer;
    }

    @Override
    public UUID getScenarioId() {
        return contextEntity.getScenarioId();
    }

    @Override
    public RemoteProducer getProducer() {
        return contextEntity.getProducer();
    }

    @Override
    public LocalConsumer getConsumer() {
        return localConsumer;
    }

    @Override
    public ContextState getContextState() {
        return contextEntity.getState();
    }

    @Override
    public ValueContainer getContextInputContainer() {
        return contextEntity.getInputContextValues().clone();
    }

    @Override
    public ValueContainer getContextOutputContainer() {
        return contextEntity.getOutContextValues().clone();
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
        return contextEntity.getExecutionStatus();
    }

    @Override
    public synchronized void setState(ValueContainer context, ContextState executedState) {
        AtomicBoolean isNextStateAvailable = new AtomicBoolean(true);
        transactionManager.executeInTransaction(transaction -> {

            //проверять ожелаемый стейт
            ContextState currentState = contextRepo.getState(contextEntity);
            if (getNextState(currentState) == executedState) {

                //если текущий контекст управляет изменением статуса то применить изменения и изменить статус
                contextEntity.getInputContextValues().mixinValueContainer(context);
                contextEntity.setState(executedState);
                contextRepo.updateContext(contextEntity);

                //если контекст находится под локальным управлением, остановить исполнение после отправки сообщения
                isNextStateAvailable.set(executedState == ContextState.ANSWERED);
            } else {

                //если ктото уже исполняет контекст, продвинуть его вперед выгрузив обновленный контекст
                contextEntity.setInputContextValues(contextRepo.getContextValues(contextEntity));
                contextEntity.setState(currentState);
            }
        }, Isolation.SERIALIZABLE);

        if (!isNextStateAvailable.get()) return;
        operationService.executeState(this, getNextState(contextEntity.getState()));
    }

    @Override
    public synchronized void error(Exception e) {
        e.printStackTrace();
        contextEntity.setExecutionStatus(ExecutionStatus.ERROR);
        operationService.executeState(this, ContextState.ANSWERED);
    }

    @Override
    public ValueContextModel getValueContextModel() {
        return valueContextModel;
    }

    @Override
    public synchronized void run() {
        try {
            transactionManager.executeInTransaction(transaction -> {
                Optional<ContextEntity> anotherContext = contextRepo.findContext(contextEntity.getScenarioId(),
                        contextEntity.getConsumerId());
                if (anotherContext.isPresent()) {
                    contextEntity = anotherContext.get();
                } else {
                    contextRepo.insertContext(contextEntity);
                }
            }, Isolation.SERIALIZABLE);
        } catch (Exception e) {
            error(e);
        }

        if (contextEntity.getState() == ContextState.ANSWERED) {
            operationService.executeState(this, contextEntity.getState());
        } else {
            operationService.executeState(this, getNextState(contextEntity.getState()));
        }
    }


    private ContextState getNextState(ContextState currentState) {
        if (currentState == ContextState.ANSWERED) {
            return ContextState.ANSWERED;
        }

        for (int i = 0; i < states.length; i++) {
            if (states[i] == currentState) {
                return states[i + 1];
            }
        }
        throw new NullPointerException();
    }


}
