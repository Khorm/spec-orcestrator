package com.petra.lib.controller;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.Value;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Result {
    private final WorkflowContextEntity workflowContext;

    public <T> List<T> getListValue(String name, Class<T> clazz) {
        Value value = workflowContext.getContextValues().getValue(name);
        return value.getParsedList(clazz);
    }

    public <T> T getValue(String name, Class<T> clazz) {
        return workflowContext.getContextValues().getValue(name).getParsedValue(clazz);
    }

    public Multiplicity getValueMultiplicty(String variableName) {
        return workflowContext.getContextValues().getValue(variableName).getMultiplicity();
    }

    public WorkflowContextState getState() {
        return this.workflowContext.getWorkflowState();
    }

    public Identifier getWorkflowId() {
        return workflowContext.getWorkflowId();
    }
}
