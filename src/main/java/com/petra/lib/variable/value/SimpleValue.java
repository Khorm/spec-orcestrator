package com.petra.lib.variable.value;

import com.petra.lib.PetraException;
import com.petra.lib.variable.GenericParsers;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.List;

class SimpleValue extends ValueAbs{
    SimpleValue(String jsoNvalue, Long id, String name, Multiplicity multiplicity) {
        super(jsoNvalue, id, name, multiplicity);
    }

    @Override
    public <T> List<T> getParsedList(Class<T> clazz) {
        throw new PetraException("Trying to get list from Object value " + name);
    }

    @Override
    public <T> T getParsedValue(Class<T> clazz) {
        return (T) GenericParsers.getParser(clazz).apply(JSONvalue);
    }
}
