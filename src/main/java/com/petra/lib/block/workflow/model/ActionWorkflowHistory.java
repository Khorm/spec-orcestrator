package com.petra.lib.block.workflow.model;

import com.petra.lib.block.enums.ExecutionStatus;

import java.util.UUID;

public class ActionWorkflowHistory {
    private final UUID scenarioId;
    private final Long consumerBlockId;
    private final String consumerBlockVersion;
    private final Long producerBlockId;
    private final String producerBlockVersion;

    private final ExecutionStatus status;
    private final String consumerValues;

    public ActionWorkflowHistory(UUID scenarioId, Long consumerBlockId, String consumerBlockVersion,
                                 Long producerBlockId, String producerBlockVersion, ExecutionStatus status, String consumerValues) {
        this.scenarioId = scenarioId;
        this.consumerBlockId = consumerBlockId;
        this.consumerBlockVersion = consumerBlockVersion;
        this.producerBlockId = producerBlockId;
        this.producerBlockVersion = producerBlockVersion;
        this.status = status;
        this.consumerValues = consumerValues;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public Long getConsumerBlockId() {
        return consumerBlockId;
    }

    public String getConsumerBlockVersion() {
        return consumerBlockVersion;
    }

    public Long getProducerBlockId() {
        return producerBlockId;
    }

    public String getProducerBlockVersion() {
        return producerBlockVersion;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public String getConsumerValues() {
        return consumerValues;
    }
}
