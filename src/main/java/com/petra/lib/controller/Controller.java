package com.petra.lib.controller;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.block.BlockContextExecutor;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.source.SourceContextExecutor;
import com.petra.lib.context.workflow.WorkflowContextExecutor;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;

public class Controller {
    private final BlockContextExecutor blockContextExecutor;
    private final WorkflowContextExecutor workflowContextExecutor;
    private final SourceContextExecutor sourceContextExecutor;

    public Controller(BlockContextExecutor blockContextExecutor,
                      WorkflowContextExecutor workflowContextExecutor, SourceContextExecutor sourceContextExecutor) {
        this.blockContextExecutor = blockContextExecutor;
        this.workflowContextExecutor = workflowContextExecutor;
        this.sourceContextExecutor = sourceContextExecutor;
    }


    public void requestBlock(MessageDto messageDto) {
        blockContextExecutor.startContext(messageDto.getScenarioId(), createProducer(messageDto));
    }

    public MessageDto requestSource(MessageDto messageDto) {
        ValueContainer valueContainer = sourceContextExecutor.startContext(createProducer(messageDto));
        messageDto.setTransmittedValues(valueContainer.getJson());
        return messageDto;
    }

    public void blockAnswer(MessageDto messageDto) {
        workflowContextExecutor.loadContext(createProducer(messageDto), messageDto.getScenarioId());
    }

    private RemoteProducer createProducer(MessageDto messageDto) {
        Identifier identifier = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier consumerId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());
        return new RemoteProducer(identifier, messageDto.getSenderServiceName(),
                ValueContainerFactory.getSimpleContainer(messageDto.getTransmittedValues()), consumerId);
    }

}
