package com.petra.lib.remote.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.List;
import java.util.UUID;

public class SourceRequestDto {
    private UUID scenarioId;

    private Long consumerSourceId;

    private String consumerSourceVersion;

    private List<ValueModel> inputValues;

    @JsonCreator
    SourceRequestDto(
            @JsonProperty("scenarioId") UUID scenarioId,
            @JsonProperty("consumerSourceId") Long consumerSourceId,
            @JsonProperty("consumerSourceVersion") String consumerSourceVersion,
            @JsonProperty("inputValues") List<ValueModel> inputValues) {
        this.scenarioId = scenarioId;
        this.consumerSourceId = consumerSourceId;
        this.consumerSourceVersion = consumerSourceVersion;
        this.inputValues = inputValues;
    }

    public SourceRequestDto(UUID scenarioId, Long consumerSourceId,
                            String consumerSourceVersion, ValueContainer inputValues) {
        this.scenarioId = scenarioId;
        this.consumerSourceId = consumerSourceId;
        this.consumerSourceVersion = consumerSourceVersion;
        this.inputValues = inputValues.getModels();
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

    @JsonProperty("inputValues")
    List<ValueModel> getInputs(){
        return inputValues;
    }

    @JsonIgnore
    public ValueContainer getInputValues() {
        return ValueContainerFactory.getSimpleContainer(inputValues);
    }

    public SourceResponseDto toOutput(ValueContainer outputValue){
        return new SourceResponseDto(scenarioId,consumerSourceId,consumerSourceVersion,
                outputValue.getModels());
    }
}
