package com.petra.lib.operation.operations.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.model.ValueModel;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class UserActionContextImpl implements UserActionContext {
    private final EntityManager entityManager;
    private final ValueContainer contextContainer;
    private final Map<String, ValueModel> contextValues;

    public UserActionContextImpl(EntityManager entityManager,
                                 List<Value> contextValues) {
        this.entityManager = entityManager;
        this.contextContainer = ValueContainerFactory.getSimpleContainer(contextValues.stream().map(Value::getModel).collect(Collectors.toList()));
        this.contextValues = contextValues.stream().collect(Collectors.toMap(Value::getName, Value::getModel));
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
        ValueModel filledOuterValue = contextValues.get(variableName);
        ObjectMapper oj = new ObjectMapper();
        try {
            filledOuterValue.setJsonVariable(oj.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            throw new RuntimeException("Variable with name " + variableName + " not found");
        }
        contextContainer.setValue(ValueFactory.createValue(filledOuterValue));
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    ValueContainer getContextContainer(){
        return contextContainer;
    }


}
