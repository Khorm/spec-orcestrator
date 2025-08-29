package com.petra.lib.constructor;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.constructor.model.ConstructorModel;
import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.constructor.model.LocalSourceModel;
import com.petra.lib.context.block.BlockContextExecutor;
import com.petra.lib.context.block.operations.executor.handler.UserActionHandler;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.LocalSource;
import com.petra.lib.context.model.RemoteConsumer;
import com.petra.lib.context.operation.OperationServiceImpl;
import com.petra.lib.context.repo.RepoFactory;
import com.petra.lib.context.source.SourceContextExecutor;
import com.petra.lib.context.source.SourceUserHandler;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.context.workflow.WorkflowContextExecutor;
import com.petra.lib.context.workflow.operations.WorkflowAnswerOperation;
import com.petra.lib.context.workflow.operations.WorkflowSendOperation;
import com.petra.lib.controller.Controller;
import com.petra.lib.remote.HttpListener;
import com.petra.lib.remote.HttpSender;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.transaction.TransactionManagerFactory;
import com.petra.lib.variable.VariableFactory;
import com.petra.lib.variable.context.ValueContextModel;
import com.petra.lib.variable.model.ValueModel;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.petra.lib.context.block.BlockContextExecutorFactory.createBlockContextExecutor;

public class Constructor {
    public Controller construct(ConstructorModel constructorModel, JpaTransactionManager jpaTransactionManager,
                                PetraProperties petraProperties, Map<String, UserActionHandler> userActionHandlerMap,
                                Map<String, SourceUserHandler> sourceUserHandlerMap) {
        TransactionManager transactionManager = TransactionManagerFactory.createTransactionManager(jpaTransactionManager);
        ThreadController threadController = new ThreadController(petraProperties.getThreadCount());
        Sender sender = new HttpSender(threadController);

        BlockContextExecutor blockContextExecutor = createBlockContextExecutor(constructorModel.getConsumers(), transactionManager,
                threadController, sender, petraProperties.getServiceName(), userActionHandlerMap);

        SourceContextExecutor sourceContextExecutor = createSourceContextExecutor(constructorModel.getSources(),
                jpaTransactionManager.getEntityManagerFactory(), sourceUserHandlerMap);

        WorkflowContextExecutor workflowContextExecutor = createWorkflowContextExecutor(transactionManager,
                threadController, blockContextExecutor, sender, constructorModel.getProducers(), petraProperties.getServiceName());


        return new Controller(blockContextExecutor, workflowContextExecutor, sourceContextExecutor);
    }

    public HttpListener createListener(Controller controller, RequestMappingHandlerMapping handlerMapping) throws NoSuchMethodException {
        return new HttpListener(controller, handlerMapping);
    }


    private SourceContextExecutor createSourceContextExecutor(Collection<LocalSourceModel> localSourceModels,
                                                              EntityManagerFactory entityManagerFactory,
                                                              Map<String, SourceUserHandler> sourceUserHandlerMap) {
        Collection<LocalSource> localSources = new ArrayList<>();
        for (LocalSourceModel localSourceModel : localSourceModels) {
            localSources.add(new LocalSource(
                    new Identifier(localSourceModel.getId(), localSourceModel.getVersion()),
                    localSourceModel.getName(),
                    localSourceModel.getOutputModels().stream().map(valueDto -> new ValueModel(valueDto.getId(), valueDto.getName(), valueDto.getMultiplicity(), null))
                            .collect(Collectors.toList()),
                    sourceUserHandlerMap.get(localSourceModel.getName())
            ));
        }
        return new SourceContextExecutor(localSources, entityManagerFactory);
    }

    private WorkflowContextExecutor createWorkflowContextExecutor(TransactionManager transactionManager,
                                                                  ThreadController threadController,
                                                                  BlockContextExecutor blockContextExecutor,
                                                                  Sender sender, Collection<LocalProducerModel> localProducerModels,
                                                                  String currentServiceName) {

        WorkflowAnswerOperation workflowAnswerOperation = new WorkflowAnswerOperation(transactionManager, blockContextExecutor);
        WorkflowSendOperation workflowSendOperation = new WorkflowSendOperation(sender, currentServiceName);
        OperationServiceImpl<WorkflowContext> operationService =
                new OperationServiceImpl<>(threadController, workflowSendOperation, workflowAnswerOperation);

        Collection<LocalProducer> localProducers = new ArrayList<>();
        for (LocalProducerModel localProducerModel : localProducerModels) {
            ValueContextModel valueContextModel = VariableFactory.createStartedLoaders(localProducerModel.getLastWorkflowBlockValueParser(),
                    localProducerModel.getLastWorkflowBlockValuesCount(),
                    threadController, sender);
            localProducers.add(new LocalProducer(
                    new Identifier(localProducerModel.getId(), localProducerModel.getVersion()),
                    localProducerModel.getConsumers().stream().map(RemoteConsumer::new).collect(Collectors.toList()),
                    localProducerModel.getName(),
                    valueContextModel
            ));
        }

        return new WorkflowContextExecutor(
                RepoFactory.createWorkflowRepo(transactionManager),
                transactionManager,
                operationService,
                localProducers
        );
    }


}
