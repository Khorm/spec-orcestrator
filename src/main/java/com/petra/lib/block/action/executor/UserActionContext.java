package com.petra.lib.block.action.executor;

import javax.persistence.EntityManager;

public interface UserActionContext {

    <T> T getValue(String variableName, Class<T> clazz);
    void setValue(String variableName, Object value);

    EntityManager getEntityManager();

}
