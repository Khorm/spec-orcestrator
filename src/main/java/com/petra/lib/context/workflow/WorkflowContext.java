package com.petra.lib.context.workflow;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public interface WorkflowContext {
    UUID getScenarioId();

    boolean create();
    boolean lockAndLoad();
    void unlockAndSave();
    void unlockAndDiscard();
    boolean load();
    void setContextValues(ValueContainer values);

    Identifier getIdentifier();

    ValueContainer getContextValues();
    WorkflowContextState getState();
    boolean setState(WorkflowContextState executedState);
}
