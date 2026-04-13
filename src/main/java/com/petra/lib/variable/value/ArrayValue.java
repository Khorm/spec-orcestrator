package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petra.lib.PetraException;
import com.petra.lib.variable.container.ValueDto;

import java.util.List;

class ArrayValue extends ValueAbs{


    ArrayValue(ValueDto model) {
        super(model);
    }

    @Override
    public <T> List<T> getParsedList(Class<T> clazz) {
        try {
            return oj.readValue(model.getJsonValue(), oj.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getParsedValue(Class<T> clazz) {
        throw new PetraException("Trying to get list from Object value " + model.getName());
    }

}
