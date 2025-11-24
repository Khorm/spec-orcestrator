package com.petra.lib.context.block;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.operation.OperationService;
import com.petra.lib.context.repo.RepoFactory;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.VariableFactory;
import com.petra.lib.variable.model.ValueModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public final class BlockContextExecutorFactory {
    BlockContextExecutorFactory() {
    }

    public static BlockContextExecutor createBlockContextExecutor(Collection<LocalConsumerModel> consumerModels,
                                                                  TransactionManager transactionManager, ThreadController threadController,
                                                                  Sender sender, String serviceName,
                                                                  Map<String, UserActionHandler> userActionHandlerMap,
                                                                  ) {

        WorkflowExecutingOperation workflowExecutingOperation = new WorkflowExecutingOperation(localProducer);
        AnswerOperation answerOperation = new AnswerOperation(sender, serviceName);
        BlockUserOperation blockUserOperation = new BlockUserOperation(transactionManager, userHandlers);

        OperationService<BlockContext> operationService = new OperationService<>(threadController,
                workflowExecutingOperation, answerOperation, blockUserOperation);

        return new BlockContextExecutor(
                transactionManager,
                RepoFactory.createBlockRepo(transactionManager),
                operationService,
                createLocalConsumers(consumerModels, threadController, sender, userActionHandlerMap),
                producerMap);
    }

    private static Collection<LocalConsumer> createLocalConsumers(Collection<LocalConsumerModel> consumerModels,
                                                                  ThreadController threadController, Sender sender,
                                                                  Map<String, UserActionHandler> userActionHandlerMap) {
        Collection<LocalConsumer> localConsumers = new ArrayList<>();
        for (LocalConsumerModel model : consumerModels) {
            Identifier identifier = new Identifier(model.getId(), model.getVersion());
            LocalConsumer localConsumer = new LocalConsumer(
                    identifier,
                    BlockType.valueOf(model.getBlockType()),
                    model.getName(),
                    VariableFactory.createStartedLoaders(model.getValueLoaders(), model.getValuesCount(), threadController, sender),
                    userActionHandlerMap.get(model.getName()),
                    model.getOuterValues().stream()
                            .map(valueDto -> new ValueModel(valueDto.getId(), valueDto.getName(), valueDto.getMultiplicity(), null))
                            .collect(Collectors.toList())

            );
            localConsumers.add(localConsumer);
        }
        return localConsumers;
    }
}
