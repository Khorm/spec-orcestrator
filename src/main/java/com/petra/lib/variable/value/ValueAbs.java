package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.utils.JsonUtils;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.enums.Multiplicity;

abstract class ValueAbs implements Value {

    protected ValueModel model;
    protected final ObjectMapper oj = new ObjectMapper();

    ValueAbs(ValueModel model) {
        this.model = model;
    }

    public String getExtractedJsonValue(String extractionString) {
        if (model.getMultiplicity() == Multiplicity.COLLECTION) {
            throw new UnsupportedOperationException("Extraction is not aloud on COLLECTION in variable " + model.getName());
        }
        return JsonUtils.getExtractedJsonValue(extractionString, model.getJsonValue());
    }

    protected synchronized void updateModel(String jsonValue) {
        model = new ValueModel(model.getId(), model.getName(), model.getMultiplicity(), jsonValue);
    }

    @Override
    public ValueModel getModel() {
        return model;
    }

    @Override
    public void setValue(Object value) {
        try {
            updateModel(oj.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setJsonValue(String jsonValue) {
        updateModel(jsonValue);
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
