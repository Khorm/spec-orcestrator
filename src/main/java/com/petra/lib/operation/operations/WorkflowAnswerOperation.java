package com.petra.lib.operation.operations;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContext;
import org.springframework.transaction.annotation.Isolation;

@Deprecated
class WorkflowAnswerOperation /*implements Operation*/ {
//    private final TransactionManager transactionManager;
//    private final ContextState CURRENT_STATE = ContextState.WORKFLOW_EXECUTING;
//
//    private final OperationService operationService;
//
//    public WorkflowAnswerOperation(TransactionManager transactionManager, OperationService operationService) {
//        this.transactionManager = transactionManager;
//        this.operationService = operationService;
//    }
//
//    @Override
//    public void execute(Context fullContext) {
//        WorkflowContext context = fullContext.getWorkflowContext();
//        transactionManager.executeInTransaction(transaction -> {
//            context.setState(context.getWorkflowOutputValues(), CURRENT_STATE);
//
//            if (context.getNextConsumer().isPresent()) {
//                //запустить следующий блок в воркфлоу
//                context.getExecutor().loadContext(context);
//            } else {
//                ValueContext valueContext = new ValueContext(context.getWorkflowOutputValues(),
//                        context.getLastWorkflowBlockOuterParser(), context.getScenarioId(), new VariableCallback() {
//                    @Override
//                    public void loaded(ValueContainer loadedValues) {
//                        blockContextExecutor.loadContext(context.getScenarioId(),
//                                context.getRemoteConsumer().getIdentifier(), loadedValues);
//                    }
//
//                    @Override
//                    public void error(Exception e) {
//                        context.error(e);
//                    }
//                });
//                valueContext.start();
//
//            }
//
//        }, Isolation.SERIALIZABLE);
//    }
//
//
//    @Override
//    public ContextState getState() {
//        return CURRENT_STATE;
//    }
}
