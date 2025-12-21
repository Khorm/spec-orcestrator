package com.petra.lib.constructor;

import com.petra.lib.constructor.model.ConstructorModel;
import com.petra.lib.constructor.model.LocalSourceModel;
import com.petra.lib.context.block.BlockContextExecutor;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalSource;
import com.petra.lib.context.source.SourceContextExecutor;
import com.petra.lib.context.source.SourceUserHandler;
import com.petra.lib.controller.PetraController;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.remote.HttpSender;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.transaction.TransactionManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.petra.lib.context.block.BlockContextExecutorFactory.createBlockContextExecutor;

public class Constructor {

    public PetraController construct(ConstructorModel constructorModel, JpaTransactionManager jpaTransactionManager,
                                     PetraProperties petraProperties, Map<String, UserActionHandler> userActionHandlerMap,
                                     Map<String, SourceUserHandler> sourceUserHandlerMap) {
        TransactionManager transactionManager = TransactionManagerFactory.createTransactionManager(jpaTransactionManager);

        ThreadController threadController = new ThreadController(petraProperties.getThreadCount());
        Sender sender = new HttpSender(threadController);

        BlockContextExecutor blockContextExecutor = createBlockContextExecutor(constructorModel.getProducers(),
                constructorModel.getConsumers(), transactionManager,
                threadController, sender, petraProperties.getServiceName(), userActionHandlerMap);

        SourceContextExecutor sourceContextExecutor = createSourceContextExecutor(constructorModel.getSources(),
                jpaTransactionManager.getEntityManagerFactory(), sourceUserHandlerMap);

        return new PetraController(blockContextExecutor, sourceContextExecutor);
    }


    private SourceContextExecutor createSourceContextExecutor(Collection<LocalSourceModel> localSourceModels,
                                                              EntityManagerFactory entityManagerFactory,
                                                              Map<String, SourceUserHandler> sourceUserHandlerMap) {
        Collection<LocalSource> localSources = new ArrayList<>();
        for (LocalSourceModel localSourceModel : localSourceModels) {
            if (!sourceUserHandlerMap.containsKey(localSourceModel.getName())) {
                throw new RuntimeException("Source user handler not found for " + localSourceModel.getName());
            }
            localSources.add(new LocalSource(
                    new Identifier(localSourceModel.getId(), localSourceModel.getVersion()),
                    localSourceModel.getName(),
                    localSourceModel.getOutputModels(),
                    sourceUserHandlerMap.get(localSourceModel.getName())
            ));
        }

        return new SourceContextExecutor(localSources, entityManagerFactory);
    }
//
//    private WorkflowContextExecutor createWorkflowContextExecutor(TransactionManager transactionManager,
//                                                                  ThreadController threadController,
////                                                                  BlockContextExecutor blockContextExecutor,
//                                                                  Sender sender, Collection<LocalProducerModel> localProducerModels,
////                                                                  String currentServiceName,
//                                                                  OperationFactory operationFactory) {
//
////        WorkflowAnswerOperation workflowAnswerOperation = new WorkflowAnswerOperation(transactionManager, blockContextExecutor);
////        WorkflowSendOperation workflowSendOperation = new WorkflowSendOperation(sender, currentServiceName);
//        OperationService operationService =
//                new OperationService(threadController,operationFactory,
//                        ContextState.BLOCK_CREATING, ContextState.WORKFLOW_STARTING, ContextState.WORKFLOW_LOADING_VARIABLES,
//                        ContextState.WORKFLOW_EXECUTING, ContextState.BLOCK_ANSWERING);
//
//        Collection<LocalProducer> localProducers = new ArrayList<>();
//        for (LocalProducerModel localProducerModel : localProducerModels) {
//            ValueContextManager valueContextManager = VariableFactory.createStartedLoaders(localProducerModel.getLastWorkflowBlockValueParser(),
//                    localProducerModel.getLastWorkflowBlockValuesCount(),
//                    threadController, sender);
//            localProducers.add(new LocalProducer(
//                    new Identifier(localProducerModel.getId(), localProducerModel.getVersion()),
//                    localProducerModel.getConsumers().stream().map((RemoteConsumerModel remoteConsumerModel) -> new RemoteConsumer(remoteConsumerModel, currentServiceName, nextConsumer, sender, valueContextManager, workflowContextRepo)).collect(Collectors.toList()),
//                    localProducerModel.getName(),
//                    valueContextManager,
//                    workflowContextRepo));
//        }
//
//        return new WorkflowContextExecutor(
//                RepoFactory.createWorkflowRepo(transactionManager),
//                transactionManager,
//                operationService,
//                localProducers
//        );
//    }


}
