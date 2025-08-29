package com.petra.lib.context.repo;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.variable.container.ValueContainer;

import java.util.Optional;
import java.util.UUID;

public interface ContextRepo {

    /**
     * @param context - заполненый контекст
     */
    void insertContext(ContextEntity context);

    Optional<ContextEntity> findContext(UUID scenarioId, Identifier consumerId);

    void updateContext(ContextEntity entity);

    ContextState getState(ContextEntity entity);

    ValueContainer getContextValues(ContextEntity entity);


//    ContextState findCurrentState(UUID scenario, Identifier actionId);
//
//    boolean updateBlockState(UUID scenario, Identifier blockId, ContextState contextState);
//
//    void updateExecutionStatus(UUID scenario, Identifier actionId, ExecutionStatus executionStatus);
//
//
//    Collection<LoadedContext> findNotCompletedContexts(Identifier actionId, BlockType blockType);


}
