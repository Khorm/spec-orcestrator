package com.petra.lib.controller;

import com.petra.lib.context.block.BlockContextExecutor;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.source.SourceContextExecutor;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;

public class Controller {
    private final BlockContextExecutor blockContextExecutor;
    private final SourceContextExecutor sourceContextExecutor;

    public Controller(BlockContextExecutor blockContextExecutor, SourceContextExecutor sourceContextExecutor) {
        this.blockContextExecutor = blockContextExecutor;
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
        Identifier blockId = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier workflowId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());

        blockContextExecutor.handleAnswerFromBlock(ValueContainerFactory.getSimpleContainer(messageDto.getTransmittedValues()),
                blockId, messageDto.getScenarioId(), workflowId);
    }

    private RemoteProducer createProducer(MessageDto messageDto) {
        Identifier identifier = new Identifier(messageDto.getSenderId(), messageDto.getSenderVersion());
        Identifier consumerId = new Identifier(messageDto.getReceiverId(), messageDto.getReceiverVersion());
        return new RemoteProducer(identifier, messageDto.getSenderServiceName(),
                ValueContainerFactory.getSimpleContainer(messageDto.getTransmittedValues()), consumerId);
    }

}
