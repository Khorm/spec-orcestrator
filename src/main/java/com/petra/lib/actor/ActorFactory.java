package com.petra.lib.actor;

import com.petra.lib.actor.activity.RemoteActivity;
import com.petra.lib.actor.local.producer.LocalProducer;
import com.petra.lib.actor.remote.consumer.RemoteCondition;
import com.petra.lib.actor.remote.consumer.RemoteConsumer;
import com.petra.lib.actor.remote.consumer.RemoteEnd;
import com.petra.lib.actor.remote.consumer.RemoteStart;
import com.petra.lib.actor.workflow.RemoteWorkflow;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableManagerFactory;
import com.petra.lib.variable.context.ValueContextManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ActorFactory {


    public static LocalProducer localProducer(LocalProducerModel localProducerModel, String currentServiceName,
                                              Sender sender, TransactionManager transactionManager, OperationService operationService,
                                              ThreadController threadController, ContextService contextService) {

        return new LocalProducer(localProducerModel.geId(),
                localProducerModel.getName(), localProducerModel.getInputVariables(),
                createRemoteConsumer(currentServiceName,
                        localProducerModel.getConsumers(),
                        sender,
                        localProducerModel.findFirst(),
                        operationService,
                        threadController,
                        contextService,
                        transactionManager
                ),
                contextService, transactionManager);
    }

    //
//    public static LocalActivity localActivity(LocalActivityModel localActivityModel, UserActivityHandler userActivityHandler,
//                                              TransactionManager transactionManager) {
//        Identifier consumerIdentifier = new Identifier(localActivityModel.getId(), localActivityModel.getVersion());
//        return new LocalActivity(consumerIdentifier, BlockType.ACTIVITY,
//                localActivityModel.getName(), localActivityModel.getInputModels(), localActivityModel.getOutputModels(),
//                userActivityHandler, transactionManager);
//    }
//
//    public static LocalCondition createLocalCondition(LocalConditionModel localConditionModel, ConditionUserHandler handler,
//                                                      TransactionManager transactionManager){
//        Identifier conditionIdentifier = new Identifier(localConditionModel.getId(), localConditionModel.getVersion());
//        return new LocalCondition(conditionIdentifier, localConditionModel.getInputModels(), localConditionModel.getName(),
//                handler, transactionManager, localConditionModel.getScript());
//    }
//
//    public static LocalSource createLocalSource(LocalSourceModel sourceModel, SourceUserHandler sourceUserHandler, TransactionManager transactionManager){
//        Identifier id = new Identifier(sourceModel.getId(), sourceModel.getVersion());
//        return new LocalSource(id, sourceModel.getName(), sourceModel.getOutputModels(), sourceUserHandler, transactionManager,
//                sourceModel.getInputModels());
//    }
    private static RemoteConsumer createRemoteConsumer(String currentServiceName, Collection<RemoteConsumerModel> consumerModelIterator,
                                                       Sender sender, RemoteConsumerModel consumerModel, OperationService operationService,
                                                       ThreadController threadController, ContextService contextService, TransactionManager transactionManager) {
        if (consumerModel == null) {
            return null;
        }

        switch (consumerModel.getScenarioBlockType()) {
            case START:
                return new RemoteStart(createRemoteConsumer(currentServiceName, consumerModelIterator, sender, getNext(consumerModelIterator, consumerModel),
                        operationService, threadController, contextService, transactionManager));
            case SOURCE:
                throw new IllegalStateException("Not implemented ");
            case ACTIVITY:
                ValueContextManager activityContextManager = VariableManagerFactory.createLoader(threadController, sender,
                        consumerModel.getLoadValues(), consumerModel.getContextValues(), consumerModel.getConsumerName());
                ConsumerIdentifier identifier = new ConsumerIdentifier(consumerModel.getId().getId(), consumerModel.getId().getVersion(),
                        consumerModel.getWorkflowId(), consumerModel.getWorkflowVersion());
                return new RemoteActivity(consumerModel.getConsumerName(), activityContextManager,
                        currentServiceName, sender, identifier, consumerModel.getServiceName(),
                        createRemoteConsumer(currentServiceName, consumerModelIterator, sender, getNext(consumerModelIterator, consumerModel),
                                operationService, threadController, contextService, transactionManager),
                        transactionManager, contextService);
            case WORKFLOW:
                ValueContextManager workflowContextManager = VariableManagerFactory.createLoader(threadController, sender,
                        consumerModel.getLoadValues(), consumerModel.getContextValues(), consumerModel.getConsumerName());
                ConsumerIdentifier workflowIdentifier = new ConsumerIdentifier(consumerModel.getId().getId(), consumerModel.getId().getVersion(),
                        consumerModel.getWorkflowId(), consumerModel.getWorkflowVersion());
                return new RemoteWorkflow(consumerModel.getConsumerName(), workflowContextManager,
                        currentServiceName, sender, workflowIdentifier, consumerModel.getServiceName(),
                        createRemoteConsumer(currentServiceName, consumerModelIterator, sender, getNext(consumerModelIterator, consumerModel),
                                operationService, threadController, contextService, transactionManager),
                        transactionManager, contextService);

            case CONDITION:
                ValueContextManager conditionContextManager = VariableManagerFactory.createLoader(threadController, sender,
                        consumerModel.getLoadValues(), consumerModel.getContextValues(), consumerModel.getConsumerName());
                ConsumerIdentifier conditionIdentifier = new ConsumerIdentifier(consumerModel.getId().getId(), consumerModel.getVersion(),
                        consumerModel.getWorkflowId(), consumerModel.getWorkflowVersion());
                Map<Integer, RemoteConsumerModel> nextForCondition = getNextForCondition(consumerModelIterator, getNext(consumerModelIterator, consumerModel));
                Map<Integer, RemoteConsumer> nextConsumers = new HashMap<>();
                for (Map.Entry<Integer, RemoteConsumerModel> entry : nextForCondition.entrySet()) {
                    nextConsumers.put(entry.getKey(), createRemoteConsumer(currentServiceName, consumerModelIterator,
                            sender, entry.getValue(), operationService, threadController, contextService, transactionManager));
                }

                return new RemoteCondition(consumerModel.getConsumerName(), conditionContextManager, currentServiceName,
                        sender, conditionIdentifier, consumerModel.getServiceName(), contextService,
                        nextConsumers);

            case END:
                ValueContextManager endContextManager = VariableManagerFactory.createLoader(threadController, sender,
                        consumerModel.getLoadValues(), consumerModel.getContextValues(), consumerModel.getConsumerName());
                Identifier endId = new Identifier(consumerModel.getWorkflowId(), consumerModel.getWorkflowVersion());
                return new RemoteEnd(endContextManager, contextService, endId, consumerModel.getConsumerName(),
                        transactionManager, operationService);

            default:
                throw new IllegalStateException("wrong ScenarioBlockType");

        }

    }

    private static RemoteConsumerModel getNext(Collection<RemoteConsumerModel> consumerModelIterator, RemoteConsumerModel current) {
        return consumerModelIterator.stream().filter(new Predicate<RemoteConsumerModel>() {
            @Override
            public boolean test(RemoteConsumerModel remoteConsumerModel) {
                Identifier prevId = new Identifier(remoteConsumerModel.getPreviousBlockId(),
                        remoteConsumerModel.getPreviousBlockVersion());
                return prevId.equals(new Identifier(current.getId().getId(), current.getVersion()));
            }
        }).findFirst().orElse(null);
    }

    private static Map<Integer, RemoteConsumerModel> getNextForCondition(Collection<RemoteConsumerModel> consumerModelIterator, RemoteConsumerModel current) {
        List<RemoteConsumerModel> list = consumerModelIterator.stream().filter(new Predicate<RemoteConsumerModel>() {
            @Override
            public boolean test(RemoteConsumerModel remoteConsumerModel) {
                Identifier prevId = new Identifier(remoteConsumerModel.getPreviousBlockId(),
                        remoteConsumerModel.getPreviousBlockVersion());
                return prevId.equals(new Identifier(current.getId().getId(), current.getVersion()));
            }
        }).collect(Collectors.toList());

        Map<Integer, RemoteConsumerModel> ret = new HashMap<>();
        current.getNextBlocksByIdCondition().forEach(new BiConsumer<Integer, Identifier>() {
            @Override
            public void accept(Integer integer, Identifier identifier) {
                for (RemoteConsumerModel rcm : list) {
                    Identifier rcmId = new Identifier(rcm.getPreviousBlockId(),
                            rcm.getPreviousBlockVersion());
                    if (rcmId.equals(identifier)) {
                        ret.put(integer, rcm);
                        return;
                    }
                }
            }
        });
        return ret;
    }


}
