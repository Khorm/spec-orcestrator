package com.petra.lib.variable.loader.impl;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.constructor.model.ValueLoaderModel;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.loader.impl.source.SourceInputVariable;
import com.petra.lib.variable.loader.impl.source.SourceLoader;

import java.util.List;
import java.util.stream.Collectors;

public class LoaderFactory {

    public static ValueLoader createLoader(ValueLoaderModel valueLoaderModel, ThreadController threadController, Sender sender){
        switch (valueLoaderModel.getLoaderType()){
            case EMPTY_LOADER:
                return createEmptyLoader(
                        valueLoaderModel.getId(),
                        valueLoaderModel.getName(),
                        valueLoaderModel.getMultiplicity(),
                        valueLoaderModel.getParents(),
                        valueLoaderModel.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        threadController
                );
            case INPUT_LOADER:
                return createInputLoader(
                        valueLoaderModel.getId(),
                        valueLoaderModel.getName(),
                        valueLoaderModel.getInputValueId(),
                        valueLoaderModel.getMultiplicity(),
                        valueLoaderModel.getParents(),
                        valueLoaderModel.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        valueLoaderModel.getExtractionString(),
                        threadController
                );
            case SCRIPT_LOADER:
                return createScriptLoader(
                        valueLoaderModel.getScript(),
                        valueLoaderModel.getId(),
                        valueLoaderModel.getName(),
                        valueLoaderModel.getMultiplicity(),
                        valueLoaderModel.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        valueLoaderModel.getParents(),
                        threadController
                );

            case SOURCE_LOADER:
                return createSourceLoader(
                        valueLoaderModel.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        valueLoaderModel.getParents(),
                        sender,
                        valueLoaderModel.getMultiplicity(),
                        new Identifier(valueLoaderModel.getSourceId(), valueLoaderModel.getSourceVersion()),
                        valueLoaderModel.getSourceName(),
                        valueLoaderModel.getId(),
                        valueLoaderModel.getName(),
                        threadController,
                        valueLoaderModel.getSourceInputVariableModels().stream().map(SourceInputVariable::new)
                                .collect(Collectors.toList())
                );

            default:
                throw  new NullPointerException("Source loader type not found");
        }
    }

    public static ValueLoader createInputLoader(Long id, String name, Long inputValueId, Multiplicity multiplicity,
                                                List<Long> parents, List<ValueLoader> children, String extractionString,
                                                ThreadController threadController) {
        return new InputLoader(inputValueId, children, id, parents, name, extractionString, multiplicity, threadController);
    }

    public static ValueLoader createScriptLoader(String groovyScript, Long variableId, String name,
                                                 Multiplicity multiplicity, List<ValueLoader> childValues,
                                                 List<Long> parentValues,
                                                 ThreadController threadController) {
        return new ScriptLoader(groovyScript, variableId, name, multiplicity, childValues, parentValues, threadController);
    }

    public static ValueLoader createSourceLoader(List<ValueLoader> childValues, List<Long> parentValues, Sender sender,
                                                 Multiplicity currentMultiplicity,
                                                 Identifier sourceId, String sourceName,
                                                 Long currentVariableId, String currentVariableName, ThreadController threadController,
                                                 List<SourceInputVariable> sourceInputVariables) {
        return new SourceLoader(childValues, parentValues, sender, sourceId, sourceName, currentVariableId, currentVariableName,
                threadController, currentMultiplicity,  sourceInputVariables);
    }

    public static ValueLoader createEmptyLoader(Long id, String name, Multiplicity multiplicity,
                                                List<Long> parents, List<ValueLoader> children, ThreadController threadController){
        return new EmptyLoader(id, children, parents, name, multiplicity, threadController);
    }
}
