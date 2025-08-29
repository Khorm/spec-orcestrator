package com.petra.lib.variable.value;

import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.model.ValueModel;

import java.util.List;

public interface Value {
    <T> List<T> getParsedList(Class<T> clazz);
    <T> T getParsedValue(Class<T> clazz);

    String getExtractedJsonValue(String extractionString);

    String getJsonValue();

    Multiplicity getMultiplicity();
    Long getId();
    String getName();

    ValueModel getModel();
}
