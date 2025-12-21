package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.operation.OperationService;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public class BlockContextImpl implements Context {
    private ContextEntity contextEntity;
    private final ContextRepo contextRepo;
    private ContextState contextState;
    private ValueContainer values;
    private final OperationService blockOperationService;




    public BlockContextImpl(ContextEntity contextEntity,
                            ContextRepo contextRepo, OperationService blockOperationService
    ) {
        this.contextEntity = contextEntity;
        this.contextRepo = contextRepo;
        this.blockOperationService = blockOperationService;
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
    public ValueContainer getContextOutValues() {
        if (values != null) {
            return values;
        }
        ValueContainer newCont = contextEntity.getOutContextValues();
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
        contextRepo.updateStateAndValues(contextEntity, getCurrentState(), getContextOutValues());
        blockOperationService.executeState(this);
    }

    @Override
    public void saveError(Exception e) {
        e.printStackTrace();
        contextEntity.setExecutionStatus(ExecutionStatus.ERROR);
        contextRepo.updateExecutionStatus(contextEntity, ContextState.EXECUTED, ExecutionStatus.ERROR);
        blockOperationService.executeState(this);
    }

}
