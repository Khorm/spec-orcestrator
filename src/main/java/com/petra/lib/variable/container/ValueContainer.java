package com.petra.lib.variable.container;

import java.util.List;

public interface ValueContainer {

    ValueDto getValue(Long id);

    ValueDto getValue(String name);

    void addValue(ValueDto value);

    void setValueJson(Long id, String json);

    void setValueJson(String name, String json);

    List<ValueDto> getValues();

    <T> List<T> getParsedList(String name, Class<T> clazz);

    <T> T getParsedValue(String name, Class<T> clazz);

    ValueContainer clone();

    String toDBJson();

    boolean containsValue(String name);
}
