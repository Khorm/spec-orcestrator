package com.petra.lib.variable.container;

import com.petra.lib.PetraException;
import com.petra.lib.variable.value.Value;

import java.util.List;

class ImmutableValueContainer implements ValueContainer {
    private final ValueContainer valueContainer;

    public ImmutableValueContainer(ValueContainer valueContainer) {
        this.valueContainer = valueContainer;
    }

    @Override
    public Value getValue(Long id) {
        return valueContainer.getValue(id);
    }

    @Override
    public Value getValue(String name) {
        return valueContainer.getValue(name);
    }

    @Override
    public void setValue(Value value) {
        throw new PetraException("Value container is immutable");
    }

    @Override
    public void setValue(String name, Object object) {
        throw new PetraException("Value container is immutable");
    }

    @Override
    public List<ValueDto> getModels() {
        return valueContainer.getModels();
    }

    @Override
    public List<Value> getValues() {
        return valueContainer.getValues();
    }

    @Override
    public ValueContainer clone() {
        return ValueContainerFactory.getImmutableContainer(valueContainer.clone());
    }

    @Override
    public String toDBJson() {
        return valueContainer.toDBJson();
    }

    @Override
    public boolean containsValue(String name) {
        return valueContainer.containsValue(name);
    }
}
