package com.petra.lib.variable.container;

import com.petra.lib.variable.value.Value;

import java.util.List;

public interface ValueContainer {

    Value getValue(Long id);
    Value getValue(String name);

    void setValue(Value value);

    void setValue(String name, Object object);

    List<ValueModel> getModels();

    List<Value> getValues();
//    void mixinValueContainer(ValueContainer valueContainer);

    ValueContainer clone();

    String toDBJson();
    boolean containsValue(String name);
}
