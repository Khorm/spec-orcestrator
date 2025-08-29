package com.petra.lib.context.operation;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.thread.ThreadController;

import java.util.HashMap;
import java.util.Map;

public class OperationServiceImpl<T extends Context>{
    private final Map<ContextState, Operation<T>> operationsByStates = new HashMap<>();
    private final ThreadController threadController;

    public OperationServiceImpl(ThreadController threadController, Operation<T> ... operations) {
        this.threadController = threadController;
        for (Operation<T> oper : operations) {
            operationsByStates.put(oper.getState(), oper);
        }
    }

    public void executeState(T blockContext, ContextState state) {
        threadController.executeLimitedPoolTask(() -> {
            try {
                operationsByStates.get(state).execute(blockContext);
            } catch (Exception e) {
                blockContext.error(e);
            }
        });
    }

}
