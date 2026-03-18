package com.petra.lib.context.workflow;

import com.petra.lib.context.block.WorkflowContextState;
import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.repo.WorkflowContextRepo;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
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
    final ConsumerIdentifier consumerIdentifier;


    public WorkflowContextImpl(WorkflowContextRepo workflowContextRepo, TransactionManager transactionManager,
                               UUID scenarioId, ConsumerIdentifier consumerIdentifier) {
        this.workflowContextRepo = workflowContextRepo;
        this.transactionManager = transactionManager;
        this.scenarioId = scenarioId;
        this.consumerIdentifier = consumerIdentifier;
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

//    @Override
//    public ValueContainer getInputValues() {
//        return entity.getInputValues();
//    }
//
//    @Override
//    public void setInputValues(ValueContainer resultValues) {
//        entity.setResultValues(resultValues);
//    }

    @Override
    public ConsumerIdentifier getIdentifier() {
        return entity.getConsumerIdentifier();
    }

    public boolean create() {
        boolean result = workflowContextRepo.insertContext(entity, transaction);
        entity = null;
        return result;
    }

    public boolean lockAndLoad() {
        if (transaction != null) {
            throw new IllegalMonitorStateException("Lock already acquired");
        }
        transaction = transactionManager.openNewTransaction();
        Optional<WorkflowContextEntity> entity = workflowContextRepo.findContext(scenarioId, consumerIdentifier, transaction);
        if (entity.isPresent()) {
            this.entity = entity.get();
            return true;
        } else {
            return false;
        }
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
    public WorkflowContextState getState() {
        return entity.getWorkflowState();
    }
}
