package com.petra.lib.constructor;

import com.petra.lib.actor.ActorFactory;
import com.petra.lib.actor.local.LocalConsumer;
import com.petra.lib.actor.local.activity.LocalActivity;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import com.petra.lib.actor.local.condition.ConditionUserHandler;
import com.petra.lib.actor.local.condition.LocalCondition;
import com.petra.lib.actor.local.producer.LocalProducer;
import com.petra.lib.actor.local.source.LocalSource;
import com.petra.lib.actor.local.source.SourceUserHandler;
import com.petra.lib.constructor.model.*;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.controller.PetraController;
import com.petra.lib.controller.PetraControllerImpl;
import com.petra.lib.executor.BlockContextExecutor;
import com.petra.lib.executor.ConsumerCollection;
import com.petra.lib.executor.WorkflowAnswerExecutor;
import com.petra.lib.operation.OperationConstructor;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.BlockExecutingOperation;
import com.petra.lib.operation.operations.FinishOperation;
import com.petra.lib.operation.operations.UserAnswerOperation;
import com.petra.lib.remote.HttpSender;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.timer.TimerThread;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.transaction.TransactionManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Constructor {

    public PetraController construct(ConstructorModel constructorModel, JpaTransactionManager jpaTransactionManager,
                                     PetraProperties petraProperties, Map<String, UserActivityHandler> userActionHandlerMap,
                                     Map<String, SourceUserHandler> sourceUserHandlerMap,
                                     Map<String, ConditionUserHandler> conditionUserHandlerMap) {

        TransactionManager transactionManager = TransactionManagerFactory.createTransactionManager(jpaTransactionManager);

        ThreadController threadController = new ThreadController(petraProperties.getThreadCount());

        Sender sender = new HttpSender(threadController);
        ContextRepo contextRepo = RepoFactory.createBlockRepo(transactionManager);
        WorkflowContextRepo workflowContextRepo = RepoFactory.createWorkflowRepo(transactionManager);


        ContextService contextService = new ContextService(contextRepo, workflowContextRepo, transactionManager, petraProperties.getServiceName());

        ConsumerCollection consumerCollection = createConsumerCollection(constructorModel, petraProperties, sender,
                transactionManager, userActionHandlerMap, threadController, contextService, conditionUserHandlerMap,
                sourceUserHandlerMap);

        BlockExecutingOperation blockExecutingOperation = new BlockExecutingOperation(consumerCollection);
        UserAnswerOperation userAnswerOperation = new UserAnswerOperation(transactionManager);
        AnswerOperation answerOperation = new AnswerOperation(sender, transactionManager);
        FinishOperation finishOperation = new FinishOperation(transactionManager);

        OperationService blockOperationService = OperationConstructor.createAsyncOperationService(threadController,
                transactionManager, blockExecutingOperation, answerOperation);
        OperationService userOperationService = OperationConstructor.createAsyncOperationService(threadController, transactionManager,
                blockExecutingOperation, userAnswerOperation);
        OperationService sourceAndConditionOperationService = OperationConstructor.createSyncOperationService(transactionManager,
                blockExecutingOperation, blockExecutingOperation, finishOperation);


        WorkflowAnswerExecutor workflowAnswerExecutor = new WorkflowAnswerExecutor(consumerCollection, blockOperationService,
                contextService, userOperationService);

        BlockContextExecutor blockContextExecutor = new BlockContextExecutor(blockOperationService, consumerCollection,
                sourceAndConditionOperationService, contextService, userOperationService);

        TimerThread timerThread = new TimerThread(contextService, blockOperationService);

        return new PetraControllerImpl(blockContextExecutor, workflowAnswerExecutor, transactionManager, threadController,
                workflowContextRepo, timerThread);
    }


    private ConsumerCollection createConsumerCollection(ConstructorModel constructorModel, PetraProperties petraProperties,
                                                        Sender sender, TransactionManager transactionManager,
                                                        Map<String, UserActivityHandler> userActionHandlerMap,
                                                        ThreadController threadController, ContextService contextService,
                                                        Map<String, ConditionUserHandler> conditionUserHandlerMap,
                                                        Map<String, SourceUserHandler> sourceUserHandlerMap) {
        Collection<LocalProducer> localProducers = new ArrayList<>();
        for (LocalProducerModel model : constructorModel.getWorkflows()) {
            localProducers.add(ActorFactory.localProducer(model, petraProperties.getServiceName(), sender,
                    transactionManager, threadController, contextService));
        }

        Collection<LocalActivity> localActivities = new ArrayList<>();
        for (LocalActivityModel model : constructorModel.getActivities()) {
            localActivities.add(ActorFactory.localActivity(model, userActionHandlerMap.get(model.getName()),
                    transactionManager));
        }

        Collection<LocalCondition> localConditions = new ArrayList<>();
        for (LocalConditionModel condition : constructorModel.getConditions()) {
            localConditions.add(ActorFactory.createLocalCondition(condition,
                    conditionUserHandlerMap.get(condition.getName()), transactionManager));
        }

        Collection<LocalSource> localSources = new ArrayList<>();
        for (LocalSourceModel model : constructorModel.getSources()) {
            localSources.add(ActorFactory.createLocalSource(model, sourceUserHandlerMap.get(model.getName()), transactionManager));
        }

        Collection<LocalConsumer> allConsumers = new ArrayList<>();
        allConsumers.addAll(localProducers);
        allConsumers.addAll(localActivities);
        allConsumers.addAll(localConditions);
        allConsumers.addAll(localSources);


        return new ConsumerCollection(allConsumers);
    }


}
