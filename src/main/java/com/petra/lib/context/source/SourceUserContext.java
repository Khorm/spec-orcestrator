package com.petra.lib.context.source;

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
        return inputValues.getValue(name).getParsedValue(clazz);
    }

    public <T> List<T> getListValue(String name, Class<T> clazz) {
        return inputValues.getValue(name).getParsedList(clazz);
    }

    public void setValue(String name, Object value) {
        outputValues.setValue(name, value);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    ValueContainer getOutputValues(){
        return outputValues;
    }
}
