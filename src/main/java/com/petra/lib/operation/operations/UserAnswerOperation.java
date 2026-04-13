package com.petra.lib.operation.operations;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UserAnswerOperation implements Operation {
    private final ContextState CURRENT_STATE = ContextState.ANSWERED;


    @Override
    public void execute(Context blockContext, OperationService operationService) {
        blockContext.lockAndLoad();
        boolean setStateResult = blockContext.setState(CURRENT_STATE);
        if (setStateResult){
            blockContext.unlockAndSave();
        }else {
            blockContext.unlockAndDiscard();
        }
        log.info("User operation executed successfully for scenarioId: {}, blockId: {}",
                blockContext.getScenarioId(), blockContext.getCurrentBlockId().toString());
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
