package com.petra.lib.context.block;

import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.actor.remote.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public interface Context {
    boolean setState(ContextState contextState);
    void setExecutionStatus(ExecutionStatus executionStatus);

    void setOutValues(ValueContainer values);

    ContextState getCurrentState();

    UUID getScenarioId();

    RemoteProducer getProducer();

    ValueContainer getContextOutValues();

    ValueContainer getContextInputValues();

    Identifier getCurrentBlockId();

    ExecutionStatus getExecutionStatus();

    String getProducerServiceName();

    void save(Transaction transaction);

//    void saveError(Exception e);
//    void saveRepeat();
    boolean lockAndLoad(Transaction transaction);

    BlockType getBlockType();

    boolean insert(RemoteProducer producer, BlockType blockType,
                   ContextState state,  Transaction transaction);

    void load(Transaction transaction);
}
