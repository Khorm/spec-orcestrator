package com.petra.lib.context.block;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.ModelFactory;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.repo.RepoFactory;
import com.petra.lib.context.repo.WorkflowContextRepo;
import com.petra.lib.operation.ActivityOperationService;
import com.petra.lib.operation.OperationConstructor;
import com.petra.lib.operation.WorkflowOperationService;
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
     * @param localProducerModels      коллекция моделей локальных продюсеров, участвующих в блоке (не null)
     * @param consumerModels           коллекция моделей потребителей, участвующих в блоке (может быть null или пустой)
     * @param transactionManager       менеджер транзакций, обеспечивающий согласованность БД операций (не null)
     * @param threadController         контроллер асинхронных операций, управляющий потоками выполнения (не null)
     * @param sender                   компонент для отправки сообщений внешним системам (не null)
     * @param serviceName              имя текущего сервиса, используемое для логирования и маршрутизации (не null)
     * @param userActionHandlerMap     карта обработчиков пользовательских действий, ключ — тип действия (может быть null)
     * @return настроенный экземпляр {@link BlockContextExecutor}, готовый к выполнению блока
     * @throws IllegalArgumentException если любой из обязательных параметров (кроме consumerModels и userActionHandlerMap) равен null
     * @throws RuntimeException         если возникла ошибка при инициализации компонентов внутри исполнителя
     * @implNote Метод является статической фабрикой. Каждый вызов возвращает новый независимый экземпляр.
     * Потокобезопасность результирующего объекта зависит от переданных зависимостей.
     */
    public static BlockContextExecutor createBlockContextExecutor(Collection<LocalProducerModel> localProducerModels,
                                                                  Collection<LocalConsumerModel> consumerModels,
                                                                  TransactionManager transactionManager, ThreadController threadController,
                                                                  Sender sender, String serviceName,
                                                                  Map<String, UserActionHandler> userActionHandlerMap
    ) {

        WorkflowContextRepo workflowContextRepo = RepoFactory.createWorkflowRepo(transactionManager);
        ContextRepo contextRepo = RepoFactory.createBlockRepo(transactionManager);

        Collection<LocalProducer> localProducers = new ArrayList<>();
        for (LocalProducerModel model : localProducerModels) {
            localProducers.add(ModelFactory.localProducer(model, serviceName, sender,
                    workflowContextRepo, contextRepo, threadController));
        }
        WorkflowExecutingOperation workflowExecutingOperation = new WorkflowExecutingOperation(localProducers);

        AnswerOperation answerOperation = new AnswerOperation(sender);

        Map<Identifier, UserActionHandler> userHandlers = new HashMap<>();
        Collection<LocalConsumer> localConsumers = createLocalConsumers(consumerModels);
        for (LocalConsumerModel model : consumerModels) {
            userHandlers.put(new Identifier(model.getId(), model.getVersion()), userActionHandlerMap.get(model.getName()));
        }
        BlockUserOperation blockUserOperation = new BlockUserOperation(transactionManager, userHandlers);

        WorkflowOperationService workflowOperationService = OperationConstructor.createWorkflowOperationService(threadController, workflowExecutingOperation,
                answerOperation);
        ActivityOperationService activityOperationService = OperationConstructor.createBlockOperationService(threadController,
                answerOperation, blockUserOperation, sender, serviceName);
        return new BlockContextExecutor(
                contextRepo, workflowOperationService,activityOperationService, localConsumers, localProducers
        );
    }

    private static Collection<LocalConsumer> createLocalConsumers(Collection<LocalConsumerModel> consumerModels) {
        Collection<LocalConsumer> localConsumers = new ArrayList<>();
        for (LocalConsumerModel model : consumerModels) {
            localConsumers.add(ModelFactory.localConsumer(model));
        }
        return localConsumers;
    }
}
