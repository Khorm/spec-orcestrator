package com.petra.lib.actor;

import com.petra.lib.actor.local.activity.LocalActivity;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import com.petra.lib.actor.local.condition.ConditionUserHandler;
import com.petra.lib.actor.local.condition.LocalCondition;
import com.petra.lib.actor.local.producer.LocalProducer;
import com.petra.lib.actor.local.producer.RemoteConsumerLinkedList;
import com.petra.lib.actor.local.source.LocalSource;
import com.petra.lib.actor.local.source.SourceUserHandler;
import com.petra.lib.actor.remote.consumer.RemoteConsumer;
import com.petra.lib.constructor.model.*;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableManagerFactory;

import java.util.Iterator;

public class ActorFactory {


    public static LocalProducer localProducer(LocalProducerModel localProducerModel, String currentServiceName,
                                              Sender sender, TransactionManager transactionManager,
                                              ThreadController threadController, ContextService contextService) {
        RemoteConsumerLinkedList linkedList = new RemoteConsumerLinkedList(remoteConsumer(currentServiceName,
                localProducerModel.getConsumers().iterator(),
                sender, threadController, contextService));
        return new LocalProducer(new Identifier(localProducerModel.getId(), localProducerModel.getVersion()),
                linkedList, localProducerModel.getName(), localProducerModel.getContextValues(),
                VariableManagerFactory.createEndLoaders(localProducerModel, threadController, sender),
                contextService, transactionManager);
    }

    public static LocalActivity localActivity(LocalActivityModel localActivityModel, UserActivityHandler userActivityHandler,
                                              TransactionManager transactionManager) {
        Identifier consumerIdentifier = new Identifier(localActivityModel.getId(), localActivityModel.getVersion());
        return new LocalActivity(consumerIdentifier, BlockType.ACTIVITY,
                localActivityModel.getName(), localActivityModel.getInputModels(), localActivityModel.getOutputModels(),
                userActivityHandler, transactionManager);
    }

    public static LocalCondition createLocalCondition(LocalConditionModel localConditionModel, ConditionUserHandler handler,
                                                      TransactionManager transactionManager){
        Identifier conditionIdentifier = new Identifier(localConditionModel.getId(), localConditionModel.getVersion());
        return new LocalCondition(conditionIdentifier, localConditionModel.getInputModels(), localConditionModel.getName(),
                handler, transactionManager, localConditionModel.getScript());
    }

    public static LocalSource createLocalSource(LocalSourceModel sourceModel, SourceUserHandler sourceUserHandler, TransactionManager transactionManager){
        Identifier id = new Identifier(sourceModel.getId(), sourceModel.getVersion());
        return new LocalSource(id, sourceModel.getName(), sourceModel.getOutputModels(), sourceUserHandler, transactionManager,
                sourceModel.getInputModels());
    }

    private static RemoteConsumer remoteConsumer(String currentServiceName, Iterator<RemoteConsumerModel> consumerModelIterator,
                                                Sender sender,
                                                ThreadController threadController, ContextService contextService) {
        if (!consumerModelIterator.hasNext()) {
            return null;
        }
        RemoteConsumerModel currentRemoteConsumerModel = consumerModelIterator.next();


        return new RemoteConsumer(currentRemoteConsumerModel,
                VariableManagerFactory.createStartedLoaders(currentRemoteConsumerModel, threadController, sender),
                currentRemoteConsumerModel.getServiceName(),
                remoteConsumer(currentServiceName, consumerModelIterator, sender,
                        threadController, contextService),
                sender,  BlockType.valueOf(currentRemoteConsumerModel.getBlockType()));
    }


}
