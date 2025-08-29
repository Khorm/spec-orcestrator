package com.petra.lib.variable.context;

import com.petra.lib.variable.loader.ValueLoader;

import java.util.Collection;

public class ValueContextModel {
    private final int valuesCount;
    private final Collection<ValueLoader> starterLoaders;

    public ValueContextModel(int valuesCount, Collection<ValueLoader> starterLoaders) {
        this.valuesCount = valuesCount;
        this.starterLoaders = starterLoaders;
    }

    public int getValuesCount() {
        return valuesCount;
    }

    public Collection<ValueLoader> getStarterLoaders() {
        return starterLoaders;
    }
}
