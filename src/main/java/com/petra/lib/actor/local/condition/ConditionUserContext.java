package com.petra.lib.actor.local.condition;

import javax.persistence.EntityManager;
import java.util.List;

public interface ConditionUserContext {
    void setFirst();

    void setSecond();

    void setThird();

    void setFourth();

    void set(int index);

    EntityManager getEntityManage();

    <T> T getValue(String variableName, Class<T> clazz);

    <T> List<T> getValueList(String variableName, Class<T> clazz);
}
