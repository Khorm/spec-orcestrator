package com.petra.lib.operation.operations;

@Deprecated
public class BlockExecutionOperation /*implements Operation*/ {

//    private final WorkflowContextRepo workflowContextRepo;
//
//    @Override
//    public void execute(Context blockContext) {
//        WorkflowContext workflowContext = blockContext.getWorkflowContext();
//        LocalProducer localProducer = workflowProducers.get(producerId);
//        Collection<WorkflowContextEntity> workflowContexts = workflowContextRepo.getWorkflowContexts(localProducer, scenarioId);
//        Map<Identifier, WorkflowContextEntity> contextsByConsumers = workflowContexts.stream()
//                .collect(Collectors.toMap(WorkflowContextEntity::getConsumerId, Function.identity()));
//
//        WorkflowContextEntity contextEntity = null;
//        WorkflowContextEntity previousContextEntity = null;
//        RemoteConsumer contextRemoteConsumer = null;
//        for (RemoteConsumer remoteConsumer : localProducer.getWorkflowConsumers()) {
//
//            //находим недовыполненный контекст
//            if (contextsByConsumers.containsKey(remoteConsumer.getIdentifier())) {
//                previousContextEntity = contextEntity;
//                contextEntity = contextsByConsumers.get(remoteConsumer.getIdentifier());
//                if (contextEntity.getWorkflowState() != ContextState.BLOCK_ANSWERING) {
//                    contextRemoteConsumer = remoteConsumer;
//                    break;
//                }
//            } else {
//                //находим что предыдущий контекст выполнен или не существует, а следующейго еще не существует.
//                previousContextEntity = contextEntity;
//                contextEntity = createContextEntity(remoteConsumer, localProducer, scenarioId);
//                contextRemoteConsumer = remoteConsumer;
//                break;
//            }
//        }
//
//        if (contextEntity == null)
//            throw new NullPointerException("No context found for workflow " + localProducer.getName());
//
//        //берет либо исходящие знаечения последнего контекст, либо входщие
//        ValueContainer contextInputValues = previousContextEntity != null ?
//                previousContextEntity.getResultValues() : inputWorkflowValues;
//        new WorkflowContextImpl(
//                localProducer,
//                contextRemoteConsumer,
//                contextEntity,
//                workflowContextRepo,
//                contextInputValues,
//                outputWorkflowValues,
//                workflowOperationService,
//                transactionManager,
//                this).run();
//    }
//
//    @Override
//    public ContextState getState() {
//        return ;
//    }
}
