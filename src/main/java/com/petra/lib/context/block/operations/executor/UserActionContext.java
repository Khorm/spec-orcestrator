package com.petra.lib.context.block.operations.executor;

import javax.persistence.EntityManager;
import java.util.List;

public interface UserActionContext {

    <T> T getValue(String variableName, Class<T> clazz);

    <T> List<T> getValueList(String variableName, Class<T> clazz);

    void setValue(String variableName, Object value);

    EntityManager getEntityManager();

}
