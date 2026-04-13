package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.utils.JsonUtils;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.enums.Multiplicity;

abstract class ValueAbs implements Value {

    protected ValueDto model;
    protected final ObjectMapper oj = new ObjectMapper();

    ValueAbs(ValueDto model) {
        this.model = model;
    }

    public String getExtractedJsonValue(String extractionString) {
        if (model.getMultiplicity() == Multiplicity.COLLECTION) {
            throw new UnsupportedOperationException("Extraction is not aloud on COLLECTION in variable " + model.getName());
        }
        return JsonUtils.getExtractedJsonValue(extractionString, model.getJsonValue());
    }

    protected synchronized void updateValue(String jsonValue) {
        model = new ValueDto(model.getId(), model.getName(), model.getMultiplicity(), jsonValue);
    }

    @Override
    public ValueDto getModel() {
        return model;
    }

    @Override
    public void setValue(Object value) {
        try {
            updateValue(oj.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setJsonValue(String jsonValue) {
        updateValue(jsonValue);
    }

    @Override
    public Long getId(){
        return model.getId();
    }

    @Override
    public String getName(){
        return model.getName();
    }

    @Override
    public Multiplicity getMultiplicity(){
        return model.getMultiplicity();
    }

}
