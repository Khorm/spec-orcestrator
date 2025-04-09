package com.petra.lib.block.workflow.orchestrator;

import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.enums.BlockManager;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.block.workflow.model.ActionWorkflowHistory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Orchestrator {

    private final ClientBlock startBlock;
    private final Map<Identifier, ClientBlock> blocksById;

    public Orchestrator(ClientBlock startBlock, Map<Identifier, ClientBlock> blocksById) {
        this.startBlock = startBlock;
        this.blocksById = blocksById;
    }

    public void start(ActionContext actionContext, ExecuteCallback executeCallback, Collection<ActionWorkflowHistory> executedModels) {

        //если пустой, запустить с самого начала
        if (executedModels.isEmpty()) {
            executeCallback.executeNext(actionContext, null);
            return;
        }

        //если есть активности с ошибкой
        boolean isExecutionNotOk = executedModels.stream().anyMatch(actionWorkflowHistory -> actionWorkflowHistory.getStatus() != ExecutionStatus.OK);
        if (isExecutionNotOk) {
            executeCallback.error(null, actionContext);
            return;
        }

        //найти последний блок и запустить его
        Set<Identifier> executedBlockIds = executedModels.stream().map(actionWorkflowHistory -> new Identifier(actionWorkflowHistory.getConsumerBlockId(),
                actionWorkflowHistory.getConsumerBlockVersion())).collect(Collectors.toSet());
        Optional<ClientBlock> firstNotExecutedBlock = getNotExecutedBlockOnStart(executedBlockIds, startBlock);
        if (firstNotExecutedBlock.isEmpty()) {
            executeCallback.executeNext(actionContext, BlockManager.WORKFLOW_ORCHESTRATOR);
        } else {
            Optional<ActionWorkflowHistory> lastModel = executedModels.stream().filter(actionWorkflowHistory ->
                    actionWorkflowHistory.getConsumerBlockId().equals(firstNotExecutedBlock.get().getClientId().getId()) &&
                            actionWorkflowHistory.getConsumerBlockVersion().equals(firstNotExecutedBlock.get().getClientId().getVersion())).findFirst();
            Optional<ClientBlock> previousBlock = getPreviousBlock(firstNotExecutedBlock.get().getClientId(), startBlock);
            String previousValues = null;
            if (previousBlock.isPresent()) {
                for (ActionWorkflowHistory actionWorkflowHistory : executedModels) {
                    previousValues = actionWorkflowHistory.getConsumerValues();
                    break;
                }
            } else {
                previousValues = actionContext.getConsumerValuesJson();
            }
            firstNotExecutedBlock.get().request(actionContext.getScenarioId(),
                    previousValues, executeCallback);
        }


    }

    public void execute(ActionContext workflowContext, ExecuteCallback executeCallback) {
        startBlock.request(workflowContext.getScenarioId(), workflowContext.getConsumerValuesJson(), executeCallback);
    }

    public void blockAnswer(BlockResponseDto blockResponseDto, ExecuteCallback executeCallback) {
        blocksById.get(new Identifier(blockResponseDto.getConsumerBlockId(), blockResponseDto.getConsumerBlockVersion()))
                .answer(blockResponseDto, executeCallback);
    }

    private Optional<ClientBlock> getNotExecutedBlockOnStart(Set<Identifier> executedBlockIds, ClientBlock clientBlock) {
        if (clientBlock.getNextBlock() == null) {
            return Optional.empty();
        }

        if (!executedBlockIds.contains(clientBlock.getClientId())) {
            return Optional.of(clientBlock);
        } else {
            return getNotExecutedBlockOnStart(executedBlockIds, clientBlock.getNextBlock());
        }
    }

    private Optional<ClientBlock> getPreviousBlock(Identifier searchingBlockId, ClientBlock currentBlock) {
        if (startBlock.getClientId().equals(searchingBlockId)) {
            return Optional.empty();
        } else if (currentBlock.getNextBlock().getClientId().equals(searchingBlockId)) {
            return Optional.of(currentBlock);
        } else {
            return getPreviousBlock(searchingBlockId, currentBlock.getNextBlock());
        }
    }


}
