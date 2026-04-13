package com.petra.lib.context.block;

import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.operation.actor.RemoteProducer;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockContextImpl implements Context {
    private ContextEntity contextEntity;
    private final ContextRepo contextRepo;
    private Transaction transaction;
    private static final List<ContextState> statesOrder
            = List.of(ContextState.STARTED, ContextState.EXECUTED, ContextState.ANSWERED);
    final Identifier blockId;
    final UUID scenarioId;
    final TransactionManager transactionManager;


    public BlockContextImpl(ContextEntity contextEntity, ContextRepo contextRepo,
                            TransactionManager transactionManager) {
        this.contextRepo = contextRepo;
        this.blockId = contextEntity.getConsumerId();
        this.scenarioId = contextEntity.getScenarioId();
        this.transactionManager = transactionManager;
        this.contextEntity = contextEntity;
    }

    public BlockContextImpl(ContextRepo contextRepo,
                            Identifier blockId, UUID scenarioId, TransactionManager transactionManager) {
        this.contextRepo = contextRepo;
        this.blockId = blockId;
        this.scenarioId = scenarioId;
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean setState(ContextState contextState) {
        if (statesOrder.indexOf(contextState) <= statesOrder.indexOf(contextEntity.getState())) {
            return false;
        }
        contextEntity.setState(contextState);
        return true;
    }

    @Override
    public void setOutValues(ValueContainer values) {
        contextEntity.setOutContextValues(values);
    }

    @Override
    public ContextState getCurrentState() {
        return contextEntity.getState();
    }

    @Override
    public UUID getScenarioId() {
        return contextEntity.getScenarioId();
    }

    @Override
    public RemoteProducer getProducer() {
        return contextEntity.getProducer();
    }


    @Override
    public ValueContainer getContextInputValues() {
        return contextEntity.getInputContextValues().clone();
    }

    @Override
    public Identifier getCurrentBlockId() {
        return contextEntity.getConsumerId();
    }

    @Override
    public ValueContainer getContextOutValues() {
        return contextEntity.getOutContextValues();
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
        return contextEntity.getExecutionStatus();
    }

    @Override
    public String getProducerServiceName() {
        return contextEntity.getProducer().getServiceName();
    }

    public boolean create(RemoteProducer producer, BlockType blockType,
                          ContextState state, ValueContainer outContextValues) {
        transaction = transactionManager.openNewTransaction();
        contextEntity = new ContextEntity(scenarioId,  producer,blockType, state, outContextValues);
        boolean result = contextRepo.insertContext(contextEntity, transaction);
        transaction.commit();
        transaction = null;
        return result;

    }

    @Override
    public void load() {
        toLoad(false, null);
    }

    @Override
    public void unlockAndSave() {
        contextRepo.save(contextEntity, transaction);
        transaction.commit();
        transaction = null;
    }

    @Override
    public boolean lockAndLoad() {
        return toLoad(true, null);
    }

    @Override
    public boolean lockAndLoad(Transaction transaction) {
        return toLoad(true, transaction);
    }

    public void unlockAndDiscard() {
        transaction.rollback();
        transaction = null;
    }

    @Override
    public BlockType getBlockType() {
        return contextEntity.getBlockType();
    }

    @Override
    public void setExecutionStatus(ExecutionStatus executionStatus) {
        contextEntity.setExecutionStatus(executionStatus);
    }

    private boolean toLoad(boolean isLocking, Transaction transaction) {
        if (this.transaction != null) {
            throw new IllegalMonitorStateException("Lock already acquired");
        }
        if (transaction == null) {
            this.transaction = transactionManager.openNewTransaction();
        }else {
            this.transaction = transaction;
        }
        Optional<ContextEntity> entity = contextRepo.findContext(scenarioId, blockId, transaction, isLocking);
        if (!isLocking) {
            this.transaction = null;
        }
        if (entity.isPresent()) {
            contextEntity = entity.get();
            return true;
        } else {
            return false;
        }
    }


}
