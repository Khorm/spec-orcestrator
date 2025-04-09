package com.petra.lib.variable;

import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.loader.LoaderFactory;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.model.VariableLoaderModel;
import com.petra.lib.variable.model.VariableModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariableFactory {

    public static VariableManager createVariableManager(VariableModel variableModel, Sender sender,
                                                        String producerServiceUrl, ThreadController threadController, Identifier ownerId) {

        List<ValueLoader> rootValueLoaders = new ArrayList<>();
        Map<Long, List<ValueLoader>> childrenByParentId = new HashMap<>();
        for (VariableLoaderModel variableLoaderModel : variableModel.getVariables()) {

            ValueLoader valueLoader;
            List<ValueLoader> childValueLoaders;
            if (childrenByParentId.containsKey(variableLoaderModel.getVariableId())){
                childValueLoaders = childrenByParentId.get(variableLoaderModel.getVariableId());
            }else {
                childValueLoaders = new ArrayList<>();
            }

            switch (variableLoaderModel.getLoaderType()) {
                case INPUT:
                    valueLoader = LoaderFactory.createInputLoader(variableLoaderModel.getVariableId(),
                            variableLoaderModel.getName(),
                            variableLoaderModel.getSourceVariable(),
                            variableLoaderModel.getParentVariables(),
                            childValueLoaders);
                    break;
                case SCRIPT:
                    valueLoader = LoaderFactory.createScriptLoader(
                            variableLoaderModel.getScript(),
                            variableLoaderModel.getVariableId(),
                            variableLoaderModel.getName(),
                            variableLoaderModel.getMultiplicity(),
                            childValueLoaders,
                            variableLoaderModel.getParentVariables()
                    );
                    break;

                case SOURCE:
                    Identifier sourceId = new Identifier(variableLoaderModel.getSourceId(), variableLoaderModel.getSourceVersion());
                    valueLoader = LoaderFactory.createSourceLoader(
                            childValueLoaders,
                            variableLoaderModel.getParentVariables(),
                            sender,
                            producerServiceUrl,
                            sourceId,
                            variableLoaderModel.getSourceUrl(),
                            variableLoaderModel.getSourceVariable(),
                            variableLoaderModel.getVariableId(),
                            variableLoaderModel.getName(),
                            threadController,
                            createVariableManager(variableLoaderModel.getSourceVariableModel(),sender, producerServiceUrl,threadController, ownerId)
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Variable type not found " + variableLoaderModel.getLoaderType());

            }

            if (variableLoaderModel.getParentVariables().isEmpty()) {
                rootValueLoaders.add(valueLoader);
            } else {
                for (Long parent : variableLoaderModel.getParentVariables()) {
                    if (childrenByParentId.containsKey(parent)) {
                        childrenByParentId.get(parent).add(valueLoader);
                    }else {
                        List<ValueLoader> list = new ArrayList<>();
                        list.add(valueLoader);
                        childrenByParentId.put(parent, list);
                    }
                }
            }
            childrenByParentId.put(variableLoaderModel.getVariableId(), childValueLoaders);
        }

        return new VariableManager(rootValueLoaders, variableModel.getVariablesCount(), ownerId);
    }
}
