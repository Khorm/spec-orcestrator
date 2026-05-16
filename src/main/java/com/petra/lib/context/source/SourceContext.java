package com.petra.lib.context.source;

import com.petra.lib.actor.remote.RemoteProducer;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class SourceContext implements Context {

    ContextState contextState;
    ExecutionStatus executionStatus;
    ValueContainer outValues;
    final UUID scenarioID;
    final RemoteProducer remoteProducer;
    final ValueContainer inputValues;
    final Identifier sourceId;


    @Override
    public boolean setState(ContextState contextState) {
        this.contextState = contextState;
        return true;
    }

    @Override
    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    @Override
    public void setOutValues(ValueContainer values) {
        this.outValues = values;
    }

    @Override
    public ContextState getCurrentState() {
        return contextState;
    }

    @Override
    public UUID getScenarioId() {
        return scenarioID;
    }

    @Override
    public RemoteProducer getProducer() {
        return remoteProducer;
    }

    @Override
    public ValueContainer getContextOutValues() {
        return outValues;
    }

    @Override
    public ValueContainer getContextInputValues() {
        return inputValues;
    }

    @Override
    public Identifier getCurrentBlockId() {
        return sourceId;
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    @Override
    public String getProducerServiceName() {
        return remoteProducer.getServiceName();
    }

    @Override
    public void save(Transaction transaction) {

    }

    @Override
    public boolean lockAndLoad(Transaction transaction) {
        return true;
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.CONDITION;
    }

    @Override
    public boolean insert(RemoteProducer producer, BlockType blockType, ContextState state,  Transaction transaction) {
        return true;
    }

    @Override
    public void load(Transaction transaction) {

    }
}
