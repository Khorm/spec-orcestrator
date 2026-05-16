package com.petra.lib.operation.operations;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.TransactionManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FinishOperation implements Operation {

    TransactionManager transactionManager;
    ContextState CURRENT_STATE = ContextState.ANSWERED;

    @Override
    public void execute(Context blockContext, OperationService operationService) {
        transactionManager.executeInTransaction(transaction -> {
            blockContext.lockAndLoad(transaction);
            boolean setStateResult = blockContext.setState(CURRENT_STATE);
            if (setStateResult) {
                blockContext.save(transaction);
                transaction.commit();
            } else {
                transaction.rollback();
            }
            log.info("Finished successfully for scenarioId: {}",
                    blockContext.getScenarioId());
        });

    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
