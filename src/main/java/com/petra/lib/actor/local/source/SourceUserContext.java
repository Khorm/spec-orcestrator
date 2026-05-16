package com.petra.lib.actor.local.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.container.ValueContainer;

import javax.persistence.EntityManager;
import java.util.List;

public class SourceUserContext {
    private final ValueContainer inputValues;
    private final ValueContainer outputValues;
    private final EntityManager entityManager;

    SourceUserContext(ValueContainer inputValues, EntityManager entityManager, ValueContainer outputValues) {
        this.inputValues = inputValues;
        this.outputValues = outputValues;
        this.entityManager = entityManager;
    }

    public <T> T getValue(String name, Class<T> clazz) {
        return inputValues.getParsedValue(name, clazz);
    }

    public <T> List<T> getListValue(String name, Class<T> clazz) {
        return inputValues.getParsedList(name, clazz);
    }

    public void setValue(String name, Object value) {
        ObjectMapper om = new ObjectMapper();
        try {
            outputValues.setValueJson(name, om.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ValueContainer getOutputValues(){
        return outputValues;
    }
}
