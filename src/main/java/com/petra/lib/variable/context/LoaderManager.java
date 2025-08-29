package com.petra.lib.variable.context;


import com.petra.lib.variable.loader.ValueLoader;

import java.util.Collection;
import java.util.HashSet;

class LoaderManager {
    private final Collection<Long> loadedValues = new HashSet<>();
    private final ValueContext valueContext;
    private final int valuesCount;

    public LoaderManager(ValueContext valueContext, int valuesCount) {
        this.valueContext = valueContext;
        this.valuesCount = valuesCount;
    }

    void addLoadedValue(ValueLoader valueLoader, Long valueId){
        if (loadedValues.contains(valueId)){
            throw new IllegalStateException("Value is already loaded " + valueId);
        }

        if (loadedValues.containsAll(valueLoader.getParentValues())){
            valueLoader.getChildValues().forEach(loader -> loader.load(valueContext));
        }
    }

    boolean areValuesLoaded(){
        return loadedValues.size() == valuesCount;
    }

}
