package com.petra.lib.operation.operations;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Отвечает за ответ об успешном или неуспешном выполнении
 */
public class AnswerOperation implements Operation {

    private static final Logger log = LogManager.getLogger(AnswerOperation.class);
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
                log.info("Answer sent successfully for scenarioId: {}, blockId: {}",
                        blockContext.getScenarioId(), blockContext.getCurrentBlockId().toString());

            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                log.error("Failed to send answer for scenarioId: {}, blockId: {}. Error: {}",
                        blockContext.getScenarioId(),
                        blockContext.getCurrentBlockId().toString(),
                        e.getMessage(), e);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                sender.answerBlockExecution(messageDto, blockContext.getProducer().getServiceName(), this);
            }
        };

        log.debug("Sending answer for scenarioId: {}, producer: {}",
                blockContext.getScenarioId(), blockContext.getProducer().getServiceName());

        sender.answerBlockExecution(messageDto, blockContext.getProducer().getServiceName(), senderCallback);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
