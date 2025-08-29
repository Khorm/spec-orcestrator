package com.petra.lib.context.block.operations;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.BlockContext;
import com.petra.lib.context.operation.Operation;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;

/**
 * Отвечает за ответ об успешном или неуспешном выполнении
 */
public class AnswerOperation implements Operation<BlockContext> {

    private final Sender sender;
    private final ContextState CURRENT_STATE = ContextState.ANSWERED;
    private final String currentServiceName;


    public AnswerOperation(Sender sender, String currentServiceName) {
        this.sender = sender;
        this.currentServiceName = currentServiceName;
    }

    @Override
    public void execute(BlockContext blockContext) {
        MessageDto messageDto = new MessageDto(
                blockContext.getScenarioId(),
                blockContext.getProducer().getId(),
                blockContext.getProducer().getVersion(),
                blockContext.getContextOutputContainer().getJson(),
                blockContext.getConsumer().getId(),
                blockContext.getConsumer().getVersion(),
                currentServiceName,
                blockContext.getExecutionStatus()
        );

        SenderCallback senderCallback = new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                blockContext.setState(null, CURRENT_STATE);
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                sender.requestBlockExecution(messageDto, blockContext.getProducer().getServiceName(), this);
            }
        };

        sender.requestBlockExecution(messageDto, blockContext.getProducer().getServiceName(), senderCallback);


    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
