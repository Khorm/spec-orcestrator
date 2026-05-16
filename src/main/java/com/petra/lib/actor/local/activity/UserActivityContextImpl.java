package com.petra.lib.actor.local.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;

import javax.persistence.EntityManager;
import java.util.List;


public class UserActivityContextImpl implements UserActivityContext {
    private final EntityManager entityManager;
    private final ValueContainer inputContainer;
    private final ValueContainer outputValues;

    public UserActivityContextImpl(EntityManager entityManager,
                                   List<ValueDto> inputValues, List<ValueModel> outputValues) {
        this.entityManager = entityManager;
        this.inputContainer = ValueContainerFactory.getSimpleContainerByDtos(inputValues);
        this.outputValues = ValueContainerFactory.getSimpleContainerByModels(outputValues);
    }

    @Override
    public <T> T getValue(String variableName, Class<T> clazz) {
        if (inputContainer.containsValue(variableName)) {
            return inputContainer.getParsedValue(variableName, clazz);
        } else if (outputValues.containsValue(variableName)) {
            return outputValues.getParsedValue(variableName, clazz);
        }
        return null;
    }

    @Override
    public <T> List<T> getValueList(String variableName, Class<T> clazz) {
        if (inputContainer.containsValue(variableName)) {
            return inputContainer.getParsedList(variableName, clazz);
        } else if (outputValues.containsValue(variableName)) {
            return outputValues.getParsedList(variableName, clazz);
        }
        return null;
    }

    @Override
    public void setValue(String variableName, Object value) {
        ObjectMapper oj = new ObjectMapper();
        try {
            outputValues.setValueJson(variableName, oj.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Variable with name " + variableName + " not found");
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ValueContainer getOutputValues() {
        return outputValues;
    }
}
