package com.petra.lib.operation.operations;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.operation.Operation;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public final class WorkflowExecutingOperation implements Operation {

    private final ContextState CURRENT_STATE = ContextState.EXECUTED;
    private final Map<Identifier, LocalProducer> localProducers;

    public WorkflowExecutingOperation(Collection<LocalProducer> localProducers) {
        this.localProducers = localProducers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));
    }

    @Override
    public void execute(Context blockContext) {
        log.info("Start executing workflow {}", blockContext.getScenarioId().toString());
        localProducers.get(blockContext.getCurrentBlockId()).start(blockContext);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
