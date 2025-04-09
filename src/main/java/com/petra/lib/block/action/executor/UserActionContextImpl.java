package com.petra.lib.block.action.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.variable.value.Value;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public
class UserActionContextImpl implements UserActionContext {

    EntityManager entityManager;
    ActionContext actionContext;


    @Override
    public <T> T getValue(String variableName, Class<T> clazz) {
        try {
            return actionContext.getValue(variableName).getParsedValue(clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(String variableName, Object value) {
        Value findValue = actionContext.getValue(variableName);
        actionContext.setValue(new Value(findValue.getId(), findValue.getMultiplicity(), findValue.getName(), value));
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }


}
