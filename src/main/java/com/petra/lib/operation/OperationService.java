package com.petra.lib.operation;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.thread.ThreadController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Отвечает за вызов операций переключающий стейты
 */
public abstract class OperationService {
    private final Map<ContextState, Operation> operationsByStates = new HashMap<>();
    private final ThreadController threadController;
    private final List<ContextState> executingStates = new ArrayList<>();

    OperationService(ThreadController threadController) {
        this.threadController = threadController;
    }


    public void executeState(Context blockContext){
        executeState(blockContext, getNextState(blockContext.getCurrentState()));
    }

    private void executeState(Context blockContext, ContextState state) {
        threadController.executeUnlimitedPoolTask(() -> {
            try {
                operationsByStates.get(state).execute(blockContext);
            } catch (Exception e) {
                blockContext.saveError(e);
                executeState(blockContext);
            }
        });
    }

    public void addOperation(Operation operation){
        operationsByStates.put(operation.getState(), operation);
        executingStates.add(operation.getState());
    }


    private ContextState getNextState(ContextState currentState) {
        if (currentState == ContextState.ANSWERED) {
            return null;
        }

        for (int i = 0; i < executingStates.size() ; i++) {
            if (executingStates.get(i) == currentState) {
                return executingStates.get(i + 1);
            }
        }
        throw new NullPointerException();
    }

}
