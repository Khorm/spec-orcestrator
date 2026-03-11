package com.petra.lib.controller;

import com.petra.lib.context.block.BlockContextExecutor;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.source.SourceContextExecutor;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PetraController {
    private final BlockContextExecutor blockContextExecutor;
    private final SourceContextExecutor sourceContextExecutor;

    public PetraController(BlockContextExecutor blockContextExecutor, SourceContextExecutor sourceContextExecutor) {
        this.blockContextExecutor = blockContextExecutor;
        this.sourceContextExecutor = sourceContextExecutor;
    }


    public void requestBlock(MessageDto messageDto) {
        blockContextExecutor.startContext(messageDto.getScenarioId(), createProducer(messageDto));
    }

    public SourceResponseDto requestSource(SourceRequestDto messageDto) {
        ValueContainer valueContainer = sourceContextExecutor.startContext(messageDto);
        return messageDto.toOutput(valueContainer);
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
        return new RemoteProducer(identifier, messageDto.getSenderServiceURL(),
                ValueContainerFactory.getSimpleContainer(messageDto.getTransmittedValues()), consumerId);
    }

}
