package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.operation.OperationService;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.transaction.TransactionRunnable;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextModel;
import org.springframework.transaction.annotation.Isolation;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockContextImpl implements Context {
    private ContextEntity contextEntity;
//    private final OperationService operationService;
//    private final TransactionManager transactionManager;
    private final ContextRepo contextRepo;
    private final LocalConsumer localConsumer;

    private ContextState contextState;
    private ValueContainer values;




    public BlockContextImpl(ContextEntity contextEntity,
//                            OperationService operationService, TransactionManager transactionManager,
                            ContextRepo contextRepo, LocalConsumer localConsumer) {
        this.contextEntity = contextEntity;
//        this.operationService = operationService;
//        this.transactionManager = transactionManager;
        this.contextRepo = contextRepo;
        this.localConsumer = localConsumer;
    }

    @Override
    public synchronized void setState(ContextState contextState) {
        this.contextState = contextState;
    }

    @Override
    public synchronized void setOutValues(ValueContainer values) {
        this.values = values;
    }

    @Override
    public ContextState getCurrentState() {
        if (contextState != null) {
            return contextState;
        }
        return contextEntity.getState();
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
    public ValueContainer getContextInputValues() {
        return contextEntity.getInputContextValues().clone();
    }

    @Override
    public Identifier getCurrentBlockId() {
        return contextEntity.getConsumerId();
    }

    @Override
    public ValueContainer getContextValues() {
        if (values != null) {
            return values;
        }
        ValueContainer newCont = contextEntity.getOutContextValues();
        newCont.mixinValueContainer(contextEntity.getInputContextValues());
        return newCont;
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
        return contextEntity.getExecutionStatus();
    }

    @Override
    public String getProducerServiceName() {
        return contextEntity.getProducer().getServiceName();
    }

    @Override
    public void save() {
        contextRepo.updateStateAndValues(contextEntity, contextState, values);
    }

    @Override
    public void saveError(Exception e) {
        e.printStackTrace();
        contextEntity.setExecutionStatus(ExecutionStatus.ERROR);
        contextRepo.updateExecutionStatus(contextEntity, ContextState.EXECUTED, ExecutionStatus.ERROR);
    }

}
