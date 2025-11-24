package com.petra.lib.operation.operations;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.operation.Operation;

public final class WorkflowExecutingOperation implements Operation {

    private final ContextState CURRENT_STATE = ContextState.EXECUTED;
    private final LocalProducer localProducer;

    public WorkflowExecutingOperation(LocalProducer localProducer) {
        this.localProducer = localProducer;
    }

    @Override
    public void execute(Context blockContext) {
        localProducer.start(blockContext);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
