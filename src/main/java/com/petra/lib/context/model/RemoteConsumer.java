package com.petra.lib.context.model;

import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.Context;
import com.petra.lib.context.block.ContextService;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.context.block.WorkflowContextState;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.context.ValueContextManager;

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
    private final ValueContextManager valueContextManager;
    private final Sender sender;
    private final ContextService contextService;
    private final OperationService operationService;


    RemoteConsumer(RemoteConsumerModel remoteConsumerModel, ValueContextManager valueContextManager,
                   String currentServiceName, RemoteConsumer nextConsumer,
                   Sender sender, ContextService contextService, OperationService operationService) {
        this.id = new ConsumerIdentifier(remoteConsumerModel.getId(), remoteConsumerModel.getVersion(),
                remoteConsumerModel.getWorkflowId(), remoteConsumerModel.getWorkflowVersion());
        this.currentServiceName = currentServiceName;
        this.nextConsumer = nextConsumer;
        this.consumerServiceName = remoteConsumerModel.getServiceName();
        this.sender = sender;
        this.valueContextManager = valueContextManager;
        this.operationService = operationService;

        this.contextService = contextService;
    }


    public void execute(Context workflowContext) {
        ValueContainer blockContext = ValueContainerFactory.getSimpleContainer();
        workflowContext.getContextInputValues().getValues().forEach(blockContext::setValue);
        workflowContext.getContextOutValues().getValues().forEach(blockContext::setValue);


        valueContextManager.start(blockContext, workflowContext.getScenarioId(), new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                loadedValues.getValues().forEach(blockContext::setValue);
                sendMessage(workflowContext, loadedValues);
            }

            @Override
            public void error(Exception e) {
                WorkflowContext errorBlockContext = contextService.createWorkflowContext(workflowContext.getScenarioId(), id);
                boolean loadResult = errorBlockContext.lockAndLoad();
                workflowContext.lockAndLoad();
                workflowContext.saveError(e);
                operationService.executeState(workflowContext);
                if (!loadResult) {
                    return;
                }

                errorBlockContext.setState(WorkflowContextState.ERROR);
                errorBlockContext.unlockAndSave();
            }
        });
    }


    public void answer(UUID scenarioId, ExecutionStatus execResult) {

        WorkflowContext contextEntity = contextService.createWorkflowContext(scenarioId, id);
        boolean loadContext = contextEntity.lockAndLoad();

        if (!loadContext){
            return;
        }

        WorkflowContextState state;
        switch (execResult) {
            case OK:
                state = WorkflowContextState.DONE;
                break;
            case ERROR:
                state = WorkflowContextState.ERROR;
                break;
            default:
                throw new IllegalStateException("WRONG EXECUTION STATUS " + execResult.name());
        }

        boolean saveResult = contextEntity.setState(state);
        if (!saveResult) {
            return;
        }

        contextEntity.unlockAndSave();

    }

//    public boolean isExecuted(UUID scenarioId) {
//        Optional<WorkflowContextEntity> context = workflowContextRepo.getWorkflowContext(id, scenarioId);
//        if (context.isEmpty()) return false;
//
//        return context.get().getWorkflowState() == WorkflowContextState.DONE ||
//                context.get().getWorkflowState() == WorkflowContextState.ERROR;
//    }

    public boolean hasNext() {
        return nextConsumer != null;
    }

    public RemoteConsumer next() {
        return nextConsumer;
    }

    public ConsumerIdentifier getId() {
        return id;
    }


    private void sendMessage(Context workflowContext, ValueContainer inputValueContainer) {


        MessageDto messageDto = new MessageDto(
                workflowContext.getScenarioId(),
                id.getConsumerId().getId(),
                id.getConsumerId().getVersion(),
                inputValueContainer.getModels(),
                id.getWorkflowId().getId(),
                id.getWorkflowId().getVersion(),
                currentServiceName,
                null
        );

        WorkflowContext blockWorkContext = contextService.createWorkflowContext(workflowContext.getScenarioId(), id);
        blockWorkContext.create();

        //TODO: set timer
        sender.requestBlockExecution(messageDto, consumerServiceName, new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                //do nothing
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {

                WorkflowContext errorBlockContext = contextService.createWorkflowContext(workflowContext.getScenarioId(), id);
                boolean loadResult = errorBlockContext.lockAndLoad();
                workflowContext.lockAndLoad();
                workflowContext.saveError(e);
                operationService.executeState(workflowContext);
                if (!loadResult) {
                    return;
                }

                errorBlockContext.setState(WorkflowContextState.ERROR);
                errorBlockContext.unlockAndSave();
            }
        });
    }


}
