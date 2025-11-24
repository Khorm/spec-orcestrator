package com.petra.lib.operation;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;

import java.util.Map;

public final class OperationConstructor {

    public static OperationService createWorkflowOperationService(ThreadController threadController, Sender sender,
                                                                  LocalProducer localProducer) {
        OperationService operationService = new OperationService(threadController);
        operationService.addOperation(null, ContextState.STARTED);
        operationService.addOperation(new WorkflowExecutingOperation(localProducer), ContextState.EXECUTED);
        operationService.addOperation(new AnswerOperation(sender), ContextState.ANSWERED);
        return operationService;
    }

    public static OperationService createBlockOperationService(ThreadController threadController, Sender sender,
                                                               TransactionManager transactionManager,
                                                               Map<Identifier, UserActionHandler> userHandlers){
        OperationService operationService = new OperationService(threadController);
        operationService.addOperation(null, ContextState.STARTED);
        operationService.addOperation(new BlockUserOperation(transactionManager, userHandlers), ContextState.EXECUTED);
        operationService.addOperation(new AnswerOperation(sender), ContextState.ANSWERED);
        return operationService;
    }
}
