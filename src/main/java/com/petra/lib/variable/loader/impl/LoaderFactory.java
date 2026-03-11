package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.SourceInputVariableModel;
import com.petra.lib.constructor.model.ValueModelDto;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.source.RemoteSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LoaderFactory {

    static class FactoryContext {
        ThreadController threadController;
        Sender sender;
        Collection<ValueModelDto> variables;
        Collection<ValueLoader> createdLoaders = new ArrayList<>();
        Set<Long> availableParents;

        public FactoryContext(ThreadController threadController, Sender sender, Collection<ValueModelDto> variables) {
            this.threadController = threadController;
            this.sender = sender;
            this.variables = variables;
            this.availableParents = variables.stream()
                    .mapToLong(ValueModelDto::getId).boxed().collect(Collectors.toSet());
        }

        ValueLoader getOrCreateLoader(ValueModelDto variableToCreate) {
            for (ValueLoader valueLoader : createdLoaders) {
                if (valueLoader.getVariableId().equals(variableToCreate.getId())) {
                    return valueLoader;
                }
            }

            ValueLoader createdLoader;
            switch (variableToCreate.getLoaderType()) {
//            case EMPTY_LOADER:
//                return createEmptyLoader(
//                        threadController,
//                        valueModelDto
//                );
                case INPUT_LOADER:
                    createdLoader = createInputLoader(
                            this,
                            variableToCreate
                    );
                    break;

                case SCRIPT_LOADER:
                    createdLoader = createScriptLoader(
                            this,
                            variableToCreate
                    );
                    break;

                case SOURCE_LOADER:
                    createdLoader = createSourceLoader(
                            this,
                            variableToCreate
                    );
                    break;

                default:
                    throw new NullPointerException("Source loader type not found");
            }
            createdLoaders.add(createdLoader);
            return createdLoader;
        }

        ThreadController getThreadController() {
            return threadController;
        }

        Sender getSender() {
            return sender;
        }

        Collection<ValueModelDto> getVariables() {
            return variables;
        }

        Set<Long> getAvailableParentIds() {
            return availableParents;
        }
    }

    public static Collection<ValueLoader> createLoaders(Collection<ValueModelDto> modelDtos, ThreadController threadController,
                                                        Sender sender) {
        FactoryContext factoryContext = new FactoryContext(threadController, sender, modelDtos);
        for (ValueModelDto valueModelDto : modelDtos) {
            factoryContext.getOrCreateLoader(valueModelDto);
        }
        return factoryContext.createdLoaders;
    }

    private static ValueLoader createInputLoader(FactoryContext factoryContext, ValueModelDto valueModel) {
        List<Long> parents = new ArrayList<>();
        if (factoryContext.getAvailableParentIds().contains(valueModel.getInputValueId())) {
            parents.add(valueModel.getInputValueId());
        }
        List<ValueLoader> children = getChildren(factoryContext, valueModel.getId());

        return new InputLoader(factoryContext.getThreadController(), valueModel,
                parents, children);
    }


    private static ValueLoader createScriptLoader(FactoryContext factoryContext, ValueModelDto valueModel) {
        List<Long> parents = getVariablesParents(valueModel, factoryContext);
        List<ValueLoader> children = getChildren(factoryContext, valueModel.getId());
        return new ScriptLoader(valueModel, factoryContext.getThreadController(), parents, children);
    }


    private static ValueLoader createSourceLoader(FactoryContext factoryContext,
                                                  ValueModelDto valueModelDto) {

        List<Long> parents = getVariablesParents(valueModelDto, factoryContext);
        List<ValueLoader> children = getChildren(factoryContext, valueModelDto.getId());
        return new RemoteSource(factoryContext.getSender(),
                factoryContext.getThreadController(),
                valueModelDto.getSourceInputVariableModels(),
                valueModelDto,
                parents, children);
    }

//    private static ValueLoader createEmptyLoader(ThreadController threadController, ValueModelDto valueModel) {
//        return new EmptyLoader(threadController, valueModel);
//    }

    private static List<ValueLoader> getChildren(FactoryContext factoryContext, Long parentId) {
        return factoryContext.getVariables().stream()
                .filter(model -> model.isChildOf(parentId))
                .map(factoryContext::getOrCreateLoader)
                .collect(Collectors.toList());
    }

    private static List<Long> getVariablesParents(ValueModelDto valueModelDto, FactoryContext factoryContext) {
        return valueModelDto.getSourceInputVariableModels().stream()
                .filter(sourceInputVariableModel -> factoryContext.getAvailableParentIds()
                        .contains(sourceInputVariableModel.getProducerVariable()))
                .mapToLong(SourceInputVariableModel::getProducerVariable).boxed().collect(Collectors.toList());
    }

}
