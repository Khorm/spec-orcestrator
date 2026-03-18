package com.petra.lib.context.model;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.block.ContextService;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.repo.WorkflowContextRepo;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.VariableFactory;

import java.util.Iterator;

public class ModelFactory {

    public static LocalProducer localProducer(LocalProducerModel localProducerModel, String currentServiceName,
                                              Sender sender, OperationService operationService,
                                              ThreadController threadController, ContextService contextService) {
        return new LocalProducer(new Identifier(localProducerModel.getId(), localProducerModel.getVersion()),
                remoteConsumer(currentServiceName, localProducerModel.getConsumers().iterator(),
                        sender,operationService,  threadController, contextService),
                VariableFactory.createStartedLoaders(localProducerModel.getExitValues(), threadController, sender),
                contextService, operationService);
    }

    public static RemoteConsumer remoteConsumer(String currentServiceName, Iterator<RemoteConsumerModel> consumerModelIterator,
                                                Sender sender, OperationService operationService,
                                                ThreadController threadController, ContextService contextService) {
        if (!consumerModelIterator.hasNext()) {
            return null;
        }
        RemoteConsumerModel remoteConsumerModel = consumerModelIterator.next();
        return new RemoteConsumer(remoteConsumerModel,
                VariableFactory.createStartedLoaders(remoteConsumerModel.getValues(), threadController, sender),
                remoteConsumerModel.getServiceName(),
                remoteConsumer(currentServiceName, consumerModelIterator, sender, operationService,
                        threadController, contextService),
                sender,contextService, operationService);
    }

    public static LocalConsumer localConsumer(LocalConsumerModel localConsumerModel) {
        Identifier consumerIdentifier = new Identifier(localConsumerModel.getId(), localConsumerModel.getVersion());
        return new LocalConsumer(consumerIdentifier, BlockType.valueOf(localConsumerModel.getBlockType()),
                localConsumerModel.getName(), localConsumerModel.getInputModels(), localConsumerModel.getOutputModels());
    }
}
