package com.petra.lib.block.dto;

import java.util.UUID;

public class SourceRequestDto {
    private UUID scenarioId;

    private Long consumerSourceId;

    private String consumerSourceVersion;

    private String producerValues;

    private String producerServiceUrl;

    private Long producerBlockId;

    private String producerBlockVersion;

    public SourceRequestDto(UUID scenarioId, Long consumerSourceId,
                            String consumerSourceVersion, String producerValues,
                            String producerServiceUrl, Long producerBlockId,
                            String producerBlockVersion) {
        this.scenarioId = scenarioId;
        this.consumerSourceId = consumerSourceId;
        this.consumerSourceVersion = consumerSourceVersion;
        this.producerValues = producerValues;
        this.producerServiceUrl = producerServiceUrl;
        this.producerBlockId = producerBlockId;
        this.producerBlockVersion = producerBlockVersion;
    }

    public SourceRequestDto() {
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public Long getConsumerSourceId() {
        return consumerSourceId;
    }

    public String getConsumerSourceVersion() {
        return consumerSourceVersion;
    }

    public String getProducerValues() {
        return producerValues;
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
}
