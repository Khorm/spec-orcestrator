package com.petra.lib.block.workflow;

import com.petra.lib.block.Block;
import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.block.action.finisher.FinishManager;
import com.petra.lib.block.action.repo.ActionRepo;
import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.enums.BlockManager;
import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.block.workflow.model.ActionWorkflowHistory;
import com.petra.lib.block.workflow.orchestrator.Orchestrator;
import com.petra.lib.sender.Sender;
import com.petra.lib.sender.SenderCallback;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.transaction.annotation.Isolation;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class Workflow implements Block, ExecuteCallback {

    private final ThreadController threadController;
    private final Identifier workflowId;
    private final ActionRepo actionRepo;
    private final ActionWorkflowRepo actionWorkflowRepo;
    private final FinishManager finishManager;
    private final TransactionManager transactionManager;
    private final Sender sender;
    private final Orchestrator orchestrator;

    public Workflow(ThreadController threadController, Identifier workflowId,
                    ActionRepo actionRepo, ActionWorkflowRepo actionWorkflowRepo,
                    FinishManager finishManager, TransactionManager transactionManager, Sender sender, Orchestrator orchestrator) {
        this.threadController = threadController;
        this.workflowId = workflowId;
        this.actionRepo = actionRepo;
        this.actionWorkflowRepo = actionWorkflowRepo;
        this.finishManager = finishManager;
        this.transactionManager = transactionManager;
        this.sender = sender;
        this.orchestrator = orchestrator;
    }

    @Override
    public void execute(BlockRequestDto blockRequestDto) {

        Identifier producerId = new Identifier(blockRequestDto.getProducerBlockId(), blockRequestDto.getProducerBlockVersion());
        ActionContext workflowContext = new ActionContext(blockRequestDto.getScenarioId(), workflowId,
                blockRequestDto.getProducerServiceUrl(), producerId,
                HistoryType.WORKFLOW, blockRequestDto.getBlockValues());


        transactionManager.executeInTransaction(transaction -> {
            Optional<LoadedContext> loadedContextOpt = actionRepo.findContext(blockRequestDto.getScenarioId(), workflowId);
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
                actionRepo.createContext(workflowContext, HistoryType.WORKFLOW);
                executeNext(workflowContext, null);
            }
        }, Isolation.SERIALIZABLE);
    }

    @Override
    public void start() {
        Collection<LoadedContext> contexts = actionRepo.findNotCompletedContexts(workflowId, HistoryType.WORKFLOW);
        for (LoadedContext loadedContext : contexts) {
            if (loadedContext.getConsumerStatus() != BlockState.DONE) {
                if (loadedContext.getConsumerContextValues().isEmpty()) {
                    executeNext(loadedContext, null);
                } else {
                    Collection<ActionWorkflowHistory> actionWorkflowHistories = actionWorkflowRepo.getModels(loadedContext.getScenarioId(), workflowId);
                    orchestrator.start(loadedContext, this, actionWorkflowHistories);
                }
            }
        }
    }

    @Override
    public void answer(BlockResponseDto blockResponseDto) {
        orchestrator.blockAnswer(blockResponseDto, this);
    }

    @Override
    public void executeNext(ActionContext actionContext, BlockManager executedManager) {
        threadController.executeLimitedPoolTask(() -> {

            try {
                if (executedManager == null) {
//                    variableManager.execute(actionContext, this);
//                } else if (executedManager == BlockManager.VARIABLE_MANAGER) {
                    orchestrator.execute(actionContext, this);
                } else if (executedManager == BlockManager.WORKFLOW_ORCHESTRATOR) {
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
        Optional<LoadedContext> loadedContextOpt = actionRepo.findContext(scenarioId, workflowId);
        if (loadedContextOpt.isEmpty()) {
            throw new NullPointerException("Context not found in workflow " + scenarioId);
        }

        executeNext(loadedContextOpt.get(), executedManager);
    }

    @Override
    public void error(Exception e, UUID scenarioId) {
        Optional<LoadedContext> loadedContextOpt = actionRepo.findContext(scenarioId, workflowId);
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
