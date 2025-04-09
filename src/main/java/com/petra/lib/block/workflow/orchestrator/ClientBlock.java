package com.petra.lib.block.workflow.orchestrator;

import com.petra.lib.PetraException;
import com.petra.lib.block.ExecuteCallback;
import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.enums.BlockManager;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.block.workflow.ActionWorkflowRepo;
import com.petra.lib.sender.Sender;
import com.petra.lib.sender.SenderCallback;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.VariableManager;
import com.petra.lib.variable.value.ValueContainer;
import org.springframework.transaction.annotation.Isolation;

import java.util.UUID;

public class ClientBlock {
    private final Identifier clientId;
    private final String producerServiceUrl;
    private final ClientBlock nextBlock;
    private final String clientName;
    private final Sender sender;
    private final String consumerServiceURL;
    private final ActionWorkflowRepo actionWorkflowRepo;
    private final TransactionManager transactionManager;
    private final VariableManager blockVariableManager;
    private final Identifier workflowId;


    public ClientBlock(Identifier clientId, String producerServiceUrl, ClientBlock nextBlock,
                       String clientName, Sender sender, String consumerServiceURL,
                       ActionWorkflowRepo actionWorkflowRepo, TransactionManager transactionManager,
                       VariableManager blockVariableManager, Identifier workflowId) {
        this.clientId = clientId;
        this.producerServiceUrl = producerServiceUrl;
        this.nextBlock = nextBlock;
        this.clientName = clientName;
        this.sender = sender;
        this.consumerServiceURL = consumerServiceURL;
        this.actionWorkflowRepo = actionWorkflowRepo;
        this.transactionManager = transactionManager;
        this.blockVariableManager = blockVariableManager;
        this.workflowId = workflowId;
    }


    public void request(UUID scenarioId, String previousBlockValues, ExecuteCallback executeCallback) {

        blockVariableManager.execute(previousBlockValues, scenarioId, new VariableCallback() {
            @Override
            public void loaded(ValueContainer valueContainer) {
                BlockRequestDto blockRequestDto = new BlockRequestDto(
                        scenarioId,
                        clientId.getId(),
                        clientId.getVersion(),
                        valueContainer.toJson(),
                        producerServiceUrl,
                        workflowId.getId(),
                        workflowId.getVersion()

                );

                sender.sendToBlock(blockRequestDto, consumerServiceURL, new SenderCallback<Void>() {
                    @Override
                    public void answer(Void dto) {
                        //await answer from bloque
                    }

                    @Override
                    public void error(Exception e) {
                        boolean isNotRepeated = saveStatus(scenarioId, workflowId, null, ExecutionStatus.ERROR);
                        if (!isNotRepeated) {
                            return;
                        }
                        executeCallback.error(e, scenarioId);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                executeCallback.error(e, scenarioId);
            }
        });
    }

    public void answer(BlockResponseDto blockResponseDto, ExecuteCallback executeCallback) {
        if (blockResponseDto.getStatus() == ExecutionStatus.OK) {
            boolean isNotRepeated = saveStatus(blockResponseDto.getScenarioId(), workflowId, blockResponseDto.getConsumerBlockValues(), ExecutionStatus.OK);
            if (!isNotRepeated) {
                return;
            }

            if (nextBlock != null) {
                nextBlock.request(blockResponseDto.getScenarioId(), blockResponseDto.getConsumerBlockValues(), executeCallback);
            } else {
                executeCallback.executeNext(blockResponseDto.getScenarioId(), BlockManager.WORKFLOW_ORCHESTRATOR);
            }
        } else {
            boolean isNotRepeated = saveStatus(blockResponseDto.getScenarioId(), workflowId, null, ExecutionStatus.ERROR);
            if (!isNotRepeated) {
                return;
            }
            executeCallback.error(new PetraException("Error in block " + clientName), blockResponseDto.getScenarioId());
        }
    }

    public Identifier getClientId() {
        return clientId;
    }

    public ClientBlock getNextBlock() {
        return nextBlock;
    }

    private boolean saveStatus(UUID scenarioId, Identifier workflowId, String consumerValues, ExecutionStatus executionStatus) {
        return transactionManager.executeInTransaction(transaction -> {
            if (!actionWorkflowRepo.isHistoryAlreadyExist(scenarioId, clientId, workflowId)) {
                actionWorkflowRepo.insertHistory(scenarioId, clientId, workflowId, consumerValues, executionStatus);
                return true;
            } else {
                return false;
            }
        }, Isolation.SERIALIZABLE);
    }


}
