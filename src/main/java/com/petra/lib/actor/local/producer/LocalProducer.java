package com.petra.lib.actor.local.producer;

import com.petra.lib.actor.local.LocalConsumer;
import com.petra.lib.actor.remote.consumer.LinkedConsumerListMessage;
import com.petra.lib.actor.remote.consumer.RemoteConsumer;
import com.petra.lib.actor.remote.consumer.RemoteConsumerExecResult;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LocalProducer implements LocalConsumer {

    Identifier workflowId;
    String workflowName;
    Collection<ValueModel> inputVariables;
    RemoteConsumer firstRemoteConsumer;

    ContextService contextService;
    TransactionManager transactionManager;

    @Override
    public void execute(Context blockContext, OperationService operationService) {
        Optional<WorkflowContext> workflowContextOpt = contextService.createWorkflowContext(workflowId, blockContext.getScenarioId(),
                blockContext.getContextInputValues());

        log.info("{} Start executing workflow {}", blockContext.getScenarioId(), workflowName);

        LinkedConsumerListMessage message = new LinkedConsumerListMessage(null, null,
                blockContext.getScenarioId(),
                blockContext.getContextInputValues().clone(),
                this::answerFromBlock,
                operationService
        );
        firstRemoteConsumer.execute(message);

    }

    @Override
    public BlockType getBlockType() {
        return BlockType.WORKFLOW;
    }

    @Override
    public Collection<ValueModel> getInputVariables() {
        return inputVariables;
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId,
                                ExecutionStatus execResult, OperationService operationService) {
        if (execResult == ExecutionStatus.ERROR) {
            error(scenarioId, operationService);
            return;
        }

        Context workflowBlockContext = contextService.loadContext(workflowId, scenarioId);
        LinkedConsumerListMessage message = new LinkedConsumerListMessage(answerBlockId,
                answerContainer,
                scenarioId,
                workflowBlockContext.getContextInputValues().clone(),
                this::answerFromBlock,
                operationService
        );

        firstRemoteConsumer.handleAnswer(message);

    }

    private void error(UUID scenarioId, OperationService operationService) {
        Context context = contextService.loadContext(workflowId, scenarioId);
        transactionManager.executeInTransaction(tr -> {
            context.lockAndLoad(tr);
            boolean stateResult = context.setState(ContextState.EXECUTED);
            if (stateResult) {
                context.setExecutionStatus(ExecutionStatus.ERROR);
            } else {
                tr.rollback();
                return;
            }
            context.save(tr);

            WorkflowContext wfCtx = contextService.loadWorkflowContext(workflowId, context.getScenarioId());
            wfCtx.lockAndLoad(tr);
            wfCtx.setState(WorkflowContextState.ERROR);
            wfCtx.save(tr);

            operationService.executeState(context);
        });
    }


    @Override
    public Identifier getId() {
        return workflowId;
    }

    @Override
    public String getName() {
        return workflowName;
    }


}
