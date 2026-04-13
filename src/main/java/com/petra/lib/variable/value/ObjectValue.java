package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petra.lib.PetraException;
import com.petra.lib.variable.container.ValueDto;

import java.util.List;

/**
 * переменная, хранящая объект
 */
class ObjectValue extends ValueAbs {


    ObjectValue(ValueDto model) {
        super(model);
    }

    @Override
    public <T> List<T> getParsedList(Class<T> clazz) {
        throw new PetraException("Trying to get list from Object value " + model.getName());
    }

    public <T> T getParsedValue(Class<T> clazz) {
        try {
            return oj.readValue(model.getJsonValue(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
