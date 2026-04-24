package com.petra.lib.operation.operations;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAnswerOperation implements Operation {
    ContextState CURRENT_STATE = ContextState.ANSWERED;
    TransactionManager transactionManager;

    @Override
    public void execute(Context blockContext, OperationService operationService) {
        try(Transaction tx = transactionManager.createNewTransaction(false, null)) {
            blockContext.load(tx);
            boolean setStateResult = blockContext.setState(CURRENT_STATE);
            if (setStateResult) {
                blockContext.save(tx);
            } else {
                tx.rollback();
                return;
            }
            log.info("User operation executed {} for scenarioId: {}, blockId: {}", blockContext.getExecutionStatus(),
                    blockContext.getScenarioId(), blockContext.getCurrentBlockId().toString());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
