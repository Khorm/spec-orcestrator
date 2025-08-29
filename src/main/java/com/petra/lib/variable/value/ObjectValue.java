package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petra.lib.PetraException;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.List;

/**
 * переменная, хранящая объект
 */
class ObjectValue extends ValueAbs {

    private transient Object objValue;


    ObjectValue(long id, String JSONvalue, Multiplicity multiplicity, String name) {
        super(JSONvalue, id, name, multiplicity);
    }

    @Override
    public <T> List<T> getParsedList(Class<T> clazz) {
        throw new PetraException("Trying to get list from Object value " + name);
    }

    public <T> T getParsedValue(Class<T> clazz) {
        if (objValue == null) {
            try {
                objValue = oj.readValue(JSONvalue, clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return (T) objValue;
    }

}
