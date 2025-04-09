package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValueContainer {

    private static final ObjectMapper oj = new ObjectMapper();
    private final Map<Long, Value> valuesById;
    private final Map<String, Value> valuesByName;


    public ValueContainer(String JSONValues) {
        if (JSONValues == null || JSONValues.isBlank()){
            valuesByName = new HashMap<>();
            valuesById = new HashMap<>();
            return;
        }

        List<Value> values;
        try {
            values = oj.readValue(JSONValues, oj.getTypeFactory().constructCollectionType(List.class, Value.class));
            valuesById = values.stream().collect(Collectors.toMap(Value::getId, Function.identity()));
            valuesByName = values.stream().collect(Collectors.toMap(Value::getName, Function.identity()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ValueContainer(){
        valuesByName = new HashMap<>();
        valuesById = new HashMap<>();
    }

//    private ValueContainer(Map<Long, Value> valuesById, Map<String, Value> valuesByName){
//        this.valuesById = new HashMap<>(valuesById);
//        this.valuesByName = valuesByName;
//    }

//    public void setValue(Value value){
//        valuesById.put(value.getId(), value);
//        valuesByName.put(value.getName(), value);
//    }

    public Value getValue(long id){
        return valuesById.get(id);
    }

    public Value getValue(String name){
        return valuesByName.get(name);
    }

    public String toJson()  {
        try {
            return oj.writeValueAsString(valuesById.values());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(Value value){
        valuesById.put(value.getId(), value);
        valuesByName.put(value.getName(), value);
    }

    public void forEach(Consumer<Value> consumer){
        valuesById.forEach(new BiConsumer<>() {
            @Override
            public void accept(Long aLong, Value value) {
                consumer.accept(value);
            }
        });
    }

    public boolean isEmpty(){
        return valuesById.isEmpty();
    }

//    public ValueContainer copy(){
//        return new ValueContainer(valuesById);
//    }

//    public String getCurrentValuesGroovyJson() {
//        StringBuilder ret = new StringBuilder();
//        for (Value value : valuesById.values()){
//            ret.append("def ")
//                    .append(value.getName())
//                    .append(" = '")
//                    .append(value.getJsonValue())
//                    .append("' ; ");
//        }
//        return ret.toString();
//    }
}
