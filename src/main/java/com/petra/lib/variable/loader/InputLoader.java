package com.petra.lib.variable.loader;

import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.value.Value;

import java.util.List;

class InputLoader implements ValueLoader {

    private final long inputValueId;
    private final long variableId;
    private final List<ValueLoader> childValues;
    private final List<Long> parentValues;
    private final String name;

    InputLoader(long inputValueId, List<ValueLoader> childrenValues,
                long variableId, List<Long> parentValues, String name) {
        this.inputValueId = inputValueId;
        this.variableId = variableId;
        this.childValues = childrenValues;
        this.parentValues = parentValues;
        this.name = name;
    }

    @Override
    public void load(ValueContext context) {
        if (!context.areValuesLoaded(parentValues)) return;

        Value sigValue = context.getInputValue(inputValueId);
        context.setValue(new Value(variableId, sigValue.getValue(), sigValue.getMultiplicity(), name));

        try {
            for (ValueLoader valueLoader : childValues) {
                valueLoader.load(context);
            }
        } catch (Exception e) {
            context.error(e);
        }
    }
}
