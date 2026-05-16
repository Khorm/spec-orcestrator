package com.petra.lib.actor.local.activity;

import javax.persistence.EntityManager;
import java.util.List;

public interface UserActivityContext {

    <T> T getValue(String variableName, Class<T> clazz);

    <T> List<T> getValueList(String variableName, Class<T> clazz);

    void setValue(String variableName, Object value);

    EntityManager getEntityManager();

}
