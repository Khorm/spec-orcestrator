package com.petra.lib.variable.container;

import com.petra.lib.constructor.model.ValueDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ValueContainerFactory {

    public static ValueContainer getSimpleContainer() {
        return new ValueContainerImpl();
    }

    public static ValueContainer getSimpleContainer(List<ValueDto> jsonValues) {
        if (jsonValues != null && !jsonValues.isEmpty()) {
            Collection<ValueModel> valueModels = new ArrayList<>();
            for (ValueDto valueDto : jsonValues) {
                valueModels.add(new ValueModel(valueDto.getId(), valueDto.getName(), valueDto.getMultiplicityEnm(),null ));
            }
            return new ValueContainerImpl(valueModels);
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

    public static ValueContainer getImmutableContainer(List<ValueModel> jsonValues) {
        return new ImmutableValueContainer(getSimpleContainer(jsonValues));
    }

}
