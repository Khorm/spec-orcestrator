package com.petra.lib.operation.operations.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.value.Value;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;


public class UserActionContextImpl implements UserActionContext {
    private final EntityManager entityManager;
    private final ValueContainer inputContainer;
    private final ValueContainer outputValues;

    public UserActionContextImpl(EntityManager entityManager,
                                 List<Value> inputValues, ValueContainer outputValues) {
        this.entityManager = entityManager;
        this.inputContainer = ValueContainerFactory.getSimpleContainer(inputValues.stream().map(Value::getModel)
                .collect(Collectors.toList()));
        this.outputValues = outputValues;
    }

    @Override
    public <T> T getValue(String variableName, Class<T> clazz) {
        if (inputContainer.containsValue(variableName)){
            return inputContainer.getValue(variableName).getParsedValue(clazz);
        }else if (outputValues.containsValue(variableName)){
            return outputValues.getValue(variableName).getParsedValue(clazz);
        }
        return null;
//        return contextContainer.getValue(variableName).getParsedValue(clazz);
    }

    @Override
    public <T> List<T> getValueList(String variableName, Class<T> clazz) {
        if (inputContainer.containsValue(variableName)){
            return inputContainer.getValue(variableName).getParsedList(clazz);
        }else if (outputValues.containsValue(variableName)){
            return outputValues.getValue(variableName).getParsedList(clazz);
        }
        return null;
//        return contextContainer.getValue(variableName).getParsedList(clazz);
    }

    @Override
    public void setValue(String variableName, Object value) {
        Value filledOuterValue = outputValues.getValue(variableName);
        ObjectMapper oj = new ObjectMapper();
        try {
            filledOuterValue.setJsonValue(oj.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            throw new RuntimeException("Variable with name " + variableName + " not found");
        }
//        contextContainer.setValue(ValueFactory.createValue(filledOuterValue));
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ValueContainer getOutputValues() {
        return outputValues;
    }
}
