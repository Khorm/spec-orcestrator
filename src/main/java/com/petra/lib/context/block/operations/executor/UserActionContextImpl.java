package com.petra.lib.context.block.operations.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.model.ValueModel;
import com.petra.lib.variable.value.ValueFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;


public class UserActionContextImpl implements UserActionContext {

    private final EntityManager entityManager;
    private final ValueContainer contextContainer;
    private final Map<String, ValueModel> contextOuterValues;

    public UserActionContextImpl(EntityManager entityManager, ValueContainer contextContainer,
                                 Map<String, ValueModel> contextOuterValues) {
        this.entityManager = entityManager;
        this.contextContainer = contextContainer;
        this.contextOuterValues = contextOuterValues;
    }

    @Override
    public <T> T getValue(String variableName, Class<T> clazz) {
        return contextContainer.getValue(variableName).getParsedValue(clazz);
    }

    @Override
    public <T> List<T> getValueList(String variableName, Class<T> clazz) {
        return contextContainer.getValue(variableName).getParsedList(clazz);
    }

    @Override
    public void setValue(String variableName, Object value) {
        ValueModel filledOuterValue = contextOuterValues.get(variableName);
        ObjectMapper oj = new ObjectMapper();
        try {
            filledOuterValue.setJsonVariable(oj.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        contextContainer.setValue(ValueFactory.createValue(filledOuterValue));
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }


}
