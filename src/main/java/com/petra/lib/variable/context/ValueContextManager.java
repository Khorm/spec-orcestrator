package com.petra.lib.variable.context;

import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

/**
 * Точка входа в выгрузку значений
 */

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Log4j2
public class ValueContextManager {

    Collection<ValueLoader> starterLoaders;
    Collection<ValueLoader> valueLoaders;
    String name;

    public void start(ValueContainer workflowContainer,
                      UUID scenarioId, VariableCallback variableCallback) {
        log.info("{} starting value manager in {}", scenarioId, name);

        if (starterLoaders.isEmpty()){
            variableCallback.loaded(ValueContainerFactory.getSimpleContainer());
            return;
        }

        ValueContext valueContext = new ValueContext(workflowContainer,
                scenarioId,variableCallback, Collections.unmodifiableCollection(valueLoaders));

        for (ValueLoader valueLoader : starterLoaders){
            valueLoader.load(valueContext);
        }
    }

}
