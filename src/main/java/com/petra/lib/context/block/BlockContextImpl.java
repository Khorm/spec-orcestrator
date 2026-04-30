package com.petra.lib.context.block;

import com.petra.lib.actor.RemoteProducer;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockContextImpl implements Context {
    ContextEntity contextEntity;
    final ContextRepo contextRepo;
    static final List<ContextState> statesOrder
            = List.of(ContextState.STARTED, ContextState.EXECUTED, ContextState.ANSWERED);
    final Identifier blockId;
    final UUID scenarioId;
    TransactionManager tm;


    public BlockContextImpl(ContextEntity contextEntity, ContextRepo contextRepo) {
        this.contextRepo = contextRepo;
        this.blockId = contextEntity.getConsumerId();
        this.scenarioId = contextEntity.getScenarioId();
        this.contextEntity = contextEntity;
    }

    public BlockContextImpl(ContextRepo contextRepo,
                            Identifier blockId, UUID scenarioId) {
        this.contextRepo = contextRepo;
        this.blockId = blockId;
        this.scenarioId = scenarioId;
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

    public boolean insert(RemoteProducer producer, BlockType blockType,
                          ContextState state, ValueContainer outContextValues, Transaction transaction) {
        contextEntity = new ContextEntity(scenarioId, producer, blockType, state, outContextValues);
        return contextRepo.insertContext(contextEntity, transaction);

    }


    @Override
    public void load(Transaction transaction) {
        toLoad(false, transaction);
    }

    @Override
    public void save(Transaction transaction) {
        contextRepo.save(contextEntity, transaction);
    }

    @Override
    public boolean lockAndLoad(Transaction transaction) {
        return toLoad(true, transaction);
    }


    @Override
    public BlockType getBlockType() {
        return contextEntity.getBlockType();
    }

    @Override
    public void setExecutionStatus(ExecutionStatus executionStatus) {
        contextEntity.setExecutionStatus(executionStatus);
    }

    private boolean toLoad(boolean isLocking, Transaction tr) {

        Optional<ContextEntity> entity = contextRepo.findContext(scenarioId, blockId, tr, isLocking);

        if (entity.isPresent()) {
            contextEntity = entity.get();
            return true;
        } else {
            return false;
        }
    }


}
