package com.petra.lib.actor;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableManagerFactory;

import java.util.Iterator;

public class ActorFactory {

    public static LocalProducer localProducer(LocalProducerModel localProducerModel, String currentServiceName,
                                              Sender sender, OperationService operationService, TransactionManager transactionManager,
                                              ThreadController threadController, ContextService contextService) {


        return new LocalProducer(new Identifier(localProducerModel.getId(), localProducerModel.getVersion()),
                remoteConsumer(currentServiceName, localProducerModel.getConsumers().iterator(),
                        sender, threadController, contextService), localProducerModel.getName(),
                VariableManagerFactory.createEndLoaders(localProducerModel, threadController, sender),
                contextService, transactionManager);
    }

    public static RemoteConsumer remoteConsumer(String currentServiceName, Iterator<RemoteConsumerModel> consumerModelIterator,
                                                Sender sender,
                                                ThreadController threadController, ContextService contextService) {
        if (!consumerModelIterator.hasNext()) {
            return null;
        }
        RemoteConsumerModel remoteConsumerModel = consumerModelIterator.next();
        return new RemoteConsumer(remoteConsumerModel,
                VariableManagerFactory.createStartedLoaders(remoteConsumerModel, threadController, sender),
                remoteConsumerModel.getServiceName(),
                remoteConsumer(currentServiceName, consumerModelIterator, sender,
                        threadController, contextService),
                sender);
    }

    public static LocalConsumer localConsumer(LocalConsumerModel localConsumerModel, UserActionHandler userActionHandler) {
        Identifier consumerIdentifier = new Identifier(localConsumerModel.getId(), localConsumerModel.getVersion());
        return new LocalConsumer(consumerIdentifier, BlockType.valueOf(localConsumerModel.getBlockType()),
                localConsumerModel.getName(), localConsumerModel.getInputModels(), localConsumerModel.getOutputModels(),
                userActionHandler);
    }
}
