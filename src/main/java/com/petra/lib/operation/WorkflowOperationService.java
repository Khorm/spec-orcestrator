package com.petra.lib.operation;

import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.thread.ThreadController;

public class WorkflowOperationService extends OperationService{
    WorkflowOperationService(ThreadController threadController, WorkflowExecutingOperation workflowExecutingOperation,
                             AnswerOperation answerOperation) {
        super(threadController);
        addOperation(workflowExecutingOperation);
        addOperation(answerOperation);
    }
}
