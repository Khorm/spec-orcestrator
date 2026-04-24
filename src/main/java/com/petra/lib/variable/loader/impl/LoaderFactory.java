package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.constructor.model.SourceInputVariableModel;
import com.petra.lib.constructor.model.ValueLoaderDto;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.source.RemoteSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class LoaderFactory {

    static class FactoryContext {
        ThreadController threadController;
        Sender sender;
        //        RemoteConsumerModel remoteConsumerModel;
        Collection<ValueLoader> createdLoaders = new ArrayList<>();
        /**
         * Коллекция загружаемых переменных
         */
        Collection<ValueLoaderDto> loadValues;

        /**
         * Коллекция всех переменных включая загружаемые
         */
        Collection<ValueModel> contextValues;

        public FactoryContext(ThreadController threadController, Sender sender, Collection<ValueLoaderDto> loadValues,
                              Collection<ValueModel> contextValues) {
            this.threadController = threadController;
            this.sender = sender;
            this.loadValues = loadValues;
            this.contextValues = contextValues;
        }

        ValueLoader getOrCreateLoader(ValueLoaderDto variableToCreate) {
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

        Set<Long> getAvailableParentIds(Long childId) {
            return contextValues
                    .stream().filter(valueModel -> !valueModel.getId().equals(childId))
                    .mapToLong(ValueModel::getId).boxed().collect(Collectors.toSet());
        }

        List<ValueLoader> getChildren(FactoryContext factoryContext, Long parentId) {
            return loadValues.stream()
                    .filter(model -> model.isChildOf(parentId))
                    .map(factoryContext::getOrCreateLoader)
                    .collect(Collectors.toList());
        }

        List<Long> getVariablesParents(ValueLoaderDto valueLoaderDto, FactoryContext factoryContext) {
            return valueLoaderDto.getSourceInputVariableModels().stream()
                    .filter(sourceInputVariableModel -> factoryContext
                            .getAvailableParentIds(valueLoaderDto.getId())
                            .contains(sourceInputVariableModel.getProducerVariable()))
                    .mapToLong(SourceInputVariableModel::getProducerVariable).boxed().collect(Collectors.toList());
        }
    }

    public static Collection<ValueLoader> createLoaders(ThreadController threadController,
                                                        Sender sender, Collection<ValueLoaderDto> loadValues,
                                                        Collection<ValueModel> contextValues) {
        FactoryContext factoryContext = new FactoryContext(threadController, sender, loadValues, contextValues);
        for (ValueLoaderDto valueLoaderDto : loadValues) {
            factoryContext.getOrCreateLoader(valueLoaderDto);
        }
        return factoryContext.createdLoaders;
    }

    private static ValueLoader createInputLoader(FactoryContext factoryContext, ValueLoaderDto valueModel) {
        List<Long> parents = new ArrayList<>();
        if (factoryContext.getAvailableParentIds(valueModel.getId()).contains(valueModel.getInputValueId())) {
            parents.add(valueModel.getInputValueId());
        }
        List<ValueLoader> children = factoryContext.getChildren(factoryContext, valueModel.getId());

        return new InputLoader(factoryContext.getThreadController(), valueModel,
                parents, children);
    }


    private static ValueLoader createScriptLoader(FactoryContext factoryContext, ValueLoaderDto valueModel) {
        List<Long> parents = factoryContext.getVariablesParents(valueModel, factoryContext);
        List<ValueLoader> children = factoryContext.getChildren(factoryContext, valueModel.getId());
        return new ScriptLoader(valueModel, factoryContext.getThreadController(), parents, children);
    }


    private static ValueLoader createSourceLoader(FactoryContext factoryContext,
                                                  ValueLoaderDto valueLoaderDto) {
        List<Long> parents = factoryContext.getVariablesParents(valueLoaderDto, factoryContext);
        List<ValueLoader> children = factoryContext.getChildren(factoryContext, valueLoaderDto.getId());
        return new RemoteSource(factoryContext.getSender(),
                factoryContext.getThreadController(),
                valueLoaderDto.getSourceInputVariableModels(),
                valueLoaderDto,
                parents, children);
    }


}
