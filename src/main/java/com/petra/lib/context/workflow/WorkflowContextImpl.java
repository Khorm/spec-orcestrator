package com.petra.lib.context.workflow;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class WorkflowContextImpl implements WorkflowContext {

    WorkflowContextEntity entity;
    final WorkflowContextRepo workflowContextRepo;
    final UUID scenarioId;
    final Identifier workflowId;


    public WorkflowContextImpl(WorkflowContextRepo workflowContextRepo,
                               UUID scenarioId, Identifier workflowId) {
        this.workflowContextRepo = workflowContextRepo;
        this.scenarioId = scenarioId;
        this.workflowId = workflowId;
    }


    @Override
    public UUID getScenarioId() {
        return entity.getScenarioId();
    }

    @Override
    public boolean setState(WorkflowContextState executedState) {
        if (executedState == WorkflowContextState.START) return false;
        if (executedState == WorkflowContextState.DONE
                && entity.getWorkflowState() == WorkflowContextState.ERROR) return false;
        if (executedState == WorkflowContextState.ERROR
                && entity.getWorkflowState() == WorkflowContextState.DONE) return false;
        entity.setWorkflowState(executedState);
        return true;
    }

    @Override
    public ValueContainer getContextValues() {
        return entity.getContextValues();
    }

    @Override
    public boolean create(ValueContainer inputValues, Transaction transaction) {
        entity = new WorkflowContextEntity(workflowId, scenarioId, inputValues,
                WorkflowContextState.START);

        return workflowContextRepo.insertContext(entity, transaction);
    }

    @Override
    public boolean lockAndLoad(Transaction transaction) {
        return toLoad(true, transaction);
    }

    @Override
    public void save(Transaction transaction) {
        workflowContextRepo.save(entity, transaction);

    }

    @Override
    public boolean load(Transaction transaction) {
        return toLoad(false, transaction);
    }

    @Override
    public void setContextValues(ValueContainer values) {
        entity.setContextValues(values);
    }

    @Override
    public Identifier getIdentifier() {
        return entity.getWorkflowId();
    }

    @Override
    public WorkflowContextState getState() {
        return entity.getWorkflowState();
    }

    private boolean toLoad(boolean isLocking, Transaction tr) {
        Optional<WorkflowContextEntity> entity = workflowContextRepo.findContext(scenarioId, workflowId, tr, isLocking);

        if (entity.isPresent()) {
            this.entity = entity.get();
            return true;
        } else {
            return false;
        }
    }
}
