package com.petra.lib.remote.dto;

import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueModel;

import java.util.List;
import java.util.UUID;

public class SourceRequestDto {
    private UUID scenarioId;

    private Long consumerSourceId;

    private String consumerSourceVersion;

    private List<ValueModel> inputValues;



    public SourceRequestDto(UUID scenarioId, Long consumerSourceId,
                            String consumerSourceVersion, ValueContainer inputValues) {
        this.scenarioId = scenarioId;
        this.consumerSourceId = consumerSourceId;
        this.consumerSourceVersion = consumerSourceVersion;
        this.inputValues = inputValues.getModels();

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

    public ValueContainer getInputValues() {
        return ValueContainerFactory.getSimpleContainer(inputValues);
    }
}
