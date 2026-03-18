package com.petra.lib.operation;

import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.thread.ThreadController;

@Deprecated
class WorkflowOperationService extends OperationService{
    WorkflowOperationService(ThreadController threadController) {
        super(threadController);

    }
}
