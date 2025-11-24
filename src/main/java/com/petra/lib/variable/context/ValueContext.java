package com.petra.lib.variable.context;

import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;

import java.util.Collection;
import java.util.UUID;

public class ValueContext {
    private final ValueContainer valueContextValues;
    private final ValueContainer blockContextValues;
    private final VariableCallback variableCallback;
    private final Collection<ValueLoader> starterLoaders;
    private final LoaderManager loaderManager;
    private final UUID scenarioId;

    public ValueContext(ValueContainer blockContextValues,
                        int valuesCount,
                        Collection<ValueLoader> starterLoaders,
                        UUID scenarioId,
                        VariableCallback variableCallback) {
        this.blockContextValues = blockContextValues;
        this.variableCallback = variableCallback;
        this.loaderManager = new LoaderManager(this, valuesCount);
        this.starterLoaders = starterLoaders;
        this.scenarioId = scenarioId;
        valueContextValues = ValueContainerFactory.getSimpleContainer();
    }

    public void start() {
        starterLoaders.forEach(loader -> loader.load(this));
    }

    public synchronized Value getValue(Long valueId) {
        Value ret = blockContextValues.getValue(valueId);
        if (ret == null) {
            ret = valueContextValues.getValue(valueId);
        }
        return ret;
    }

    public synchronized void setValue(Value value, ValueLoader valueLoader) {
        valueContextValues.setValue(value);
        loaderManager.addLoadedValue(valueLoader, value.getId());
        if (loaderManager.areValuesLoaded()) {
            variableCallback.loaded(valueContextValues);
        }
    }

    public void error(Exception e) {
        variableCallback.error(e);
    }

    public UUID getScenarioId() {
        return scenarioId;
    }
}
