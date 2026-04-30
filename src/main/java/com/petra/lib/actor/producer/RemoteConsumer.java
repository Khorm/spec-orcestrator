package com.petra.lib.actor.producer;

import com.petra.lib.actor.RemoteConsumerResultCallback;
import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.context.ValueContextManager;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a remote consumer in a workflow.
 * Responsible for executing remote calls and handling responses.
 */
@Log4j2
public class RemoteConsumer {
    private final ConsumerIdentifier id;
    private final String currentServiceName;
    private final RemoteConsumer nextConsumer;
    private final String consumerServiceName;
    private final ValueContextManager valueContextManager;
    private final Sender sender;

    @Getter
    private final String consumerName;


    public RemoteConsumer(RemoteConsumerModel remoteConsumerModel, ValueContextManager valueContextManager,
                   String currentServiceName, RemoteConsumer nextConsumer,
                   Sender sender) {
        this.id = new ConsumerIdentifier(remoteConsumerModel.getId(), remoteConsumerModel.getVersion(),
                remoteConsumerModel.getWorkflowId(), remoteConsumerModel.getWorkflowVersion());
        this.currentServiceName = currentServiceName;
        this.nextConsumer = nextConsumer;
        this.consumerServiceName = remoteConsumerModel.getServiceName();
        this.sender = sender;
        this.valueContextManager = valueContextManager;
        this.consumerName = remoteConsumerModel.getConsumerName();
    }


    public void execute(ValueContainer workflowContextVariables, UUID scenarioId,
                        RemoteConsumerResultCallback skip, OperationService operationService) {

//        RemoteConsumer thisConsumer = this;

        log.info("{} remote consumer {} variables loading", scenarioId, consumerName);
        valueContextManager.start(workflowContextVariables, scenarioId, new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                ValueContainer loadedAndWorkflowVariables = ValueContainerFactory.getSimpleContainer();
                loadedValues.getValues().forEach(loadedAndWorkflowVariables::setValue);
                workflowContextVariables.getValues().forEach(loadedAndWorkflowVariables::setValue);

                log.info("{} remote consumer {} variables loaded", scenarioId, consumerName);
                sendMessage(scenarioId, loadedAndWorkflowVariables, skip, operationService);
            }

            @Override
            public void error(Exception e) {
                log.info("{} remote consumer {} variables error", scenarioId, consumerName);
                skip.callback(ValueContainerFactory.getSimpleContainer(), id.getConsumerId(), scenarioId, ExecutionStatus.ERROR, operationService);
            }
        });
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


    private void sendMessage(UUID scenarioId, ValueContainer inputValueContainer, RemoteConsumerResultCallback skip,
                             OperationService operationService) {

        MessageDto messageDto = new MessageDto(
                scenarioId,
                id.getConsumerId().getId(),
                id.getConsumerId().getVersion(),
                inputValueContainer.getModels(),
                id.getWorkflowId().getId(),
                id.getWorkflowId().getVersion(),
                currentServiceName,
                null
        );

        log.info("{} remote consumer {} message sending", scenarioId, consumerName);

        RemoteConsumer thisConsumer = this;
        sender.requestBlockExecution(messageDto, consumerServiceName, new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                if (messageResponse.isRepeat()) {
                    ValueContainer resultVariableContainer = ValueContainerFactory.getSimpleContainer(messageResponse.getResultValues());
                    ExecutionStatus repeatStatus = messageResponse.isError() ? ExecutionStatus.ERROR: ExecutionStatus.OK;
                    skip.callback(resultVariableContainer, id.getConsumerId(), scenarioId, repeatStatus, operationService);
                    log.info("{} remote consumer {} message repeat", scenarioId, consumerName);
                    return;
                }
                log.info("{} remote consumer {} message sent", scenarioId, consumerName);
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                log.info("{} remote consumer {} message sending error {}", scenarioId, consumerName, e);
                skip.callback(ValueContainerFactory.getSimpleContainer(), id.getConsumerId(), scenarioId, ExecutionStatus.ERROR, operationService);
            }
        });
    }


}
