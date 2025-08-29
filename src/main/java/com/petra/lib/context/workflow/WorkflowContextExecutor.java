package com.petra.lib.context.workflow;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteConsumer;
import com.petra.lib.context.operation.OperationServiceImpl;
import com.petra.lib.context.repo.WorkflowContextRepo;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.container.ValueContainer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkflowContextExecutor {
    private final WorkflowContextRepo workflowContextRepo;
    private final TransactionManager transactionManager;
    private final OperationServiceImpl<WorkflowContext> workflowOperationService;
    private final Map<Identifier, LocalProducer> workflowProducers;


    public WorkflowContextExecutor(WorkflowContextRepo workflowContextRepo, TransactionManager transactionManager,
                                   OperationServiceImpl<WorkflowContext> workflowOperationService,
                                   Collection<LocalProducer> workflowProducers) {
        this.workflowContextRepo = workflowContextRepo;
        this.transactionManager = transactionManager;
        this.workflowOperationService = workflowOperationService;
        this.workflowProducers = workflowProducers.stream().collect(Collectors.toMap(LocalProducer::getIdentifier, Function.identity()));
    }

    public void loadContext(RemoteConsumer remoteConsumer, ValueContainer resultValues, UUID scenarioId) {
        loadContext(remoteConsumer.getIdentifier(), resultValues, scenarioId, null);
    }

    public void loadContext(WorkflowContext previousContext) {
        loadContext(previousContext.getLocalProducer().getIdentifier(), null,
                previousContext.getScenarioId(), previousContext.getWorkflowOutputValues());
    }

    /**
     * @param producerId           - айди текущего блока воркфлоу
     * @param outputWorkflowValues - исходящие переменные текущего блока воркфлоу
     * @param scenarioId           - айди выполняемого сценария
     * @param inputWorkflowValues  - входящие в воркфлоу переменные
     */
    private void loadContext(Identifier producerId, ValueContainer outputWorkflowValues, UUID scenarioId, ValueContainer inputWorkflowValues) {
        LocalProducer localProducer = workflowProducers.get(producerId);
        Collection<WorkflowContextEntity> workflowContexts = workflowContextRepo.getWorkflowContexts(localProducer, scenarioId);
        Map<Identifier, WorkflowContextEntity> contextsByConsumers = workflowContexts.stream()
                .collect(Collectors.toMap(WorkflowContextEntity::getConsumerId, Function.identity()));

        WorkflowContextEntity contextEntity = null;
        WorkflowContextEntity previousContextEntity = null;
        RemoteConsumer contextRemoteConsumer = null;
        for (RemoteConsumer remoteConsumer : localProducer.getWorkflowConsumers()) {

            //находим недовыполненный контекст
            if (contextsByConsumers.containsKey(remoteConsumer.getIdentifier())) {
                previousContextEntity = contextEntity;
                contextEntity = contextsByConsumers.get(remoteConsumer.getIdentifier());
                if (contextEntity.getWorkflowState() != ContextState.ANSWERED) {
                    contextRemoteConsumer = remoteConsumer;
                    break;
                }
            } else {
                //находим что предыдущий контекст выполнен или не существует, а следующейго еще не существует.
                previousContextEntity = contextEntity;
                contextEntity = createContextEntity(remoteConsumer, localProducer, scenarioId);
                contextRemoteConsumer = remoteConsumer;
                break;
            }
        }

        if (contextEntity == null)
            throw new NullPointerException("No context found for workflow " + localProducer.getName());

        //берет либо исходящие знаечения последнего контекст, либо входщие
        ValueContainer contextInputValues = previousContextEntity != null ?
                previousContextEntity.getResultValues() : inputWorkflowValues;
        new WorkflowContextImpl(
                localProducer,
                contextRemoteConsumer,
                contextEntity,
                workflowContextRepo,
                contextInputValues,
                outputWorkflowValues,
                workflowOperationService,
                transactionManager,
                this).run();
    }


    private WorkflowContextEntity createContextEntity(RemoteConsumer contextRemoteConsumer,
                                                      LocalProducer localProducer, UUID scenarioId) {
        return new WorkflowContextEntity(contextRemoteConsumer.getIdentifier(), localProducer.getIdentifier(), scenarioId);
    }


}
