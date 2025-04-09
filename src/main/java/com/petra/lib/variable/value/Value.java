package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


public class Value {
    private static final ObjectMapper oj = new ObjectMapper();
    private final String value;
    private final long id;

    private final String name;
    private final Multiplicity multiplicity;

    private transient List objValueList;
    private transient Object objValue;


    public Value(long id, String value, Multiplicity multiplicity, String name) {
        this.value = value;
        this.id = id;
        this.name = name;
        this.multiplicity = multiplicity;
    }

    public Value(long id, Multiplicity multiplicity, String name, Object value) {
        this.objValue = value;
        this.id = id;
        this.name = name;
        this.multiplicity = multiplicity;
        try {
            this.value = oj.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    <T> List<T> getParsedList(Class<T> clazz) throws JsonProcessingException {
        if (objValueList == null){
            objValueList = oj.readValue(value, oj.getTypeFactory().constructCollectionType(List.class, clazz));
        }
        return objValueList;
    }

    public <T> T getParsedValue(Class<T> clazz) throws JsonProcessingException {
        if (objValue == null){
            objValue = oj.readValue(value, clazz);
        }
        return (T) objValue;
    }

//    public void setValue(Object value) throws JsonProcessingException {
//        oj.writeValueAsString(value);
//        objValue = null;
//        objValueList = null;
//    }

    public String getJsonValue(){
        return value;
    }

//    public Value clone(){
//        Value cloneVal = new Value();
//        cloneVal.id = this.id;
//        cloneVal.value = this.value;
//        cloneVal.name = this.name;
//        cloneVal.multiplicity = this.multiplicity;
//        cloneVal.objValue = this.objValue;
//        cloneVal.objValueList = this.objValueList;
//        return cloneVal;
//    }



}
