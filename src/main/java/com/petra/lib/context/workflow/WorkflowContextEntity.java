package com.petra.lib.context.workflow;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.ContextState;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public class WorkflowContextEntity {
    private final Identifier consumerId;
    private final Identifier producerId;
    private final UUID scenarioId;
    private ContextState workflowState = ContextState.CREATED;
    private ValueContainer resultValues;

    public WorkflowContextEntity(Identifier consumerId, Identifier producerId, UUID scenarioId) {
        this.consumerId = consumerId;
        this.producerId = producerId;
        this.scenarioId = scenarioId;
    }

    public ContextState getWorkflowState() {
        return workflowState;
    }

    public ValueContainer getResultValues() {
        return resultValues;
    }

    public Identifier getConsumerId() {
        return consumerId;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public void setWorkflowState(ContextState workflowState) {
        this.workflowState = workflowState;
    }

    public void setResultValues(ValueContainer resultValues) {
        this.resultValues = resultValues;
    }
}
