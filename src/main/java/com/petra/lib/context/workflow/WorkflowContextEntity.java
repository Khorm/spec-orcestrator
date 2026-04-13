package com.petra.lib.context.workflow;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Хранит контекст исполнения и результат исполнения workflow
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class WorkflowContextEntity {

    final Identifier workflowId;

    final UUID scenarioId;

    WorkflowContextState workflowState = WorkflowContextState.START;
    boolean isWorkflowStateChanged;

    ValueContainer contextValues;
    boolean isContextValuesUpdated;


    public WorkflowContextEntity(Identifier workflowId, UUID scenarioId, ValueContainer contextValues,
                                 WorkflowContextState workflowState) {
        this.workflowId = workflowId;
        this.scenarioId = scenarioId;
        this.contextValues = contextValues;
        this.workflowState = workflowState;

    }

    public void setContextValues(ValueContainer contextValues) {
        isContextValuesUpdated = true;
        this.contextValues = contextValues;
    }

    public void setWorkflowState(WorkflowContextState state){
        isWorkflowStateChanged = true;
        workflowState = state;
    }

}
