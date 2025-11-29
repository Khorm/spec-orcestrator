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

    public static LocalProducer localProducer(LocalProducerModel localConsumerModel, String currentServiceName, Sender sender,
                                              WorkflowContextRepo workflowContextRepo, ContextRepo contextRepo,
                                              ThreadController threadController) {
        return new LocalProducer(new Identifier(localConsumerModel.getId(), localConsumerModel.getVersion()),
                remoteConsumer(currentServiceName, localConsumerModel.getConsumers().iterator(),
                        sender, workflowContextRepo, contextRepo, threadController),
                contextRepo, VariableFactory.createStartedLoaders(localConsumerModel.getExitValues(), threadController, sender));
    }

    public static RemoteConsumer remoteConsumer(String currentServiceName, Iterator<RemoteConsumerModel> consumerModelIterator, Sender sender,
                                                WorkflowContextRepo workflowContextRepo, ContextRepo contextRepo,
                                                ThreadController threadController) {
        if (!consumerModelIterator.hasNext()) {
            return null;
        }
        RemoteConsumerModel remoteConsumerModel = consumerModelIterator.next();
        return new RemoteConsumer(remoteConsumerModel,
                VariableFactory.createStartedLoaders(remoteConsumerModel.getBlockValues(), threadController, sender),
                currentServiceName,
                remoteConsumer(currentServiceName, consumerModelIterator, sender, workflowContextRepo,
                        contextRepo, threadController),
                sender, workflowContextRepo);
    }

    public static LocalConsumer localConsumer(LocalConsumerModel localConsumerModel) {
        ConsumerIdentifier consumerIdentifier = new ConsumerIdentifier(localConsumerModel.getId(), localConsumerModel.getVersion(),
                localConsumerModel.getWorkflowId(), localConsumerModel.getWorkflowVersion());
        return new LocalConsumer(consumerIdentifier, BlockType.valueOf(localConsumerModel.getBlockType()),
                localConsumerModel.getName());
    }
}
