package com.petra.lib.block.action.repo;

import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.variable.value.ValueContainer;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ActionRepo {

//    boolean updateBlockHistoryVariables(UUID scenario, Identifier blockId,
//                                        ValueContainer actionVariables);

    BlockState findCurrentState(UUID scenario, Identifier actionId);

    boolean updateBlockState(UUID scenario, Identifier blockId, BlockState blockState) ;

    void updateExecutionStatus(UUID scenario, Identifier actionId, ExecutionStatus executionStatus);


    /**
     * @param actionContext
     * @return true - если удалось сохранить контекст
     * false - если не удалось сохранить контекст так как он уже существует
     */
    boolean createContext(ActionContext actionContext, HistoryType historyType);

    Collection<LoadedContext> findNotCompletedContexts(Identifier actionId, HistoryType historyType);

    Optional<LoadedContext> findContext(UUID scenarioId, Identifier actionId);

}
