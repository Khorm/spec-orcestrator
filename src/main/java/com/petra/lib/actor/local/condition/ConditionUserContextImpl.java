package com.petra.lib.actor.local.condition;


import com.petra.lib.actor.remote.RemoteProducer;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
class ConditionUserContextImpl implements ConditionUserContext, Context {

    @Getter
    Integer acceptNumber;
    final EntityManager entityManager;
    final ValueContainer inputContainer;


    @Override
    public void setFirst() {
        checkAccept();
        acceptNumber = 1;
    }

    @Override
    public void setSecond() {
        checkAccept();
        acceptNumber = 2;
    }

    @Override
    public void setThird() {
        checkAccept();
        acceptNumber = 3;
    }

    @Override
    public void setFourth() {
        checkAccept();
        acceptNumber = 4;
    }

    @Override
    public void set(int index) {
        checkAccept();
        acceptNumber = index;
    }

    @Override
    public EntityManager getEntityManage(){
        return entityManager;
    }

    @Override
    public <T> T getValue(String variableName, Class<T> clazz) {
        if (inputContainer.containsValue(variableName)) {
            return inputContainer.getParsedValue(variableName, clazz);
        }
        return null;
    }

    @Override
    public <T> List<T> getValueList(String variableName, Class<T> clazz) {
        if (inputContainer.containsValue(variableName)){
            return inputContainer.getParsedList(variableName, clazz);
        }
        return null;
    }

    private void checkAccept() {
        if (acceptNumber != null) {
            throw new IllegalStateException("Accept number has been already defined");
        }
    }

    @Override
    public boolean setState(ContextState contextState) {
        return true;
    }

    @Override
    public void setExecutionStatus(ExecutionStatus executionStatus) {

    }

    @Override
    public void setOutValues(ValueContainer values) {

    }

    @Override
    public ContextState getCurrentState() {
        return null;
    }

    @Override
    public UUID getScenarioId() {
        return null;
    }

    @Override
    public RemoteProducer getProducer() {
        return null;
    }

    @Override
    public ValueContainer getContextOutValues() {
        return null;
    }

    @Override
    public ValueContainer getContextInputValues() {
        return null;
    }

    @Override
    public Identifier getCurrentBlockId() {
        return null;
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
        return null;
    }

    @Override
    public String getProducerServiceName() {
        return "";
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
    public boolean insert(RemoteProducer producer, BlockType blockType, ContextState state, Transaction transaction) {
        return true;
    }


    @Override
    public void load(Transaction transaction) {

    }
}
