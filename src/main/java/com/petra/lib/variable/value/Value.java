package com.petra.lib.variable.value;

import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.container.ValueDto;

import java.util.List;

public interface Value {
    <T> List<T> getParsedList(Class<T> clazz);
    <T> T getParsedValue(Class<T> clazz);

    String getExtractedJsonValue(String extractionString);
    void setValue(Object value);
    void setJsonValue(String jsonValue);
    ValueDto getModel();

    Long getId();
    String getName();

    Multiplicity getMultiplicity();
}
