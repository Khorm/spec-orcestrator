package com.petra.lib.source;

@Deprecated
class Source {
    asdasd

//    private final Identifier sourceId;
//    private final TransactionManager transactionManager;
//    private final UserActionHandler userActionHandler;
//
//    public Source(Identifier sourceId, TransactionManager transactionManager, UserActionHandler userActionHandler) {
//        this.sourceId = sourceId;
//        this.transactionManager = transactionManager;
//        this.userActionHandler = userActionHandler;
//    }
//
//
//    public SourceResponseDto execute(SourceRequestDto sourceRequestDto) {
//        try {
//            ActionContext actionContext = new ActionContext(
//                    sourceRequestDto.getScenarioId(),
//                    sourceId,
//                    sourceRequestDto.getProducerServiceUrl(),
//                    new Identifier(sourceRequestDto.getProducerBlockId(), sourceRequestDto.getProducerBlockVersion()),
//                    null,
//                    sourceRequestDto.getProducerValues()
//            );
//
//            transactionManager.executeInTransaction(transaction -> {
//                EntityManager entityManager = EntityManagerFactoryUtils
//                        .getTransactionalEntityManager(Objects.requireNonNull(transaction.getEntityManagerFactory()));
//                UserActionContextImpl userContext = new UserActionContextImpl(entityManager, actionContext);
//                userActionHandler.execute(userContext);
//            }, Isolation.REPEATABLE_READ);
//
//            return new SourceResponseDto(sourceRequestDto.getScenarioId(),
//                    sourceId.getId(), sourceId.getVersion(), ExecutionStatus.OK, actionContext.getConsumerValuesJson(),
//                    sourceRequestDto.getProducerBlockId(), sourceRequestDto.getProducerBlockVersion());
//        } catch (Exception e) {
//            return new SourceResponseDto(sourceRequestDto.getScenarioId(),
//                    sourceId.getId(), sourceId.getVersion(), ExecutionStatus.ERROR, null,
//                    sourceRequestDto.getProducerBlockId(), sourceRequestDto.getProducerBlockVersion());
//        }
//    }
//
//
//    @Override
//    public void executeNext(ActionContext actionContext, BlockManager executedManager) {
//
//    }
//
//    @Override
//    public void executeNext(UUID scenarioId, BlockManager executedManager) {
//
//    }
//
//    @Override
//    public void error(Exception e, UUID scenarioId) {
//
//    }
//
//    @Override
//    public void error(Exception e, ActionContext actionContext) {
//
//    }
}
