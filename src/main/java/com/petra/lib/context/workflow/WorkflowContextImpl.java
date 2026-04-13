package com.petra.lib.context.workflow;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Optional;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkflowContextImpl implements WorkflowContext {

    WorkflowContextEntity entity;
    final WorkflowContextRepo workflowContextRepo;
    Transaction transaction;
    final TransactionManager transactionManager;
    final UUID scenarioId;
    final Identifier workflowId;


    public WorkflowContextImpl(WorkflowContextRepo workflowContextRepo, TransactionManager transactionManager,
                               UUID scenarioId, Identifier workflowId) {
        this.workflowContextRepo = workflowContextRepo;
        this.transactionManager = transactionManager;
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

    public boolean create() {
        transaction = transactionManager.openNewTransaction();
        entity = new WorkflowContextEntity(workflowId, scenarioId, ValueContainerFactory.getSimpleContainer(),
                WorkflowContextState.START);
        boolean result = workflowContextRepo.insertContext(entity, transaction);
        entity = null;
        transaction.commit();
        transaction = null;
        return result;
    }

    public boolean lockAndLoad() {
//        if (transaction != null) {
//            throw new IllegalMonitorStateException("Lock already acquired");
//        }
//        transaction = transactionManager.openNewTransaction();
//        Optional<WorkflowContextEntity> entity = workflowContextRepo.findContext(scenarioId, workflowId, transaction);
//        if (entity.isPresent()) {
//            this.entity = entity.get();
//            return true;
//        } else {
//            return false;
//        }
        return toLoad(true, null);
    }

    public void unlockAndSave() {
        workflowContextRepo.save(entity, transaction);
        transaction.commit();
        transaction = null;
        entity = null;
    }

    public void unlockAndDiscard() {
        transaction.rollback();
        transaction = null;
        entity = null;
    }

    @Override
    public boolean load() {
        return toLoad(false, null);
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

    private boolean toLoad(boolean isLocking, Transaction transaction) {
        if (this.transaction != null) {
            throw new IllegalMonitorStateException("Lock already acquired");
        }
        if (transaction == null) {
            this.transaction = transactionManager.openNewTransaction();
        } else {
            this.transaction = transaction;
        }
        Optional<WorkflowContextEntity> entity = workflowContextRepo.findContext(scenarioId, workflowId, transaction, isLocking);
        if (!isLocking) {
            this.transaction = null;
        }
        if (entity.isPresent()) {
            this.entity = entity.get();
            return true;
        } else {
            return false;
        }
    }
}
