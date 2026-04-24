package com.petra.lib.operation.operations;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Отвечает за ответ об успешном или неуспешном выполнении
 */
@Log4j2
@RequiredArgsConstructor
public class AnswerOperation implements Operation {

    private final Sender sender;
    private final TransactionManager transactionManager;
    private final ContextState CURRENT_STATE = ContextState.ANSWERED;

    @Override
    public void execute(Context blockContext, OperationService operationService) {
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

        log.debug("{} Sending answer {} , producer: {}",
                blockContext.getScenarioId(), blockContext.getExecutionStatus(),
                blockContext.getProducer().getServiceName());

        SenderCallback senderCallback = new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                try(Transaction transaction = transactionManager.createNewTransaction(false, null)) {
                    blockContext.lockAndLoad(transaction);
                    boolean setStateResult = blockContext.setState(CURRENT_STATE);
                    if (setStateResult) {
                        blockContext.save(transaction);
                        transaction.commit();
                    } else {
                        transaction.rollback();
                    }
                    log.info("Answer sent successfully for scenarioId: {}",
                            blockContext.getScenarioId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                log.error("Failed to send answer for scenarioId: {},  Error: {}",
                        blockContext.getScenarioId(),
                        e);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
//                sender.answerBlockExecution(messageDto, blockContext.getProducer().getServiceName(), this);
            }
        };
        sender.answerBlockExecution(messageDto, blockContext.getProducer().getServiceName(), senderCallback);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
