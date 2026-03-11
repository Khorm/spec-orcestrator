package com.petra.lib.context.model;

import com.petra.lib.constructor.model.LocalConsumerModel;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.repo.WorkflowContextRepo;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.VariableFactory;

import java.util.Iterator;

public class ModelFactory {

    public static LocalProducer localProducer(LocalProducerModel localProducerModel, String currentServiceName, Sender sender,
                                              WorkflowContextRepo workflowContextRepo, ContextRepo contextRepo,
                                              ThreadController threadController) {
        return new LocalProducer(new Identifier(localProducerModel.getId(), localProducerModel.getVersion()),
                remoteConsumer(currentServiceName, localProducerModel.getConsumers().iterator(),
                        sender, workflowContextRepo, contextRepo, threadController),
                contextRepo, VariableFactory.createStartedLoaders(localProducerModel.getExitValues(), threadController, sender));
    }

    public static RemoteConsumer remoteConsumer(String currentServiceName, Iterator<RemoteConsumerModel> consumerModelIterator, Sender sender,
                                                WorkflowContextRepo workflowContextRepo, ContextRepo contextRepo,
                                                ThreadController threadController) {
        if (!consumerModelIterator.hasNext()) {
            return null;
        }
        RemoteConsumerModel remoteConsumerModel = consumerModelIterator.next();
        return new RemoteConsumer(remoteConsumerModel,
                VariableFactory.createStartedLoaders(remoteConsumerModel.getValues(), threadController, sender),
                remoteConsumerModel.getServiceName(),
                remoteConsumer(currentServiceName, consumerModelIterator, sender, workflowContextRepo,
                        contextRepo, threadController),
                sender, workflowContextRepo);
    }

    public static LocalConsumer localConsumer(LocalConsumerModel localConsumerModel) {
        Identifier consumerIdentifier = new Identifier(localConsumerModel.getId(), localConsumerModel.getVersion());
        return new LocalConsumer(consumerIdentifier, BlockType.valueOf(localConsumerModel.getBlockType()),
                localConsumerModel.getName(), localConsumerModel.getInputModels(), localConsumerModel.getOutputModels());
    }
}
