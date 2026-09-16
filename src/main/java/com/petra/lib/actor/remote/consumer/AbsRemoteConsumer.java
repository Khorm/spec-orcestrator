package com.petra.lib.actor.remote.consumer;

import com.petra.lib.actor.remote.RemoteConsumerResultCallback;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContextManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public abstract class AbsRemoteConsumer implements RemoteConsumer {

    String consumerName;
    ValueContextManager valueContextManager;
    String currentServiceName;
    Sender sender;

    @Getter
    ConsumerIdentifier id;
    String consumerServiceName;

    ContextService contextService;


    public void handleAnswer(LinkedConsumerListMessage message) {
        if (id.getConsumerId().equals(message.getAnswerId())) {
            updateWorkflowContextValues(id.getWorkflowId(), message.getScenarioId(), message.getAnswerVariables());
            message.addContextVariable(message.getAnswerVariables());
            getNextConsumer(message).execute(message);
        } else {
            Context currentContext = contextService.loadContext(id.getConsumerId(), message.getScenarioId());
            message.addContextVariable(currentContext.getContextOutValues());
            getNextConsumer(message).handleAnswer(message);
        }
    }

    public void execute(LinkedConsumerListMessage message) {
        UUID scenarioId = message.getScenarioId();
        ValueContainer workflowContextVariables = message.getWorkflowContextVariables();
        RemoteConsumerResultCallback skip = message.getSkip();
        OperationService operationService = message.getOperationService();

        log.info("{} remote consumer {} variables loading", scenarioId, consumerName);
        valueContextManager.start(workflowContextVariables, scenarioId, new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                ValueContainer loadedAndWorkflowVariables = ValueContainerFactory.getSimpleContainerByModels(valueContextManager.getLoadValues());
                for (ValueDto value : loadedValues.getValues()) {
                    loadedAndWorkflowVariables.setValueJson(value.getId(), value.getJsonValue());
                }

                for (ValueDto value : workflowContextVariables.getValues()) {
                    loadedAndWorkflowVariables.setValueJson(value.getId(), value.getJsonValue());
                }

                log.info("{} remote consumer {} variables loaded", scenarioId, consumerName);
                sendMessage(scenarioId, loadedAndWorkflowVariables, skip, operationService);
            }

            @Override
            public void error(Exception e) {
                log.info("{} remote consumer {} variables error", scenarioId, consumerName);
                skip.callback(ValueContainerFactory.getSimpleContainerByModels(valueContextManager.getLoadValues()), id.getConsumerId(), scenarioId, ExecutionStatus.ERROR, operationService);
            }
        });
    }


    private void sendMessage(UUID scenarioId, ValueContainer inputValueContainer, RemoteConsumerResultCallback skip,
                             OperationService operationService) {

        MessageDto messageDto = new MessageDto(
                scenarioId,
                id.getConsumerId().getId(),
                id.getConsumerId().getVersion(),
                inputValueContainer.getValues(),
                id.getWorkflowId().getId(),
                id.getWorkflowId().getVersion(),
                currentServiceName,
                null
        );

        log.info("{} remote consumer {} message sending", scenarioId, consumerName);

        sender.requestBlockExecution(messageDto, consumerServiceName, new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                if (messageResponse.isRepeat()) {
                    ValueContainer resultVariableContainer = ValueContainerFactory.getSimpleContainerByDtos(messageResponse.getResultValues());
                    ExecutionStatus repeatStatus = messageResponse.isError() ? ExecutionStatus.ERROR : ExecutionStatus.OK;
                    skip.callback(resultVariableContainer, id.getConsumerId(), scenarioId, repeatStatus, operationService);
                    log.info("{} remote consumer {} message repeat", scenarioId, consumerName);
                    return;
                }
                log.info("{} remote consumer {} message sent", scenarioId, consumerName);
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                log.info("{} remote consumer {} message sending error {}", scenarioId, consumerName, e);
                skip.callback(ValueContainerFactory.getSimpleContainerByModels(valueContextManager.getLoadValues()), id.getConsumerId(), scenarioId, ExecutionStatus.ERROR, operationService);
            }
        });
    }


    protected abstract RemoteConsumer getNextConsumer(LinkedConsumerListMessage linkedConsumerListMessage);

    /*
     *обновляет контекст воркфлоу новыми значениями из выполненного блока
     */
    protected abstract void updateWorkflowContextValues(Identifier workflowId, UUID scenarioId, ValueContainer answerContainer);

}
