package com.petra.lib.context.model;

import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.Context;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.repo.WorkflowContextRepo;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.context.workflow.WorkflowContextState;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextModel;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a remote consumer in a workflow.
 * Responsible for executing remote calls and handling responses.
 */
public class RemoteConsumer {
    private final ConsumerIdentifier id;
    private final String currentServiceName;
    private final RemoteConsumer nextConsumer;
    private final String consumerServiceName;
    private final ValueContextModel valueContextModel;
    private final Sender sender;
    private final WorkflowContextRepo workflowContextRepo;
    private final ContextRepo contextRepo;
    private final OperationService operationService;

    public RemoteConsumer(RemoteConsumerModel remoteConsumerModel, String currentServiceName, RemoteConsumer nextConsumer,
                          Sender sender, ValueContextModel valueContextModel, WorkflowContextRepo workflowContextRepo,
                          ContextRepo contextRepo, OperationService operationService) {
        this.id = new ConsumerIdentifier(remoteConsumerModel.getId(), remoteConsumerModel.getVersion(),
                remoteConsumerModel.getWorkflowId(), remoteConsumerModel.getWorkflowVersion());
        this.currentServiceName = currentServiceName;
        this.nextConsumer = nextConsumer;
        this.consumerServiceName = remoteConsumerModel.getServiceName();
        this.sender = sender;
        this.valueContextModel = valueContextModel;
        this.workflowContextRepo = workflowContextRepo;
        this.contextRepo = contextRepo;
        this.operationService = operationService;
    }

    public void execute(ValueContainer inputValueContainer, UUID scenarioId) {
        valueContextModel.start(inputValueContainer, scenarioId, new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                MessageDto messageDto = new MessageDto(
                        scenarioId,
                        id.getWorkflowId().getId(),
                        id.getWorkflowId().getVersion(),
                        inputValueContainer.getJson(),
                        id.getConsumerId().getId(),
                        id.getConsumerId().getVersion(),
                        currentServiceName,
                        null
                );
                WorkflowContextEntity workflowContextEntity = new WorkflowContextEntity(id, scenarioId);
                workflowContextRepo.insertContext(workflowContextEntity);

                //TODO: set timer
                sender.requestBlockExecution(messageDto, consumerServiceName, new SenderCallback() {
                    @Override
                    public void answer(MessageResponse messageResponse) {
                        //do nothing
                    }

                    @Override
                    public void error(Exception e, MessageResponse messageResponse) {
                        Context context = contextRepo.findContext(scenarioId, id.getWorkflowId()).get();
                        context.saveError(e);
                        operationService.executeState(context);

                        workflowContextEntity.setWorkflowState(WorkflowContextState.ERROR);
                        workflowContextRepo.updateContext(workflowContextEntity);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                Context context = contextRepo.findContext(scenarioId, id.getWorkflowId()).get();
                context.saveError(e);
                operationService.executeState(context);
            }
        });
    }

    public void answer(UUID scenarioId, ValueContainer answerContainer) {
        WorkflowContextEntity contextEntity = new WorkflowContextEntity(id, scenarioId);
        contextEntity.setWorkflowState(WorkflowContextState.DONE);
        contextEntity.setResultValues(answerContainer);
        workflowContextRepo.updateContext(contextEntity);

    }

    public boolean isExecuted(UUID scenarioId) {
        Optional<WorkflowContextEntity> context = workflowContextRepo.getWorkflowContext(id, scenarioId);
        if (context.isEmpty()) return false;

        return context.get().getWorkflowState() == WorkflowContextState.DONE ||
                context.get().getWorkflowState() == WorkflowContextState.ERROR;
    }

    public boolean hasNext() {
        return nextConsumer != null;
    }

    public RemoteConsumer next() {
        return nextConsumer;
    }

    public ConsumerIdentifier getId() {
        return id;
    }
}
