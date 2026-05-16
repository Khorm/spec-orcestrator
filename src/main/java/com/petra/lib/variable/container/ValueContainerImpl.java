package com.petra.lib.variable.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class ValueContainerImpl implements ValueContainer {

    private final Map<Long, Value> valuesById;
    private final Map<String, Value> valuesByName;


    ValueContainerImpl(Collection<ValueDto> valueDtos) {
        if (valueDtos == null || valueDtos.isEmpty()) {
            valuesByName = new HashMap<>();
            valuesById = new HashMap<>();
            return;
        }

        List<Value> values = getValues(valueDtos);
        valuesById = values.stream().collect(Collectors.toMap(Value::getId, Function.identity()));
        valuesByName = values.stream().collect(Collectors.toMap(Value::getName, Function.identity()));
    }

    ValueContainerImpl(ValueModel... variableModels) {
        valuesByName = new HashMap<>();
        valuesById = new HashMap<>();
        if (variableModels == null || List.of(variableModels).isEmpty()) {
            return;
        }

        Collection<Value> values = Arrays.stream(variableModels)
                .map(ValueFactory::createValue).collect(Collectors.toList());


        values.forEach(value -> {
            valuesById.put(value.getId(), value);
            valuesByName.put(value.getName(), value);
        });

    }

//    ValueContainerImpl(Collection<ValueDto> valueDtos) {
//        valuesById = new HashMap<>();
//        valuesByName = new HashMap<>();
//        for (ValueDto valueDto : valueDtos) {
//            Value value = ValueFactory.createValue(valueDto);
//            valuesById.put(valueDto.getId(), value);
//            valuesByName.put(valueDto.getName(), value);
//        }
//    }

    ValueContainerImpl() {
        valuesByName = new HashMap<>();
        valuesById = new HashMap<>();
    }

//    public void mixinValueContainer(ValueContainer valueContainer) {
//        if (valueContainer == null) return;
//        mixinValues(valueContainer.getValues());
//    }

    @Override
    public ValueContainer clone() {
        return ValueContainerFactory.getSimpleContainerByDtos(getValues());
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


//    private void mixinValues(List<Value> values) {
//        values = values.stream().filter(value -> !valuesById.containsKey(value.getId())).collect(Collectors.toList());
//        Map<Long, Value> newValuesById = values.stream().collect(Collectors.toMap(Value::getId, Function.identity()));
//        valuesById.putAll(newValuesById);
//        Map<String, Value> newValuesByName = values.stream().collect(Collectors.toMap(Value::getName, Function.identity()));
//        valuesByName.putAll(newValuesByName);
//    }


//    @Override
//    public Value getValue(Long id) {
//        return valuesById.get(id);
//    }
//
//    @Override
//    public Value getValue(String name) {
//        return valuesByName.get(name);
//    }

//    @Override
//    public void addValue(Value value) {
//        valuesById.put(value.getId(), value);
//        valuesByName.put(value.getName(), value);
//    }

//    @Override
//    public void addValue(ValueDto value) {
//        Value v = ValueFactory.createValue(value);
//        valuesById.put(value.getId(), v);
//        valuesByName.put(value.getName(), v);
//    }


    @Override
    public ValueDto getValue(Long id) {
        return valuesById.get(id).getModel();
    }

    @Override
    public ValueDto getValue(String name) {
        return valuesByName.get(name).getModel();
    }

    @Override
    public void addValue(ValueDto value) {
        valuesByName.put(value.getName(), ValueFactory.createValue(value));
    }

    @Override
    public void setValueJson(Long id, String json) {
        valuesById.get(id).setJsonValue(json);
    }

    @Override
    public void setValueJson(String name, String json) {
        valuesByName.get(name).setJsonValue(json);
    }


    @Override
    public List<ValueDto> getValues() {
        return valuesById.values().stream().map(Value::getModel).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> getParsedList(String name, Class<T> clazz) {
        return valuesByName.get(name).getParsedList(clazz);
    }

    @Override
    public <T> T getParsedValue(String name, Class<T> clazz) {
        return valuesByName.get(name).getParsedValue(clazz);
    }

//    @Override
//    public List<Value> getValues() {
//        return new ArrayList<>(valuesById.values());
//    }

    private List<Value> getValues(Collection<ValueDto> values) {
        return values.stream().map(ValueFactory::createValue).collect(Collectors.toList());
    }

}
