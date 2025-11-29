package com.petra.lib.operation.operations;

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
