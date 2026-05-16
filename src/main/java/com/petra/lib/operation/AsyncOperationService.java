package com.petra.lib.operation;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.BlockExecutingOperation;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * Отвечает за вызов операций переключающий стейты
 */

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class AsyncOperationService implements OperationService {
    Map<ContextState, Operation> operationsByStates;
    ThreadController threadController;
    TransactionManager transactionManager;

    public AsyncOperationService(ThreadController threadController, TransactionManager transactionManager, Operation ... operations) {
        this.threadController = threadController;
        this.transactionManager = transactionManager;
        operationsByStates = new HashMap<>();
        for (Operation op : operations){
            operationsByStates.put(op.getState(), op);
        }
    }

    public void executeState(Context blockContext) {
        executeNextState(blockContext, getNextState(blockContext.getCurrentState()));
    }

    private synchronized void executeNextState(Context blockContext, ContextState state) {
        if (state == null) throw new NullPointerException("No next states");

        threadController.executeLimitedPoolTask(() -> {
            try {
                operationsByStates.get(state).execute(blockContext, this);
            } catch (Exception e) {
                log.error("{} Operation error {}", blockContext.getScenarioId(), e);
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

//    public void addOperation(Operation operation) {
//        operationsByStates.put(operation.getState(), operation);
//    }


    private ContextState getNextState(ContextState currentState) {
        return currentState.getNext();
    }

}
