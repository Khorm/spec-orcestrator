package com.petra.lib.block.action.finisher;

import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.action.repo.ActionRepo;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.sender.Sender;
import com.petra.lib.sender.SenderCallback;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.transaction.annotation.Isolation;

public class FinishManager {

    private final Sender sender;
    private final TransactionManager transactionManager;
    private final ActionRepo actionRepo;

    public FinishManager(Sender sender, TransactionManager transactionManager, ActionRepo actionRepo) {
        this.sender = sender;
        this.transactionManager = transactionManager;
        this.actionRepo = actionRepo;
    }

    public void finish(ActionContext actionContext, ExecuteCallback executeCallback, ExecutionStatus executionStatus){

        BlockResponseDto blockResponseDto = new BlockResponseDto(
                actionContext.getScenarioId(),
                actionContext.getConsumerBlockId(),
                actionContext.getConsumerValuesJson(),
                actionContext.getProducerBlockId(),
                executionStatus
        );
        sender.answerFromBlock(blockResponseDto, actionContext.getProducerServiceUrl(), new SenderCallback<>() {
            @Override
            public void answer(Void dto) {
                transactionManager.executeInTransaction(transaction -> {
                    BlockState blockState = actionRepo.findCurrentState(actionContext.getScenarioId(), actionContext.getConsumerBlockId());
                    switch (blockState) {
                        case START:
                            executeCallback.error(new IllegalStateException("Start status when finish"), actionContext);
                            break;
                        case EXECUTED:
                            actionRepo.updateBlockState(actionContext.getScenarioId(), actionContext.getConsumerBlockId(), BlockState.DONE);
                            actionRepo.updateExecutionStatus(actionContext.getScenarioId(), actionContext.getConsumerBlockId() ,executionStatus);
                            break;
                        case DONE:
                            break;
                    }
                }, Isolation.SERIALIZABLE);
            }

            @Override
            public void error(Exception e) {
                finish(actionContext, executeCallback, executionStatus);
            }
        });


    }
}
