package com.petra.lib.variable;


import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.constructor.model.ValuesCollectionModel;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContextManager;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.LoaderFactory;

import java.util.ArrayList;
import java.util.Collection;

public final class VariableFactory {


    VariableFactory() {
    }

    public static ValueContextManager createStartedLoaders(ValuesCollectionModel valuesCollectionModel,
                                                           ThreadController threadController, Sender sender) {
        Collection<ValueLoader> valueLoaders = new ArrayList<>();
        for (ValueModel valueModel : valuesCollectionModel.getValues()) {
            valueLoaders.add(LoaderFactory.createLoader(valueModel, threadController, sender));
        }
        return new ValueContextManager(valuesCollectionModel.getValueCount(), valueLoaders);
    }


//    public static VariableManager createVariableManager(VariableModel variableModel, Sender sender,
//                                                        String producerServiceUrl, ThreadController threadController, Identifier ownerId) {
//
//        List<ValueLoader> rootValueLoaders = new ArrayList<>();
//        Map<Long, List<ValueLoader>> childrenByParentId = new HashMap<>();
//        for (VariableLoaderModel variableLoaderModel : variableModel.getVariables()) {
//
//            ValueLoader valueLoader;
//            List<ValueLoader> childValueLoaders;
//            if (childrenByParentId.containsKey(variableLoaderModel.getVariableId())){
//                childValueLoaders = childrenByParentId.get(variableLoaderModel.getVariableId());
//            }else {
//                childValueLoaders = new ArrayList<>();
//            }
//
//            switch (variableLoaderModel.getLoaderType()) {
//                case INPUT:
//                    valueLoader = LoaderFactory.createInputLoader(variableLoaderModel.getVariableId(),
//                            variableLoaderModel.getName(),
//                            variableLoaderModel.getSourceVariable(),
//                            variableLoaderModel.getParentVariables(),
//                            childValueLoaders);
//                    break;
//                case SCRIPT:
//                    valueLoader = LoaderFactory.createScriptLoader(
//                            variableLoaderModel.getScript(),
//                            variableLoaderModel.getVariableId(),
//                            variableLoaderModel.getName(),
//                            variableLoaderModel.getMultiplicity(),
//                            childValueLoaders,
//                            variableLoaderModel.getParentVariables()
//                    );
//                    break;
//
//                case SOURCE:
//                    Identifier sourceId = new Identifier(variableLoaderModel.getSourceId(), variableLoaderModel.getSourceVersion());
//                    valueLoader = LoaderFactory.createSourceLoader(
//                            childValueLoaders,
//                            variableLoaderModel.getParentVariables(),
//                            sender,
//                            producerServiceUrl,
//                            sourceId,
//                            variableLoaderModel.getSourceUrl(),
//                            variableLoaderModel.getSourceVariable(),
//                            variableLoaderModel.getVariableId(),
//                            variableLoaderModel.getName(),
//                            threadController,
//                            createVariableManager(variableLoaderModel.getSourceVariableModel(),sender, producerServiceUrl,threadController, ownerId)
//                    );
//                    break;
//                default:
//                    throw new IllegalArgumentException("Variable type not found " + variableLoaderModel.getLoaderType());
//
//            }
//
//            if (variableLoaderModel.getParentVariables().isEmpty()) {
//                rootValueLoaders.add(valueLoader);
//            } else {
//                for (Long parent : variableLoaderModel.getParentVariables()) {
//                    if (childrenByParentId.containsKey(parent)) {
//                        childrenByParentId.get(parent).add(valueLoader);
//                    }else {
//                        List<ValueLoader> list = new ArrayList<>();
//                        list.add(valueLoader);
//                        childrenByParentId.put(parent, list);
//                    }
//                }
//            }
//            childrenByParentId.put(variableLoaderModel.getVariableId(), childValueLoaders);
//        }
//
//        return new VariableManager(rootValueLoaders, variableModel.getVariablesCount(), ownerId);
//    }

//    public List<ValueLoader> createValueLoaders(VariableModel variableModel, Sender sender,
//                                                String producerServiceUrl, ThreadController threadController, Identifier ownerId){
//        List<ValueLoader> rootValueLoaders = new ArrayList<>();
//        Map<Long, List<ValueLoader>> childrenByParentId = new HashMap<>();
//        for (VariableLoaderModel variableLoaderModel : variableModel.getVariables()) {
//
//            ValueLoader valueLoader;
//            List<ValueLoader> childValueLoaders;
//            if (childrenByParentId.containsKey(variableLoaderModel.getVariableId())){
//                childValueLoaders = childrenByParentId.get(variableLoaderModel.getVariableId());
//            }else {
//                childValueLoaders = new ArrayList<>();
//            }
//
//            switch (variableLoaderModel.getLoaderType()) {
//                case INPUT:
//                    valueLoader = LoaderFactory.createInputLoader(variableLoaderModel.getVariableId(),
//                            variableLoaderModel.getName(),
//                            variableLoaderModel.getSourceVariable(),
//                            variableLoaderModel.getParentVariables(),
//                            childValueLoaders);
//                    break;
//                case SCRIPT:
//                    valueLoader = LoaderFactory.createScriptLoader(
//                            variableLoaderModel.getScript(),
//                            variableLoaderModel.getVariableId(),
//                            variableLoaderModel.getName(),
//                            variableLoaderModel.getMultiplicity(),
//                            childValueLoaders,
//                            variableLoaderModel.getParentVariables()
//                    );
//                    break;
//
//                case SOURCE:
//                    Identifier sourceId = new Identifier(variableLoaderModel.getSourceId(), variableLoaderModel.getSourceVersion());
//                    valueLoader = LoaderFactory.createSourceLoader(
//                            childValueLoaders,
//                            variableLoaderModel.getParentVariables(),
//                            sender,
//                            producerServiceUrl,
//                            sourceId,
//                            variableLoaderModel.getSourceUrl(),
//                            variableLoaderModel.getSourceVariable(),
//                            variableLoaderModel.getVariableId(),
//                            variableLoaderModel.getName(),
//                            threadController,
//                            createVariableManager(variableLoaderModel.getSourceVariableModel(),sender, producerServiceUrl,threadController, ownerId)
//                    );
//                    break;
//                default:
//                    throw new IllegalArgumentException("Variable type not found " + variableLoaderModel.getLoaderType());
//
//            }
//
//            if (variableLoaderModel.getParentVariables().isEmpty()) {
//                rootValueLoaders.add(valueLoader);
//            } else {
//                for (Long parent : variableLoaderModel.getParentVariables()) {
//                    if (childrenByParentId.containsKey(parent)) {
//                        childrenByParentId.get(parent).add(valueLoader);
//                    }else {
//                        List<ValueLoader> list = new ArrayList<>();
//                        list.add(valueLoader);
//                        childrenByParentId.put(parent, list);
//                    }
//                }
//            }
//            childrenByParentId.put(variableLoaderModel.getVariableId(), childValueLoaders);
//        }
//
//        return rootValueLoaders;
//    }
}
