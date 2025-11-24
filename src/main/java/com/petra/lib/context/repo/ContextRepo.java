package com.petra.lib.context.repo;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
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

    Optional<Context> findContext(UUID scenarioId, Identifier consumerId);

    void updateStateAndValues(ContextEntity entity, ContextState state, ValueContainer outValues);
    void updateExecutionStatus(ContextEntity entity,ContextState state, ExecutionStatus executionStatus);

//    ContextState getState(Context entity);

//    ValueContainer getContextValues(Context entity);

}
