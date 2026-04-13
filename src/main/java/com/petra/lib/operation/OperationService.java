package com.petra.lib.operation;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.thread.ThreadController;

import java.util.HashMap;
import java.util.Map;

/**
 * Отвечает за вызов операций переключающий стейты
 */
public class OperationService {
    private final Map<ContextState, Operation> operationsByStates = new HashMap<>();
    private final ThreadController threadController;

    OperationService(ThreadController threadController) {
        this.threadController = threadController;
    }


    public void executeState(Context blockContext){
        executeState(blockContext, getNextState(blockContext.getCurrentState()));
    }

    private synchronized void executeState(Context blockContext, ContextState state) {
        if (state == null) return;
        threadController.executeUnlimitedPoolTask(() -> {
            try {
                operationsByStates.get(state).execute(blockContext, this);
            } catch (Exception e) {
                blockContext.lockAndLoad();
                boolean stateResult = blockContext.setState(ContextState.EXECUTED);
                if (stateResult) {
                    blockContext.setExecutionStatus(ExecutionStatus.ERROR);
                }
                blockContext.unlockAndSave();
                executeState(blockContext);
            }
        });
    }

    public void addOperation(Operation operation){
        operationsByStates.put(operation.getState(), operation);
//        executingStates.add(operation.getState());
    }


    private ContextState getNextState(ContextState currentState) {
        return currentState.getNext();
//        if (currentState == ContextState.ANSWERED) {
//            return null;
//        }
//
//        if (currentState == ContextState.STARTED) {
//            for (ContextState state : executingStates) {
//                if (state == ContextState.EXECUTED){
//                    return state;
//                }
//            }
//        }
//
//        for (int i = 0; i < executingStates.size() ; i++) {
//            if (executingStates.get(i) == currentState) {
//                return executingStates.get(i + 1);
//            }
//        }
//        throw new NullPointerException();
    }

}
