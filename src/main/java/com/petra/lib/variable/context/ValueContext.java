package com.petra.lib.variable.context;

import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValueContext {

    //предзагруженные переменные  непосредственно в текущем блоке в лоадерах
    private final ValueContainer blockContextValues;


    //переменные, загружающиеся из контекста воркфлоу
    private final ValueContainer workflowContextValues;

    //лоадеры
    private final Map<Long, ValueLoader> valueLoaders;

    //менеджер, который хранить айди всех переменных и айди загруженных переменных
    private final LoadedValuesManager loadedValuesManager;
    private final VariableCallback variableCallback;

    private final UUID scenarioId;



    public ValueContext(ValueContainer workflowContextValues,
                        UUID scenarioId,
                        VariableCallback variableCallback,
                        Collection<ValueLoader> valueLoaders, Collection<ValueModel> loadValues) {
        this.workflowContextValues = workflowContextValues;
        this.variableCallback = variableCallback;

        Set<Long> allValues = valueLoaders.stream()
                .mapToLong(ValueLoader::getVariableId).boxed().collect(Collectors.toSet());
        allValues.addAll(workflowContextValues.getValues().stream()
                .mapToLong(ValueDto::getId).boxed().collect(Collectors.toSet()));
        this.loadedValuesManager = new LoadedValuesManager(allValues,
                workflowContextValues.getValues().stream()
                        .mapToLong(ValueDto::getId).boxed().collect(Collectors.toSet()));

        this.scenarioId = scenarioId;
        this.valueLoaders = valueLoaders.stream().collect(Collectors.toMap(ValueLoader::getVariableId, Function.identity()));
        blockContextValues = ValueContainerFactory.getSimpleContainerByModels(loadValues);
    }


    public synchronized ValueDto getValue(Long valueId) {
        ValueDto ret = workflowContextValues.getValue(valueId);
        if (ret == null) {
            ret = blockContextValues.getValue(valueId);
        }
        return ret;
    }

    public synchronized boolean registerLoadedValue(ValueDto value) {
        blockContextValues.addValue(value);
        loadedValuesManager.registerLoadedValue(value.getId());
        if (loadedValuesManager.isValueAcceptToExecute()) {
            variableCallback.loaded(blockContextValues);
            return true;
        }
        return false;
    }

    public ValueLoader getValueLoader(Long valueId) {
        return valueLoaders.get(valueId);
    }

    /**
     * Проверяент можно ли загружать переменную или не все паренты еще загружены
     * @param valueIds
     * @param requestValueId
     * @return
     */
    public synchronized boolean isValueAcceptToExecute(List<Long> valueIds, Long requestValueId) {
        return loadedValuesManager.isValueAcceptToExecute(valueIds, requestValueId);
    }

    public void error(Exception e) {
        variableCallback.error(e);
    }

    public UUID getScenarioId() {
        return scenarioId;
    }
}
