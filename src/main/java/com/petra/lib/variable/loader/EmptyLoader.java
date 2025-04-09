package com.petra.lib.variable.loader;

import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.value.Multiplicity;
import com.petra.lib.variable.value.Value;

import java.util.List;

class EmptyLoader implements ValueLoader{

    private final long variableId;
    private final List<ValueLoader> childValues;
    private final List<Long> parentValues;
    private final String name;
    private final Multiplicity multiplicity;

    EmptyLoader(long variableId, List<ValueLoader> childValues, List<Long> parentValues, String name, Multiplicity multiplicity) {
        this.variableId = variableId;
        this.childValues = childValues;
        this.parentValues = parentValues;
        this.name = name;
        this.multiplicity = multiplicity;
    }

    @Override
    public void load(ValueContext context) {
        context.setValue(new Value(variableId, null, multiplicity, name));
        try {
            for (ValueLoader valueLoader : childValues) {
                valueLoader.load(context);
            }
        } catch (Exception e) {
            context.error(e);
        }
    }
}
