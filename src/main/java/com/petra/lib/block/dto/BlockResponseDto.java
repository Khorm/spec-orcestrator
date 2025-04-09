package com.petra.lib.block.dto;

import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.model.Identifier;

import java.util.UUID;

public class BlockResponseDto {
    private final UUID scenarioId;

    private final Long consumerBlockId;

    private final String consumerBlockVersion;

    private final String consumerBlockValues;

    private final Long producerBlockId;

    private final String producerBlockVersion;

    private final ExecutionStatus status;

    public BlockResponseDto(UUID scenarioId, Identifier consumerBlockId,
                            String consumerBlockValues, Identifier producerBlockId, ExecutionStatus status) {
        this.scenarioId = scenarioId;
        this.consumerBlockId = consumerBlockId.getId();
        this.consumerBlockVersion = consumerBlockId.getVersion();
        this.consumerBlockValues = consumerBlockValues;
        this.producerBlockId = producerBlockId.getId();
        this.producerBlockVersion = producerBlockId.getVersion();
        this.status = status;
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

    public String getConsumerBlockValues() {
        return consumerBlockValues;
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
}
