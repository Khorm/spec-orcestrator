package com.petra.lib.variable.context;

import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ValueContextManager {

    Collection<ValueLoader> starterLoaders;
    Collection<ValueLoader> valueLoaders;

    public void start(ValueContainer blockContainer,
                      UUID scenarioId, VariableCallback variableCallback) {

        ValueContext valueContext = new ValueContext(blockContainer,
                scenarioId,variableCallback, valueLoaders);

        for (ValueLoader valueLoader : starterLoaders){
            valueLoader.load(valueContext);
        }
    }

}
