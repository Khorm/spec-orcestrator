package com.petra.lib.variable.context;

import com.petra.lib.context.Context;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.loader.ValueLoader;

import java.util.Collection;
import java.util.UUID;


public class ValueContextModel {
    private final int valuesCount;
    private final Collection<ValueLoader> starterLoaders;

    public ValueContextModel(int valuesCount, Collection<ValueLoader> starterLoaders) {
        this.valuesCount = valuesCount;
        this.starterLoaders = starterLoaders;
    }

    public void start(ValueContainer valueContainer, UUID scenarioId, VariableCallback variableCallback) {
        ValueContext valueContext = new ValueContext(valueContainer, valuesCount,
                starterLoaders, scenarioId,variableCallback);
        valueContext.start();
    }

}
