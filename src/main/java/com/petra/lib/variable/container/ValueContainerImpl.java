package com.petra.lib.variable.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.model.ValueModel;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class ValueContainerImpl implements ValueContainer {

    private static final ObjectMapper oj = new ObjectMapper();
    private final Map<Long, Value> valuesById;
    private final Map<String, Value> valuesByName;


    ValueContainerImpl(String JSONValues) {
        if (JSONValues == null || JSONValues.isBlank()) {
            valuesByName = new HashMap<>();
            valuesById = new HashMap<>();
            return;
        }

        List<Value> values = getValues(JSONValues);
        valuesById = values.stream().collect(Collectors.toMap(Value::getId, Function.identity()));
        valuesByName = values.stream().collect(Collectors.toMap(Value::getName, Function.identity()));
    }

    ValueContainerImpl(Collection<ValueModel> valueModels) {
        valuesById = new HashMap<>();
        valuesByName = new HashMap<>();
        for (ValueModel valueModel : valueModels) {
            Value value = ValueFactory.createValue(valueModel);
            valuesById.put(valueModel.getId(), value);
            valuesByName.put(valueModel.getName(), value);
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
        return ValueContainerFactory.getSimpleContainer(getJson());
    }

//    public void mixValues(String JSONValues) {
//        List<Value> values = getValues(JSONValues);
//        mixinValues(values);
//    }


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
        Value oldVal = valuesByName.get(name);
        Value newVal = ValueFactory.createValue(oldVal.getId(), oldVal.getName(), oldVal.getMultiplicity(), object);
        setValue(newVal);
    }

    @Override
    public String getJson() {
        try {
            Collection<ValueModel> valueModels = valuesById.values().stream().map(Value::getModel).collect(Collectors.toList());
            return oj.writeValueAsString(valueModels);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Value> getValues() {
        return new ArrayList<>(valuesById.values());
    }

    private List<Value> getValues(String JSONValues) {
        return parse(JSONValues).stream().map(ValueFactory::createValue).collect(Collectors.toList());
    }

    private List<ValueModel> parse(String JSONValues) {
        try {
            return oj.readValue(JSONValues, oj.getTypeFactory().constructCollectionType(List.class, ValueModel.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
