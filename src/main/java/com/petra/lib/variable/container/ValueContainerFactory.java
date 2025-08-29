package com.petra.lib.variable.container;

import com.petra.lib.variable.model.ValueModel;

import java.util.Collection;

public final class ValueContainerFactory {

    public static ValueContainer getSimpleContainer() {
        return new ValueContainerImpl();
    }

    public static ValueContainer getSimpleContainer(String jsonValues) {
        if (jsonValues != null && !jsonValues.isBlank()) {
            return new ValueContainerImpl(jsonValues);
        } else {
            return new ValueContainerImpl();
        }
    }

    public static ValueContainer getSimpleContainer(Collection<ValueModel> models){
        if (models != null) {
            return new ValueContainerImpl(models);
        }else {
            return new ValueContainerImpl();
        }
    }

    public static ValueContainer getImmutableContainer(ValueContainer valueContainer) {
        return new ImmutableValueContainer(valueContainer);
    }

    public static ValueContainer getImmutableContainer(String json) {
        return new ImmutableValueContainer(getSimpleContainer(json));
    }


}
