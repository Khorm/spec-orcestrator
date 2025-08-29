package com.petra.lib.remote.dto;

import java.util.UUID;

public class SourceResponseDto {
    private UUID scenarioId;

    private Long consumerSourceId;

    private String consumerSourceVersion ;

    private String consumerSourceValues;

    private Long producerBlockId ;

    private String producerVersion ;


    public SourceResponseDto(){}

    public SourceResponseDto(UUID scenarioId, Long consumerSourceId, String consumerSourceVersion,
                             String consumerSourceValues, Long producerBlockId, String producerVersion) {
        this.scenarioId = scenarioId;
        this.consumerSourceId = consumerSourceId;
        this.consumerSourceVersion = consumerSourceVersion;
        this.consumerSourceValues = consumerSourceValues;
        this.producerBlockId = producerBlockId;
        this.producerVersion = producerVersion;
//        this.executionStatus = executionStatus;
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

    public String getConsumerSourceValues() {
        return consumerSourceValues;
    }

    public Long getProducerBlockId() {
        return producerBlockId;
    }

    public String getProducerVersion() {
        return producerVersion;
    }
}
