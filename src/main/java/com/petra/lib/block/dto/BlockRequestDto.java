package com.petra.lib.block.dto;

import java.util.UUID;


public class BlockRequestDto {
    private UUID scenarioId;
    private Long consumerBlockId;
    private  String consumerBlockVersion;
    private String blockValues;
    private String producerServiceUrl;
    private Long producerBlockId;
    private String producerBlockVersion;

    public BlockRequestDto(UUID scenarioId, Long consumerBlockId, String consumerBlockVersion, String blockValues,
                           String producerServiceUrl, Long producerBlockId, String producerBlockVersion) {
        this.scenarioId = scenarioId;
        this.consumerBlockId = consumerBlockId;
        this.consumerBlockVersion = consumerBlockVersion;
        this.producerServiceUrl = producerServiceUrl;
        this.producerBlockId = producerBlockId;
        this.producerBlockVersion = producerBlockVersion;
        this.blockValues = blockValues;
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

    public String getProducerServiceUrl() {
        return producerServiceUrl;
    }

    public Long getProducerBlockId() {
        return producerBlockId;
    }

    public String getProducerBlockVersion() {
        return producerBlockVersion;
    }

    public String getBlockValues() {
        return blockValues;
    }
}
