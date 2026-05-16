package com.petra.lib.executor;

@Deprecated
public final class BlockContextExecutorFactory {
    BlockContextExecutorFactory() {
    }


    /**
     * Создаёт и настраивает экземпляр {@link BlockContextExecutor} с необходимыми зависимостями.
     * <p>
     * Метод инициализирует исполнитель контекста блока, передавая ему все компоненты, требуемые для:
     * - обработки локальных продюсеров,
     * - выполнения операций на уровне блока и всего workflow,
     * - взаимодействия с репозиториями контекстов,
     * - управления асинхронными потоками,
     * - отправки сообщений,
     * - обработки пользовательских действий.
     * </p>
     *
     * @param consumerModels       коллекция моделей потребителей, участвующих в блоке (может быть null или пустой)
     * @param transactionManager   менеджер транзакций, обеспечивающий согласованность БД операций (не null)
     * @param sender               компонент для отправки сообщений внешним системам (не null)
     * @param userActionHandlerMap карта обработчиков пользовательских действий, ключ — тип действия (может быть null)
     * @return настроенный экземпляр {@link BlockContextExecutor}, готовый к выполнению блока
     * @throws IllegalArgumentException если любой из обязательных параметров (кроме consumerModels и userActionHandlerMap) равен null
     * @throws RuntimeException         если возникла ошибка при инициализации компонентов внутри исполнителя
     * @implNote Метод является статической фабрикой. Каждый вызов возвращает новый независимый экземпляр.
     * Потокобезопасность результирующего объекта зависит от переданных зависимостей.
     */
//    public static BlockContextExecutor createBlockContextExecutor(ConsumerCollection consumerCollection) {

//        WorkflowExecutingOperation workflowExecutingOperation = new WorkflowExecutingOperation(localProducers);
//
//        UserAnswerOperation userAnswerOperation = new UserAnswerOperation(transactionManager);
//
//        AnswerOperation answerOperation = new AnswerOperation(sender, transactionManager);

//        Map<Identifier, LocalActivity> userHandlers = new HashMap<>();
//        Collection<LocalConsumer> localConsumers = createLocalConsumers(consumerModels, userActionHandlerMap, transactionManager);
//        for (LocalActivity model : localConsumers) {
//            userHandlers.put(model.getIdentifier(), model);
//        }

//        BlockUserOperation blockUserOperation = new BlockUserOperation(transactionManager, userHandlers);
//
//        activityOperationService.addOperation(blockUserOperation);
//        activityOperationService.addOperation(answerOperation);
//
//        workflowOperationService.addOperation(workflowExecutingOperation);
//        workflowOperationService.addOperation(answerOperation);
//
//        userWorkflowOperationService.addOperation(workflowExecutingOperation);
//        userWorkflowOperationService.addOperation(userAnswerOperation);

//        System.out.println("localConsumers = " + localConsumers.size());
//        System.out.println("localProducers = " + localProducers.size());
//        ConsumerCollection consumerCollection = new ConsumerCollection(localConsumers);
//        return new BlockContextExecutor(
//                workflowOperationService, consumerCollection, userWorkflowOperationService, contextService,
//                activityOperationService
//        );
//    }

//    private static Collection<LocalActivity> createLocalConsumers(Collection<LocalConsumerModel> consumerModels,
//                                                                  Map<String, UserActivityHandler> userActionHandlerMap, TransactionManager manager) {
//        Collection<LocalActivity> localConsumers = new ArrayList<>();
//        for (LocalConsumerModel model : consumerModels) {
//            localConsumers.add(ActorFactory.localConsumer(model, userActionHandlerMap.get(model.getName()), manager));
//        }
//        return localConsumers;
//    }
}
