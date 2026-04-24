package com.petra.lib.operation;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * Отвечает за вызов операций переключающий стейты
 */
@RequiredArgsConstructor
@Log4j2
public class OperationService {
    private final Map<ContextState, Operation> operationsByStates = new HashMap<>();
    private final ThreadController threadController;
    private final TransactionManager transactionManager;


    public void executeState(Context blockContext) {
        executeState(blockContext, getNextState(blockContext.getCurrentState()));
    }

    private synchronized void executeState(Context blockContext, ContextState state) {
        if (state == null) return;
        threadController.executeUnlimitedPoolTask(() -> {
            try {
                operationsByStates.get(state).execute(blockContext, this);
            } catch (Exception e) {
                log.error("{} Operation error {}",blockContext.getScenarioId(), e);
                try (Transaction transaction = transactionManager.createNewTransaction(false, null)) {
                    blockContext.lockAndLoad(transaction);
                    boolean stateResult = blockContext.setState(ContextState.EXECUTED);
                    if (stateResult) {
                        blockContext.setExecutionStatus(ExecutionStatus.ERROR);
                    }
                    blockContext.save(transaction);
                    transaction.commit();
                    executeState(blockContext);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void addOperation(Operation operation) {
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
