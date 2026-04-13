package com.petra.lib.operation.actor;

import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.utils.id.ConsumerIdentifier;
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


    RemoteConsumer(RemoteConsumerModel remoteConsumerModel, ValueContextManager valueContextManager,
                   String currentServiceName, RemoteConsumer nextConsumer,
                   Sender sender) {
        this.id = new ConsumerIdentifier(remoteConsumerModel.getId(), remoteConsumerModel.getVersion(),
                remoteConsumerModel.getWorkflowId(), remoteConsumerModel.getWorkflowVersion());
        this.currentServiceName = currentServiceName;
        this.nextConsumer = nextConsumer;
        this.consumerServiceName = remoteConsumerModel.getServiceName();
        this.sender = sender;
        this.valueContextManager = valueContextManager;
    }


    public void execute(ValueContainer contextVariables, UUID scenarioId,
                        RemoteConsumerResultCallback skip, OperationService operationService,
                        RemoteConsumerResultCallback error) {
//        WorkflowContext currentContext = contextService.createWorkflowContext(workflowContext.getScenarioId(), id);
//        boolean isLoaded = currentContext.load();
//        if (isLoaded){
//            return Optional.of(currentContext.get;
//        }

        ValueContainer blockContext = ValueContainerFactory.getSimpleContainer();
        contextVariables.getValues().forEach(blockContext::setValue);

        RemoteConsumer thisConsumer = this;

        valueContextManager.start(blockContext, scenarioId, new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                loadedValues.getValues().forEach(blockContext::setValue);
                sendMessage(scenarioId, loadedValues, skip, operationService, error);
            }

            @Override
            public void error(Exception e) {
                error.callback(scenarioId, thisConsumer, operationService);
//                WorkflowContext errorBlockContext = contextService.createWorkflowContext(workflowContext.getScenarioId(), id);
//                boolean loadResult = errorBlockContext.lockAndLoad();
//                workflowContext.lockAndLoad();
//                workflowContext.saveError(e);
//                operationService.executeState(workflowContext);
//                if (!loadResult) {
//                    return;
//                }
//
//                errorBlockContext.setState(WorkflowContextState.ERROR);
//                errorBlockContext.unlockAndSave();
            }
        });
    }


//    public void answer(UUID scenarioId, ExecutionStatus execResult) {
//
//        WorkflowContext contextEntity = contextService.createWorkflowContext(scenarioId, id);
//
//
//        WorkflowContextState state;
//        switch (execResult) {
//            case OK:
//                state = WorkflowContextState.DONE;
//                break;
//            case ERROR:
//                state = WorkflowContextState.ERROR;
//                break;
//            default:
//                throw new IllegalStateException("WRONG EXECUTION STATUS " + execResult.name());
//        }
//
//        boolean loadContext = contextEntity.lockAndLoad();
//
//        if (!loadContext) {
//            return;
//        }
//
//        boolean saveResult = contextEntity.setState(state);
//        if (!saveResult) {
//            return;
//        }
//
//        contextEntity.unlockAndSave();
//
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


    private void sendMessage(UUID scenarioID, ValueContainer inputValueContainer, RemoteConsumerResultCallback skip,
                             OperationService operationService,
                             RemoteConsumerResultCallback error) {

        MessageDto messageDto = new MessageDto(
                scenarioID,
                id.getConsumerId().getId(),
                id.getConsumerId().getVersion(),
                inputValueContainer.getModels(),
                id.getWorkflowId().getId(),
                id.getWorkflowId().getVersion(),
                currentServiceName,
                null
        );

//        WorkflowContext blockWorkContext = contextService.createWorkflowContext(scenarioID, id);
//        blockWorkContext.create();
        RemoteConsumer thisConsumer = this;
        //TODO: set timer
        sender.requestBlockExecution(messageDto, consumerServiceName, new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                if (messageResponse.isRepeat()) {
                    skip.callback(scenarioID, thisConsumer, operationService);
                }
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                error.callback(scenarioID, thisConsumer, operationService);
//                WorkflowContext errorBlockContext = contextService.createWorkflowContext(workflowContext.getScenarioId(), id);
//                boolean loadResult = errorBlockContext.lockAndLoad();
//                workflowContext.lockAndLoad();
//                workflowContext.saveError(e);
//                operationService.executeState(workflowContext);
//                if (!loadResult) {
//                    return;
//                }
//
//                errorBlockContext.setState(WorkflowContextState.ERROR);
//                errorBlockContext.unlockAndSave();
            }
        });
    }


}
