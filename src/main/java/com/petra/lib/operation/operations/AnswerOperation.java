package com.petra.lib.operation.operations;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;

/**
 * Отвечает за ответ об успешном или неуспешном выполнении
 */
public class AnswerOperation implements Operation {

    private final Sender sender;
    private final ContextState CURRENT_STATE = ContextState.ANSWERED;


    public AnswerOperation(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void execute(Context blockContext) {
        MessageDto messageDto = new MessageDto(
                blockContext.getScenarioId(),
                blockContext.getProducer().getId(),
                blockContext.getProducer().getVersion(),
                blockContext.getContextOutValues().getModels(),
                blockContext.getCurrentBlockId().getId(),
                blockContext.getCurrentBlockId().getVersion(),
                blockContext.getProducerServiceName(),
                blockContext.getExecutionStatus()
        );

        SenderCallback senderCallback = new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                blockContext.setState(CURRENT_STATE);
                blockContext.save();
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                sender.answerAboutBlockExecution(messageDto, blockContext.getProducer().getServiceName(), this);
            }
        };

        sender.answerAboutBlockExecution(messageDto, blockContext.getProducer().getServiceName(), senderCallback);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
