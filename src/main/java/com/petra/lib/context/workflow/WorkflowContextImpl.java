package com.petra.lib.context.workflow;

@Deprecated
class WorkflowContextImpl /*implements WorkflowContext*/ {
//    private final LocalProducer localProducer;
////    private final RemoteConsumer remoteConsumer;
//    private final WorkflowContextEntity workflowContextEntity;
//    private final WorkflowContextRepo workflowContextRepo;
//    private final ValueContainer workflowInputValues;
//    private final ValueContainer workflowOutputValues;
//    private final OperationService<WorkflowContext> workflowOperationService;
//    private final TransactionManager transactionManager;
//    private final WorkflowContextExecutor workflowContextExecutor;
//    private final ContextState[] states = {ContextState.BLOCK_CREATING, ContextState.STARTED, ContextState.BLOCK_ANSWERING};
//
//
//    public WorkflowContextImpl(LocalProducer localProducer, RemoteConsumer remoteConsumer, WorkflowContextEntity workflowContextEntity,
//                               WorkflowContextRepo workflowContextRepo, ValueContainer workflowInputValues, ValueContainer workflowOutputValues,
//                               OperationService<WorkflowContext> workflowOperationService, TransactionManager transactionManager, WorkflowContextExecutor workflowContextExecutor) {
//        this.localProducer = localProducer;
//        this.remoteConsumer = remoteConsumer;
//        this.workflowContextEntity = workflowContextEntity;
//        this.workflowContextRepo = workflowContextRepo;
//        this.workflowInputValues = workflowInputValues;
//        this.workflowOutputValues = workflowOutputValues;
//        this.workflowOperationService = workflowOperationService;
//        this.transactionManager = transactionManager;
//        this.workflowContextExecutor = workflowContextExecutor;
//    }
//
//    public synchronized void run() {
//        if (workflowContextEntity.getWorkflowState() == ContextState.BLOCK_CREATING) {
//            Optional<WorkflowContextEntity> anotherContext
//                    = workflowContextRepo.getWorkflowContext(localProducer, remoteConsumer, workflowContextEntity.getScenarioId());
//            if (anotherContext.isEmpty()) {
//                workflowContextRepo.insertContext(workflowContextEntity);
//            } else {
//                stop();
//                return;
//            }
//        }
//        workflowOperationService.executeState(this, getNextState(workflowContextEntity.getWorkflowState()));
//    }
//
//
//    @Override
//    public UUID getScenarioId() {
//        return workflowContextEntity.getScenarioId();
//    }
//
//    @Override
//    public synchronized void setState(ValueContainer values, ContextState executedState) {
//        AtomicBoolean isNextStateAvailable = new AtomicBoolean(true);
//        transactionManager.executeInTransaction(transaction -> {
//            ContextState contextState = workflowContextRepo.getState();
//            if (getNextState(contextState) != executedState) {
//                isNextStateAvailable.set(false);
//                stop();
//                return;
//            }
//
//            if (values != null) {
//                workflowContextEntity.setResultValues(values);
//            }
//            workflowContextEntity.setWorkflowState(executedState);
//            workflowContextRepo.updateContext(workflowContextEntity);
//
//            if (executedState == ContextState.ACTIVITY_EXECUTING) {
//                isNextStateAvailable.set(false);
//            }
//        }, Isolation.SERIALIZABLE);
//
//        if (!isNextStateAvailable.get()) return;
//        workflowOperationService.executeState(this, getNextState(executedState));
//    }
//
//    @Override
//    public void saveError(Exception e) {
//        e.printStackTrace();
//        stop();
//    }
//
//    @Override
//    public LocalProducer getLocalProducer() {
//        return localProducer;
//    }
//
////    @Override
////    public RemoteConsumer getRemoteConsumer() {
////        return remoteConsumer;
////    }
//
//    @Override
//    public ValueContainer getWorkflowInputValues() {
//        return workflowInputValues;
//    }
//
//    @Override
//    public ValueContainer getWorkflowOutputValues() {
//        return workflowOutputValues;
//    }
//
//    @Override
//    public Optional<RemoteConsumer> getNextConsumer() {
//        return localProducer.getNextConsumer(remoteConsumer);
//    }
//
//    @Override
//    public ValueContextModel getLastWorkflowBlockOuterParser() {
//        return localProducer.getLastWorkflowBlockOuterParser();
//    }
//
////    @Override
////    public WorkflowContextExecutor getExecutor() {
////        return workflowContextExecutor;
////    }
////
////    private ContextState getNextState(ContextState currentState) {
////        if (currentState == ContextState.BLOCK_ANSWERING) {
////            return null;
////        }
////
////        for (int i = 0; i < states.length; i++) {
////            if (states[i] == currentState) {
////                return states[i+1];
////            }
////        }
////        throw new NullPointerException();
////    }
//
//    private void stop() {
//        System.out.println("STOP WORKFLOW " + localProducer.getName());
//    }
}
