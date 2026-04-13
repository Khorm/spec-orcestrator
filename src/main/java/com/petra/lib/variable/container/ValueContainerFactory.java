package com.petra.lib.variable.container;

import com.petra.lib.constructor.model.ValueModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ValueContainerFactory {

    public static ValueContainer getSimpleContainer() {
        return new ValueContainerImpl();
    }

    public static ValueContainer getSimpleContainer(List<ValueModel> models) {
        if (models != null && !models.isEmpty()) {
            Collection<ValueDto> valueDtos = new ArrayList<>();
            for (ValueModel valueModel : models) {
                valueDtos.add(new ValueDto(valueModel.getId(), valueModel.getName(), valueModel.getMultiplicityEnm(),null ));
            }
            return new ValueContainerImpl(valueDtos);
        } else {
            return new ValueContainerImpl();
        }
    }


    public static ValueContainer getSimpleContainer(Collection<ValueDto> dtos){
        if (dtos != null) {
            return new ValueContainerImpl(dtos);
        }else {
            return new ValueContainerImpl();
        }
    }

    public static ValueContainer getImmutableContainer(ValueContainer valueContainer) {
        return new ImmutableValueContainer(valueContainer);
    }

    public static ValueContainer getImmutableContainer(List<ValueDto> jsonValues) {
        return new ImmutableValueContainer(getSimpleContainer(jsonValues));
    }

}
