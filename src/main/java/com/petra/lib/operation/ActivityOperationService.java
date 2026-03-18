package com.petra.lib.operation;

import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.thread.ThreadController;

@Deprecated
class ActivityOperationService extends OperationService{
    ActivityOperationService(ThreadController threadController, BlockUserOperation blockUserOperation, AnswerOperation answerOperation) {
        super(threadController);
        addOperation(blockUserOperation);
        addOperation(answerOperation);
    }
}
