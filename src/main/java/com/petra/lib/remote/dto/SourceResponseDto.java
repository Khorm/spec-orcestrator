package com.petra.lib.remote.dto;

import com.petra.lib.variable.container.ValueDto;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class SourceResponseDto {
    private UUID scenarioId;

    private Long consumerSourceId;

    private String consumerSourceVersion ;

    private List<ValueDto> consumerSourceResultValue;




    public SourceResponseDto(UUID scenarioId, Long consumerSourceId, String consumerSourceVersion,
                             List<ValueDto> consumerSourceResultValue) {
        this.scenarioId = scenarioId;
        this.consumerSourceId = consumerSourceId;
        this.consumerSourceVersion = consumerSourceVersion;
        this.consumerSourceResultValue = consumerSourceResultValue;
//        this.producerBlockId = producerBlockId;
//        this.producerVersion = producerVersion;
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

    public List<ValueDto> getConsumerSourceResultValue() {
        return consumerSourceResultValue;
    }

//    public Long getProducerBlockId() {
//        return producerBlockId;
//    }
//
//    public String getProducerVersion() {
//        return producerVersion;
//    }
}
