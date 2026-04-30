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
import java.util.function.Consumer;
import java.util.function.Function;

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
        if (state == null) throw new NullPointerException("No next states");

        threadController.executeLimitedPoolTask(() -> {
            try {
                operationsByStates.get(state).execute(blockContext, this);
            } catch (Exception e) {
                log.error("{} Operation error {}",blockContext.getScenarioId(), e);
                transactionManager.executeInTransaction(transaction -> {
                    blockContext.lockAndLoad(transaction);
                    boolean stateResult = blockContext.setState(ContextState.EXECUTED);
                    if (stateResult) {
                        blockContext.setExecutionStatus(ExecutionStatus.ERROR);
                    }
                    blockContext.save(transaction);
                    transaction.commit();
                    executeState(blockContext);
                });
            }
        });
    }

    public void addOperation(Operation operation) {
        operationsByStates.put(operation.getState(), operation);
    }


    private ContextState getNextState(ContextState currentState) {
        return currentState.getNext();
    }

}
