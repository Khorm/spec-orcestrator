package com.petra.lib.actor.remote.consumer;

import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.remote.Sender;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContextManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RemoteCondition extends AbsRemoteConsumer {

    private final ContextService contextService;
    public static final String conditionResultValueName = "CONDITION_RESULT";
    private final Map<Integer, RemoteConsumer> nextRemoteConsumersByConditionResult;

    public RemoteCondition(String consumerName, ValueContextManager valueContextManager, String currentServiceName,
                           Sender sender, ConsumerIdentifier id,
                           String consumerServiceName, ContextService contextService,
                           Map<Integer, RemoteConsumer> nextRemoteConsumersByConditionResult) {
        super(consumerName, valueContextManager, currentServiceName, sender, id, consumerServiceName, contextService);
        this.contextService = contextService;
        this.nextRemoteConsumersByConditionResult = nextRemoteConsumersByConditionResult;
    }

    @Override
    protected RemoteConsumer getNextConsumer(LinkedConsumerListMessage linkedConsumerListMessage) {
        Context conditionContext = contextService.loadContext(getId().getConsumerId(), linkedConsumerListMessage.getScenarioId());
        ValueDto valueDto = conditionContext.getContextOutValues().getValue(conditionResultValueName);
        Integer value = Integer.valueOf(valueDto.getJsonValue());
        return nextRemoteConsumersByConditionResult.get(value);
    }

    @Override
    public void updateWorkflowContextValues(Identifier workflowId, UUID scenarioId, ValueContainer answerContainer) {

    }
}
