package com.petra.lib.variable;


import com.petra.lib.constructor.model.ValueModelDto;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContextManager;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.LoaderFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 *
 */
public final class VariableFactory {


    VariableFactory() {
    }

    public static ValueContextManager createStartedLoaders(Collection<ValueModelDto> valuesCollectionModel,
                                                           ThreadController threadController, Sender sender) {
        Collection<ValueLoader> valueLoaders = LoaderFactory.createLoaders(valuesCollectionModel,threadController, sender);

        Collection<ValueLoader> starterLoaders = new ArrayList<>();
        for (ValueLoader valueLoader : valueLoaders) {
            if (valueLoader.getParents().isEmpty()) {
                starterLoaders.add(valueLoader);
            }
        }

        return new ValueContextManager(starterLoaders, valueLoaders);
    }

}
