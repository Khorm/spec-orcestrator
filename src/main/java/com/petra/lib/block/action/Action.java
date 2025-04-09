package com.petra.lib.block.action;

import com.petra.lib.block.Block;
import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.block.action.executor.UserExecutor;
import com.petra.lib.block.action.finisher.FinishManager;
import com.petra.lib.block.action.repo.ActionRepo;
import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.enums.BlockManager;
import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.Sender;
import com.petra.lib.sender.SenderCallback;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.transaction.annotation.Isolation;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

class Action implements Block, ExecuteCallback {


    private final ThreadController threadController;
    private final Identifier actionId;
    private final ActionRepo actionRepo;
    private final UserExecutor userExecutor;
    private final FinishManager finishManager;
    private final TransactionManager transactionManager;
    private final Sender sender;


    Action(ThreadController threadController, Identifier actionId, ActionRepo actionRepo,
           UserExecutor userExecutor, FinishManager finishManager, TransactionManager transactionManager, Sender sender) {
        this.threadController = threadController;
        this.actionId = actionId;
        this.actionRepo = actionRepo;
        this.userExecutor = userExecutor;
        this.finishManager = finishManager;
        this.transactionManager = transactionManager;
        this.sender = sender;
    }


    @Override
    public void execute(BlockRequestDto blockRequestDto) {
        Identifier producerId = new Identifier(blockRequestDto.getProducerBlockId(), blockRequestDto.getProducerBlockVersion());
        ActionContext actionContext = new ActionContext(blockRequestDto.getScenarioId(), actionId,
                blockRequestDto.getProducerServiceUrl(), producerId,
                HistoryType.ACTION, blockRequestDto.getBlockValues());

        transactionManager.executeInTransaction(transaction -> {
            Optional<LoadedContext> loadedContextOpt = actionRepo.findContext(blockRequestDto.getScenarioId(), actionId);
            if (loadedContextOpt.isPresent()) {
                LoadedContext loadedContext = loadedContextOpt.get();
                if (loadedContext.getConsumerStatus() == BlockState.DONE) {
                    BlockResponseDto blockResponseDto = new BlockResponseDto(
                            loadedContext.getScenarioId(),
                            loadedContext.getConsumerBlockId(),
                            loadedContext.getConsumerValuesJson(),
                            loadedContext.getProducerBlockId(),
                            loadedContext.getExecutionStatus()
                    );
                    sender.answerFromBlock(blockResponseDto, loadedContext.getProducerServiceUrl(), new SenderCallback<Void>() {
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
                actionRepo.createContext(actionContext, HistoryType.ACTION);
                executeNext(actionContext, null);
            }
        }, Isolation.SERIALIZABLE);

    }

    @Override
    public void start() {
        Collection<LoadedContext> contexts = actionRepo.findNotCompletedContexts(actionId, HistoryType.ACTION);
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
    public void answer(BlockResponseDto blockResponseDto) {
        // do nothin
    }


    @Override
    public void executeNext(ActionContext actionContext, BlockManager executedManager) {
        threadController.executeLimitedPoolTask(() -> {
            try {
                if (executedManager == null) {
//                    variableManager.execute(actionContext, this);
//                } else if (executedManager == BlockManager.VARIABLE_MANAGER) {
                    userExecutor.execute(actionContext, this);
                } else if (executedManager == BlockManager.USER_HANDLER_MANAGER) {
                    finishManager.finish(actionContext, this, ExecutionStatus.OK);
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
        Optional<LoadedContext> loadedContextOpt = actionRepo.findContext(scenarioId, actionId);
        if (loadedContextOpt.isEmpty()) {
            throw new NullPointerException("Context not found in workflow " + scenarioId);
        }

        executeNext(loadedContextOpt.get(), executedManager);
    }

    @Override
    public void error(Exception e, UUID scenarioId) {
        Optional<LoadedContext> loadedContextOpt = actionRepo.findContext(scenarioId, actionId);
        if (loadedContextOpt.isEmpty()) {
            throw new NullPointerException("Context not found in workflow " + scenarioId);
        }

        finishManager.finish(loadedContextOpt.get(), this, ExecutionStatus.ERROR);
    }

    @Override
    public void error(Exception e, ActionContext actionContext) {
        finishManager.finish(actionContext, this, ExecutionStatus.ERROR);
    }
}
