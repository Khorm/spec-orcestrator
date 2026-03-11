package com.petra.lib.variable.context;

import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValueContext {

    private final ValueContainer valueContextValues;

    private final ValueContainer blockContextValues;
    private final VariableCallback variableCallback;
    private final LoadedValuesManager loadedValuesManager;
    private final UUID scenarioId;

    private final Map<Long, ValueLoader> valueLoaders;

    public ValueContext(ValueContainer blockContextValues,
                        UUID scenarioId,
                        VariableCallback variableCallback,
                        Collection<ValueLoader> valueLoaders) {
        this.blockContextValues = blockContextValues;
        this.variableCallback = variableCallback;
        this.loadedValuesManager = new LoadedValuesManager(valueLoaders.stream()
                .mapToLong(ValueLoader::getVariableId).boxed().collect(Collectors.toSet()));
        this.scenarioId = scenarioId;
        this.valueLoaders = valueLoaders.stream().collect(Collectors.toMap(ValueLoader::getVariableId, Function.identity()));
        valueContextValues = ValueContainerFactory.getSimpleContainer();
    }


    public synchronized Value getValue(Long valueId) {
        Value ret = blockContextValues.getValue(valueId);
        if (ret == null) {
            ret = valueContextValues.getValue(valueId);
        }
        return ret;
    }

    public synchronized boolean registerLoadedValue(Value value) {
        valueContextValues.setValue(value);
        loadedValuesManager.registerLoadedValue(value.getId());
        if (loadedValuesManager.areValuesLoaded()) {
            variableCallback.loaded(valueContextValues);
            return true;
        }
        return false;
    }

    public ValueLoader getValueLoader(Long valueId) {
        return valueLoaders.get(valueId);
    }

    public boolean areValuesLoaded(List<Long> valueIds) {
        return loadedValuesManager.areValuesLoaded(valueIds);
    }

    public void error(Exception e) {
        variableCallback.error(e);
    }

    public UUID getScenarioId() {
        return scenarioId;
    }
}
