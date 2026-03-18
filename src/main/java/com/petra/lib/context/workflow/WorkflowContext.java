package com.petra.lib.context.workflow;

import com.petra.lib.context.block.WorkflowContextState;
import com.petra.lib.context.model.ConsumerIdentifier;

import java.util.UUID;

public interface WorkflowContext {
    UUID getScenarioId();

    boolean setState(WorkflowContextState executedState);
    boolean create();
    boolean lockAndLoad();
    void unlockAndSave();
    void unlockAndDiscard();

    WorkflowContextState getState();

    ConsumerIdentifier getIdentifier();

}
