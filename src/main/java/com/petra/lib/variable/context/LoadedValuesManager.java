package com.petra.lib.variable.context;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Регистрирует уже загруженые переменные
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
class LoadedValuesManager {
    Set<Long> allValues;
    Set<Long> loadedValues;

    // id Запросов на загрузку
    Set<Long> requestedValueIds = new HashSet<>();


    void registerLoadedValue(Long valueId) {
        if (loadedValues.contains(valueId)) {
            throw new IllegalStateException("Value is already loaded " + valueId);
        }
        loadedValues.add(valueId);
    }

    public boolean isValueAcceptToExecute(Collection<Long> parents, Long requestValueId) {
        boolean allParentsReady = loadedValues.containsAll(parents);
        if (allParentsReady){
            if (requestedValueIds.contains(requestValueId)){
                return false;
            }
            requestedValueIds.add(requestValueId);
            return true;
        }
        return false;
    }

    boolean isValueAcceptToExecute() {
        return loadedValues.equals(allValues);
    }

}
