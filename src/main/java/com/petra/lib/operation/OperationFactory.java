package com.petra.lib.operation;

import com.petra.lib.context.ContextState;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.remote.Sender;
import com.petra.lib.transaction.TransactionManager;

@Deprecated
public class OperationFactory {

//    private WorkflowSendOperation workflowSendOperation;

//    private WorkflowExecutingOperation workflowExecutingOperation;
//
////    private WorkflowAnswerOperation workflowAnswerOperation;
////
//    private BlockUserOperation blockUserOperation;
//
//    private AnswerOperation answerOperation;
//
//    private final Sender sender;
//    private final String currentServiceName;
//
//    private final TransactionManager transactionManager;
//
//    private final OperationService operationService;
//
//
//    public OperationFactory(Sender sender, String currentServiceName,
//                            TransactionManager transactionManager, OperationService operationService) {
//        this.sender = sender;
//        this.currentServiceName = currentServiceName;
//        this.transactionManager = transactionManager;
//        this.operationService = operationService;
//    }
//
//
//    public Operation createOperation(ContextState contextState) {
//        switch (contextState) {
//            case STARTING:
//                return getWorkflowSendOperation();
//            case WORKFLOW_LOADING_VARIABLES:
//                return getValueParsingOperation();
//            case WORKFLOW_EXECUTING:
//                return getWorkflowAnswerOperation();
//            case ACTIVITY_EXECUTING:
//                return getBlockUserOperation();
//            case BLOCK_ANSWERING:
//                return getAnswerOperation();
//            default:
//                throw new IllegalStateException("Wrong state request: " + contextState.name());
//        }
//    }
//
//    private WorkflowSendOperation getWorkflowSendOperation() {
//        if (workflowSendOperation != null) {
//            return workflowSendOperation;
//        }
//
//        workflowSendOperation = new WorkflowSendOperation(sender, currentServiceName);
//        return workflowSendOperation;
//    }
//
//    private WorkflowExecutingOperation getValueParsingOperation() {
//        if (workflowExecutingOperation != null) {
//            return workflowExecutingOperation;
//        }
//
//        workflowExecutingOperation = new WorkflowExecutingOperation(localProducer);
//        return workflowExecutingOperation;
//    }
//
//    private WorkflowAnswerOperation getWorkflowAnswerOperation() {
//        if (workflowAnswerOperation != null) {
//            return workflowAnswerOperation;
//        }
//
//        workflowAnswerOperation = new WorkflowAnswerOperation(transactionManager, operationService);
//        return workflowAnswerOperation;
//    }
//
//
//    private BlockUserOperation getBlockUserOperation() {
//        if (blockUserOperation != null) {
//            return blockUserOperation;
//        }
//
//        blockUserOperation = new BlockUserOperation(transactionManager, userHandlers);
//        return blockUserOperation;
//    }
//
//    private AnswerOperation getAnswerOperation() {
//        if (answerOperation != null) {
//            return answerOperation;
//        }
//
//        answerOperation = new AnswerOperation(sender, currentServiceName);
//        return answerOperation;
//    }


}
