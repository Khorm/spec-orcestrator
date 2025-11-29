package com.petra.lib.operation.operations;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.operation.Operation;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class WorkflowExecutingOperation implements Operation {

    private final ContextState CURRENT_STATE = ContextState.EXECUTED;
    private final Map<Identifier, LocalProducer> localProducers;

    public WorkflowExecutingOperation(Collection<LocalProducer> localProducers) {
        this.localProducers = localProducers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));
    }

    @Override
    public void execute(Context blockContext) {
        localProducers.get(blockContext.getCurrentBlockId()).start(blockContext);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
