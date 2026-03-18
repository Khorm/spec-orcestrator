package com.petra.lib.context.repo;

import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.transaction.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface ContextRepo {

    boolean insertContext(ContextEntity context, Transaction transaction);

    Optional<ContextEntity> findContext(UUID scenarioId, Identifier consumerId, Transaction transaction, boolean isBlocking);
    void save(ContextEntity entity, Transaction transaction);

    ContextEntity getNotFinishedContexts(Identifier blockId);
//    void updateStateAndValues(ContextEntity entity, ContextState state, ValueContainer outValues);
//    void updateExecutionStatus(ContextEntity entity,ContextState state, ExecutionStatus executionStatus);


}
