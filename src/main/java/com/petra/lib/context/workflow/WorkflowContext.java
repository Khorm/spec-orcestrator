package com.petra.lib.context.workflow;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public interface WorkflowContext {
    UUID getScenarioId();

    boolean create(ValueContainer inputValues, Transaction transaction);
    boolean lockAndLoad(Transaction transaction);
    void save(Transaction transaction);
    boolean load(Transaction transaction);
    void setContextValues(ValueContainer values);

    Identifier getIdentifier();

    ValueContainer getContextValues();
    WorkflowContextState getState();
    boolean setState(WorkflowContextState executedState);
}
