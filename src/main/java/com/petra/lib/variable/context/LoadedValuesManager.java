package com.petra.lib.variable.context;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.HashSet;

/**
 * Регистрирует уже загруженые переменные
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
class LoadedValuesManager {
    Collection<Long> loadedValues = new HashSet<>();
    Collection<Long> allValues;


    void registerLoadedValue(Long valueId) {
        if (loadedValues.contains(valueId)) {
            throw new IllegalStateException("Value is already loaded " + valueId);
        }
        loadedValues.add(valueId);
    }

    public boolean areValuesLoaded(Collection<Long> values) {
        return loadedValues.containsAll(values);
    }

    boolean areValuesLoaded() {
        return loadedValues.equals(allValues);
    }

}
