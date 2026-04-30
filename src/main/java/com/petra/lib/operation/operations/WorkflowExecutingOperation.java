package com.petra.lib.operation.operations;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.actor.producer.LocalProducer;
import com.petra.lib.operation.Operation;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс обслуживает набор локальных продюсеров сервиса
 */
@Log4j2
public final class WorkflowExecutingOperation implements Operation {

    private final ContextState CURRENT_STATE = ContextState.EXECUTED;
    private final Map<Identifier, LocalProducer> localProducers;


    public WorkflowExecutingOperation(Collection<LocalProducer> localProducers) {
        this.localProducers = localProducers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));

    }

    @Override
    public void execute(Context blockContext, OperationService operationService) {
        localProducers.get(blockContext.getCurrentBlockId()).start(blockContext,operationService);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }


}
