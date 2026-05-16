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
    public ValueDto getValue(Long id) {
        return valueContainer.getValue(id);
    }

    @Override
    public ValueDto getValue(String name) {
        return valueContainer.getValue(name);
    }

    @Override
    public void addValue(ValueDto value) {
        throw new IllegalStateException("Cannot modify immutables");
    }

    @Override
    public void setValueJson(Long id, String json) {
        throw new IllegalStateException("Cannot modify immutables");
    }

    @Override
    public void setValueJson(String name, String json) {
        throw new IllegalStateException("Cannot modify immutables");
    }

    @Override
    public List<ValueDto> getValues() {
        return valueContainer.getValues();
    }

    @Override
    public <T> List<T> getParsedList(String name, Class<T> clazz) {
        return valueContainer.getParsedList(name, clazz);
    }

    @Override
    public <T> T getParsedValue(String name, Class<T> clazz) {
        return getParsedValue(name, clazz);
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
