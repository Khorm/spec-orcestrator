package com.petra.lib.variable.value;

import com.petra.lib.PetraException;
import com.petra.lib.variable.GenericParsers;
import com.petra.lib.variable.container.ValueDto;

import java.util.List;

class SimpleValue extends ValueAbs{


    SimpleValue(ValueDto model) {
        super(model);
    }

    @Override
    public <T> List<T> getParsedList(Class<T> clazz) {
        throw new PetraException("Trying to get list from Object value " + model.getName());
    }

    @Override
    public <T> T getParsedValue(Class<T> clazz) {
        return (T) GenericParsers.getParser(clazz).apply(model.getJsonValue());
    }

}
