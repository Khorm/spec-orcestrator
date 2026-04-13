package com.petra.lib.variable.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class ValueContainerImpl implements ValueContainer {

    private final Map<Long, Value> valuesById;
    private final Map<String, Value> valuesByName;


    ValueContainerImpl(List<ValueDto> valueDtos) {
        if (valueDtos == null || valueDtos.isEmpty()) {
            valuesByName = new HashMap<>();
            valuesById = new HashMap<>();
            return;
        }

        List<Value> values = getValues(valueDtos);
        valuesById = values.stream().collect(Collectors.toMap(Value::getId, Function.identity()));
        valuesByName = values.stream().collect(Collectors.toMap(Value::getName, Function.identity()));
    }

    ValueContainerImpl(Collection<ValueDto> valueDtos) {
        valuesById = new HashMap<>();
        valuesByName = new HashMap<>();
        for (ValueDto valueDto : valueDtos) {
            Value value = ValueFactory.createValue(valueDto);
            valuesById.put(valueDto.getId(), value);
            valuesByName.put(valueDto.getName(), value);
        }
    }

    ValueContainerImpl() {
        valuesByName = new HashMap<>();
        valuesById = new HashMap<>();
    }

    public void mixinValueContainer(ValueContainer valueContainer) {
        if (valueContainer == null) return;
        mixinValues(valueContainer.getValues());
    }

    @Override
    public ValueContainer clone() {
        return ValueContainerFactory.getSimpleContainer(getModels());
    }

    @Override
    public String toDBJson() {
        ObjectMapper oj = new ObjectMapper();
        Collection<ValueDto> models = valuesById.values().stream().map(Value::getModel).collect(Collectors.toList());

        try {
            return oj.writeValueAsString(models);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(String name) {
        return valuesByName.containsKey(name);
    }


    private void mixinValues(List<Value> values) {
        values = values.stream().filter(value -> !valuesById.containsKey(value.getId())).collect(Collectors.toList());
        Map<Long, Value> newValuesById = values.stream().collect(Collectors.toMap(Value::getId, Function.identity()));
        valuesById.putAll(newValuesById);
        Map<String, Value> newValuesByName = values.stream().collect(Collectors.toMap(Value::getName, Function.identity()));
        valuesByName.putAll(newValuesByName);
    }


    @Override
    public Value getValue(Long id) {
        return valuesById.get(id);
    }

    @Override
    public Value getValue(String name) {
        return valuesByName.get(name);
    }

    @Override
    public void setValue(Value value) {
        valuesById.put(value.getId(), value);
        valuesByName.put(value.getName(), value);
    }

    @Override
    public void setValue(String name, Object object) {
        valuesByName.get(name).setValue(object);
//        oldVal.setValue(object);
//        Value newVal = ValueFactory.createValue(oldVal.getId(), oldVal.getName(), oldVal.getMultiplicity(), object);
//        setValue(newVal);
    }


    @Override
    public List<ValueDto> getModels() {
        return valuesById.values().stream().map(Value::getModel).collect(Collectors.toList());
    }

    @Override
    public List<Value> getValues() {
        return new ArrayList<>(valuesById.values());
    }

    private List<Value> getValues(List<ValueDto> values) {
        return values.stream().map(ValueFactory::createValue).collect(Collectors.toList());
    }

}
