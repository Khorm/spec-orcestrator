package com.petra.lib.operation.operations;

import com.petra.lib.actor.local.LocalConsumer;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.executor.ConsumerCollection;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.Identifier;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс обслуживает набор локальных продюсеров сервиса
 */
@Log4j2
public final class BlockExecutingOperation implements Operation {

    private final ContextState CURRENT_STATE = ContextState.EXECUTED;
    private final ConsumerCollection locals;


    public BlockExecutingOperation(ConsumerCollection locals) {
        this.locals = locals;

    }

    @Override
    public void execute(Context blockContext, OperationService operationService) {
        locals.getById(blockContext.getCurrentBlockId()).execute(blockContext, operationService);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }


}
