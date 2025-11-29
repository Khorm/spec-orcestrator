package com.petra.lib.context.workflow;

import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.operation.WorkflowOperationService;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;


public class WorkflowContextEntity {

    private final ConsumerIdentifier consumerIdentifier;
    private final UUID scenarioId;
    private WorkflowContextState workflowState = WorkflowContextState.START;
    private ValueContainer resultValues;

    public WorkflowContextEntity(ConsumerIdentifier consumerIdentifier, UUID scenarioId) {
        this.consumerIdentifier = consumerIdentifier;
        this.scenarioId = scenarioId;
    }


    public ValueContainer getResultValues() {
        return resultValues;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public void setResultValues(ValueContainer resultValues) {
        this.resultValues = resultValues;
    }

    public WorkflowContextState getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(WorkflowContextState workflowState) {
        this.workflowState = workflowState;

    }

    public ConsumerIdentifier getConsumerIdentifier() {
        return consumerIdentifier;
    }
}
