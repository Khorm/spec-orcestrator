package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petra.lib.PetraException;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.List;

class ArrayValue extends ValueAbs{

    private transient List<?> objValueList;

    ArrayValue(String jsonValue, Long id, String name, Multiplicity multiplicity) {
        super(jsonValue, id, name, multiplicity);
    }

    @Override
    public <T> List<T> getParsedList(Class<T> clazz) {
        if (objValueList == null){
            try {
                objValueList = oj.readValue(JSONvalue, oj.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return (List<T>) objValueList;
    }

    @Override
    public <T> T getParsedValue(Class<T> clazz) {
        throw new PetraException("Trying to get list from Object value " + name);
    }


}
