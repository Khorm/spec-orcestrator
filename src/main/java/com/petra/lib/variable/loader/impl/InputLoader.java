package com.petra.lib.variable.loader.impl;

import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;
import java.util.Optional;

class InputLoader extends LoaderAbs {

    private final long inputValueId;
    private final long variableId;
    private final String name;
    private final Optional<String> extractionString;
    private final Multiplicity multiplicity;

    InputLoader(Long inputValueId, List<ValueLoader> childrenValues,
                Long variableId, List<Long> parentValues, String name, String extractionString, Multiplicity multiplicity,
                ThreadController threadController) {
        super(childrenValues, parentValues, threadController);
        this.inputValueId = inputValueId;
        this.variableId = variableId;
        this.name = name;
        this.extractionString = extractionString == null || extractionString.isBlank() ?
                Optional.empty() : Optional.of(extractionString);
        this.multiplicity = multiplicity;
    }

    @Override
    protected void executeLoad(ValueContext context) {
        Value producerValue = context.getValue(inputValueId);
        if (producerValue.getMultiplicity() != multiplicity) {
            throw new IllegalArgumentException("Wrong producer multiplicity for value " + name);
        }
        String newJsonValue;
        if (extractionString.isPresent()) {
            newJsonValue = producerValue.getExtractedJsonValue(extractionString.get());
        } else {
            newJsonValue = producerValue.getJsonValue();
        }
        Value contextValue = ValueFactory.createValue(variableId, name, producerValue.getMultiplicity(), newJsonValue);
        context.setValue(contextValue, this);
    }

}
