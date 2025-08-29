package com.petra.lib.context.block;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.context.block.operations.AnswerOperation;
import com.petra.lib.context.block.operations.ValueParsingOperation;
import com.petra.lib.context.block.operations.executor.BlockUserOperation;
import com.petra.lib.context.block.operations.executor.handler.UserActionHandler;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.operation.OperationServiceImpl;
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
                                                                  Map<String, UserActionHandler> userActionHandlerMap) {

        ValueParsingOperation valueParsingOperation = new ValueParsingOperation();
        AnswerOperation answerOperation = new AnswerOperation(sender, serviceName);
        BlockUserOperation blockUserOperation = new BlockUserOperation(transactionManager);

        OperationServiceImpl<BlockContext> operationService = new OperationServiceImpl<>(threadController,
                valueParsingOperation, answerOperation, blockUserOperation);

        return new BlockContextExecutor(
                transactionManager,
                RepoFactory.createBlockRepo(transactionManager),
                operationService,
                createLocalConsumers(consumerModels, threadController, sender, userActionHandlerMap)
        );
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
