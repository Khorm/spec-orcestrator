package com.petra.lib.context.workflow;

import com.petra.lib.context.block.WorkflowContextState;
import com.petra.lib.context.model.ConsumerIdentifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class WorkflowContextEntity {

    final ConsumerIdentifier consumerIdentifier;
    final UUID scenarioId;

    WorkflowContextState workflowState = WorkflowContextState.START;
    boolean areWorkflowStateChanged;

//    ValueContainer resultValues;
//    boolean areResultValuesChanged;


//    final ValueContainer inputValues;


    public WorkflowContextEntity(ConsumerIdentifier consumerIdentifier, UUID scenarioId) {
        this.consumerIdentifier = consumerIdentifier;
        this.scenarioId = scenarioId;
//        this.inputValues = inputValues;
    }


//    public ValueContainer getInputValues() {
//        return resultValues;
//    }
//
//    public UUID getScenarioId() {
//        return scenarioId;
//    }
//
//    void setResultValues(ValueContainer resultValues) {
//        this.resultValues = resultValues;
//        areResultValuesChanged = true;
//    }

    public WorkflowContextState getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(WorkflowContextState workflowState) {
        this.workflowState = workflowState;
        areWorkflowStateChanged = true;
    }

    public ConsumerIdentifier getConsumerIdentifier() {
        return consumerIdentifier;
    }
}
