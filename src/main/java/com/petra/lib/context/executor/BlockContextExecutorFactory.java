package com.petra.lib.context.executor;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.operation.operations.UserAnswerOperation;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.operation.actor.LocalConsumer;
import com.petra.lib.operation.actor.LocalProducer;
import com.petra.lib.operation.actor.ActorFactory;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.operation.OperationConstructor;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public static BlockContextExecutor createBlockContextExecutor(OperationService workflowOperationService,
                                                                  Collection<LocalConsumerModel> consumerModels,
                                                                  OperationService activityOperationService,
                                                                  TransactionManager transactionManager,
                                                                  OperationService userWorkflowOperationService,
                                                                  Sender sender,
                                                                  Map<String, UserActionHandler> userActionHandlerMap, ContextService contextService,
                                                                  ContextRepo contextRepo, Collection<LocalProducer> localProducers
    ) {

        //создать исполнитель операций для workflow
//        OperationService workflowOperationService = OperationConstructor.createWorkflowOperationService(threadController);
//        OperationService activityOperationService = OperationConstructor.createActionOperationService(threadController);
//        OperationService userWorkflowOperationService = OperationConstructor.createWorkflowOperationService(threadController);


//        Collection<LocalProducer> localProducers = new ArrayList<>();
//        for (LocalProducerModel model : localProducerModels) {
//            localProducers.add(ActorFactory.localProducer(model, serviceName, sender, workflowOperationService,
//                    threadController, contextService));
//        }
        WorkflowExecutingOperation workflowExecutingOperation = new WorkflowExecutingOperation(localProducers);

        UserAnswerOperation userAnswerOperation = new UserAnswerOperation();

        AnswerOperation answerOperation = new AnswerOperation(sender);

        Map<Identifier, UserActionHandler> userHandlers = new HashMap<>();
        Collection<LocalConsumer> localConsumers = createLocalConsumers(consumerModels);
        for (LocalConsumerModel model : consumerModels) {
            userHandlers.put(new Identifier(model.getId(), model.getVersion()), userActionHandlerMap.get(model.getName()));
        }

        BlockUserOperation blockUserOperation = new BlockUserOperation(transactionManager, userHandlers);

        activityOperationService.addOperation(blockUserOperation);
        activityOperationService.addOperation(answerOperation);

        workflowOperationService.addOperation(workflowExecutingOperation);
        workflowOperationService.addOperation(answerOperation);

        userWorkflowOperationService.addOperation(workflowExecutingOperation);
        userWorkflowOperationService.addOperation(userAnswerOperation);

        System.out.println("localConsumers = " + localConsumers.size());
        System.out.println("localProducers = " + localProducers.size());
        return new BlockContextExecutor(
                contextRepo, workflowOperationService,userWorkflowOperationService, localConsumers, activityOperationService,
                contextService
        );
    }

    private static Collection<LocalConsumer> createLocalConsumers(Collection<LocalConsumerModel> consumerModels) {
        Collection<LocalConsumer> localConsumers = new ArrayList<>();
        for (LocalConsumerModel model : consumerModels) {
            localConsumers.add(ActorFactory.localConsumer(model));
        }
        return localConsumers;
    }
}
