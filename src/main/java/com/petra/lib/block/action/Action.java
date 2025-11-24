package com.petra.lib.block.action;

import com.petra.lib.block.Block;
import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.block.enums.BlockManager;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.transaction.annotation.Isolation;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

class Action implements Block, ExecuteCallback {


    private final ThreadController threadController;
    private final Identifier actionId;
    private final ContextRepo contextRepo;
    private final BlockUserOperation blockUserOperation;
    private final AnswerOperation answerOperation;
    private final TransactionManager transactionManager;
    private final Sender sender;


    Action(ThreadController threadController, Identifier actionId, ContextRepo contextRepo,
           BlockUserOperation blockUserOperation, AnswerOperation answerOperation, TransactionManager transactionManager, Sender sender) {
        this.threadController = threadController;
        this.actionId = actionId;
        this.contextRepo = contextRepo;
        this.blockUserOperation = blockUserOperation;
        this.answerOperation = answerOperation;
        this.transactionManager = transactionManager;
        this.sender = sender;
    }


    @Override
    public void execute(BlockRequestDto blockRequestDto) {
        Identifier producerId = new Identifier(blockRequestDto.getProducerBlockId(), blockRequestDto.getProducerBlockVersion());
        ActionContext actionContext = new ActionContext(blockRequestDto.getScenarioId(), actionId,
                blockRequestDto.getProducerServiceUrl(), producerId,
                BlockType.ACTION, blockRequestDto.getBlockValues());

        transactionManager.executeInTransaction(transaction -> {
            Optional<LoadedContext> loadedContextOpt = contextRepo.findContext(blockRequestDto.getScenarioId(), actionId);
            if (loadedContextOpt.isPresent()) {
                LoadedContext loadedContext = loadedContextOpt.get();
                if (loadedContext.getConsumerStatus() == ContextState.DONE) {
                    MessageDto messageDto = new MessageDto(
                            loadedContext.getScenarioId(),
                            loadedContext.getConsumerBlockId(),
                            loadedContext.getConsumerValuesJson(),
                            loadedContext.getProducerBlockId(),
                            loadedContext.getExecutionStatus()
                    );
                    sender.answerFromBlock(messageDto, loadedContext.getProducerServiceUrl(), new SenderCallback<Void>() {
                        @Override
                        public void answer(Void dto) {
                            //do nothin
                        }

                        @Override
                        public void error(Exception e) {
                            //do nothin
                        }
                    });
                }
            } else {
                contextRepo.createContext(actionContext, BlockType.ACTION);
                executeNext(actionContext, null);
            }
        }, Isolation.SERIALIZABLE);

    }

    @Override
    public void start() {
        Collection<LoadedContext> contexts = contextRepo.findNotCompletedContexts(actionId, BlockType.ACTION);
        for (LoadedContext context : contexts) {
            switch (context.getConsumerStatus()) {
                case START:
                    executeNext(context, null);
                    break;
                case EXECUTED:
                    executeNext(context, BlockManager.USER_HANDLER_MANAGER);
                    break;
            }
        }

    }

    @Override
    public void answer(MessageDto messageDto) {
        // do nothin
    }


    @Override
    public void executeNext(ActionContext actionContext, BlockManager executedManager) {
        threadController.executeLimitedPoolTask(() -> {
            try {
                if (executedManager == null) {
//                    variableManager.execute(actionContext, this);
//                } else if (executedManager == BlockManager.VARIABLE_MANAGER) {
                    blockUserOperation.execute(actionContext, this);
                } else if (executedManager == BlockManager.USER_HANDLER_MANAGER) {
                    answerOperation.finish(actionContext, this, ExecutionStatus.OK);
                } else if (executedManager == BlockManager.FINISH) {
//                    finishManager.finish(actionContext, this, ExecutionStatus.OK);
                    throw new IllegalStateException("Workflow in state FINISH");
                }
            } catch (Exception e) {
                e.printStackTrace();
                error(e, actionContext);
            }
        });
    }

    @Override
    public void executeNext(UUID scenarioId, BlockManager executedManager) {
        Optional<LoadedContext> loadedContextOpt = contextRepo.findContext(scenarioId, actionId);
        if (loadedContextOpt.isEmpty()) {
            throw new NullPointerException("Context not found in workflow " + scenarioId);
        }

        executeNext(loadedContextOpt.get(), executedManager);
    }

    @Override
    public void error(Exception e, UUID scenarioId) {
        Optional<LoadedContext> loadedContextOpt = contextRepo.findContext(scenarioId, actionId);
        if (loadedContextOpt.isEmpty()) {
            throw new NullPointerException("Context not found in workflow " + scenarioId);
        }

        answerOperation.finish(loadedContextOpt.get(), this, ExecutionStatus.ERROR);
    }

    @Override
    public void error(Exception e, ActionContext actionContext) {
        answerOperation.finish(actionContext, this, ExecutionStatus.ERROR);
    }
}
