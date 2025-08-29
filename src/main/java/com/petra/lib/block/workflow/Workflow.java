package com.petra.lib.block.workflow;

import com.petra.lib.block.Block;
import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.context.block.operations.AnswerOperation;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.block.enums.BlockManager;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.block.workflow.model.ActionWorkflowHistory;
import com.petra.lib.block.workflow.orchestrator.Orchestrator;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.transaction.annotation.Isolation;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class Workflow implements Block, ExecuteCallback {

    private final ThreadController threadController;
    private final Identifier workflowId;
    private final ContextRepo contextRepo;
    private final ActionWorkflowRepo actionWorkflowRepo;
    private final AnswerOperation answerOperation;
    private final TransactionManager transactionManager;
    private final Sender sender;
    private final Orchestrator orchestrator;

    public Workflow(ThreadController threadController, Identifier workflowId,
                    ContextRepo contextRepo, ActionWorkflowRepo actionWorkflowRepo,
                    AnswerOperation answerOperation, TransactionManager transactionManager, Sender sender, Orchestrator orchestrator) {
        this.threadController = threadController;
        this.workflowId = workflowId;
        this.contextRepo = contextRepo;
        this.actionWorkflowRepo = actionWorkflowRepo;
        this.answerOperation = answerOperation;
        this.transactionManager = transactionManager;
        this.sender = sender;
        this.orchestrator = orchestrator;
    }

    @Override
    public void execute(BlockRequestDto blockRequestDto) {

        Identifier producerId = new Identifier(blockRequestDto.getProducerBlockId(), blockRequestDto.getProducerBlockVersion());
        ActionContext workflowContext = new ActionContext(blockRequestDto.getScenarioId(), workflowId,
                blockRequestDto.getProducerServiceUrl(), producerId,
                BlockType.WORKFLOW, blockRequestDto.getBlockValues());


        transactionManager.executeInTransaction(transaction -> {
            Optional<LoadedContext> loadedContextOpt = contextRepo.findContext(blockRequestDto.getScenarioId(), workflowId);
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
                contextRepo.createContext(workflowContext, BlockType.WORKFLOW);
                executeNext(workflowContext, null);
            }
        }, Isolation.SERIALIZABLE);
    }

    @Override
    public void start() {
        Collection<LoadedContext> contexts = contextRepo.findNotCompletedContexts(workflowId, BlockType.WORKFLOW);
        for (LoadedContext loadedContext : contexts) {
            if (loadedContext.getConsumerStatus() != ContextState.DONE) {
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
    public void answer(MessageDto messageDto) {
        orchestrator.blockAnswer(messageDto, this);
    }

    @Override
    public void executeNext(ActionContext actionContext, BlockManager executedManager) {
        threadController.executeLimitedPoolTask(() -> {

            try {
                if (executedManager == null) {
                    orchestrator.execute(actionContext, this);
                } else if (executedManager == BlockManager.WORKFLOW_ORCHESTRATOR) {
                    answerOperation.finish(actionContext, this, ExecutionStatus.OK);
                } else if (executedManager == BlockManager.FINISH) {
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
        Optional<LoadedContext> loadedContextOpt = contextRepo.findContext(scenarioId, workflowId);
        if (loadedContextOpt.isEmpty()) {
            throw new NullPointerException("Context not found in workflow " + scenarioId);
        }

        executeNext(loadedContextOpt.get(), executedManager);
    }

    @Override
    public void error(Exception e, UUID scenarioId) {
        Optional<LoadedContext> loadedContextOpt = contextRepo.findContext(scenarioId, workflowId);
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
