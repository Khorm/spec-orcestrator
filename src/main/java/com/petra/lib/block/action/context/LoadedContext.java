package com.petra.lib.block.action.context;

import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;

import java.util.UUID;

public class LoadedContext extends ActionContext{

    private final BlockState consumerStatus;
    private final ExecutionStatus executionStatus;

    public LoadedContext(UUID scenarioId, Identifier consumerBlockId, String producerServiceUrl,
                         Identifier producerBlockId, String values,
                         HistoryType historyType, BlockState consumerStatus, ExecutionStatus executionStatus) {
        super(scenarioId, consumerBlockId, producerServiceUrl, producerBlockId, historyType, values);
        this.consumerStatus = consumerStatus;
        this.executionStatus = executionStatus;
    }

    public BlockState getConsumerStatus() {
        return consumerStatus;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
}
