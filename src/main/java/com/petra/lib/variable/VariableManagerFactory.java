package com.petra.lib.variable;


import com.petra.lib.constructor.model.LocalProducerModel;
import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContextManager;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.LoaderFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 */
public final class VariableManagerFactory {


    VariableManagerFactory() {
    }

    public static ValueContextManager createStartedLoaders(RemoteConsumerModel remoteConsumerModel,
                                                           ThreadController threadController, Sender sender) {
        Collection<ValueLoader> valueLoaders = LoaderFactory.createLoaders(threadController, sender,
                remoteConsumerModel.getLoadedValues(),
                remoteConsumerModel.getContextValues());

        return createStarter(valueLoaders, remoteConsumerModel.getContextValues(), remoteConsumerModel.getConsumerName());
    }

    public static ValueContextManager createEndLoaders(LocalProducerModel localProducerModel,
                                                       ThreadController threadController, Sender sender) {
        Collection<ValueLoader> valueLoaders = LoaderFactory.createLoaders(threadController, sender,
                localProducerModel.getExitValues(),
                localProducerModel.getContextValues());
        return createStarter(valueLoaders, localProducerModel.getContextValues(), localProducerModel.getName());
    }

    private static ValueContextManager createStarter(Collection<ValueLoader> valueLoaders, Collection<ValueModel> contextValues,
                                                     String name) {
        Set<Long> notBlockModels = new HashSet<>();
        Collection<ValueModel> blockModels = new ArrayList<>();
        for (ValueModel model : contextValues) {
            boolean find = false;
            for (ValueLoader loader : valueLoaders) {
                if (loader.getVariableId().equals(model.getId())) {
                    find = true;
                    break;
                }
            }
            if (!find){
                notBlockModels.add(model.getId());
            }else {
                blockModels.add(model);
            }
        }

        Collection<ValueLoader> starterLoaders = new ArrayList<>();
        for (ValueLoader valueLoader : valueLoaders) {
            for (Long id:valueLoader.getParents()){
                if (notBlockModels.contains(id)) {
                    starterLoaders.add(valueLoader);
                    break;
                }
            }
        }

        return new ValueContextManager(starterLoaders, valueLoaders,  name, blockModels);
    }

}
