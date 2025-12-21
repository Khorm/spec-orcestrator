package com.petra.lib.variable.loader.impl;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.constructor.model.ValueModelDto;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.loader.impl.source.SourceInputVariable;
import com.petra.lib.variable.loader.impl.source.RemoteSource;

import java.util.List;
import java.util.stream.Collectors;

public class LoaderFactory {

    public static ValueLoader createLoader(ValueModelDto valueModelDto, ThreadController threadController, Sender sender){
        switch (valueModelDto.getLoaderType()){
            case EMPTY_LOADER:
                return createEmptyLoader(
                        valueModelDto.getId(),
                        valueModelDto.getName(),
                        valueModelDto.getMultiplicity(),
                        valueModelDto.getParents(),
                        valueModelDto.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        threadController
                );
            case INPUT_LOADER:
                return createInputLoader(
                        valueModelDto.getId(),
                        valueModelDto.getName(),
                        valueModelDto.getInputValueId(),
                        valueModelDto.getMultiplicity(),
                        valueModelDto.getParents(),
                        valueModelDto.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        valueModelDto.getExtractionString(),
                        threadController
                );
            case SCRIPT_LOADER:
                return createScriptLoader(
                        valueModelDto.getScript(),
                        valueModelDto.getId(),
                        valueModelDto.getName(),
                        valueModelDto.getMultiplicity(),
                        valueModelDto.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        valueModelDto.getParents(),
                        threadController
                );

            case SOURCE_LOADER:
                return createSourceLoader(
                        valueModelDto.getChildren().stream().map(loaderModel ->  createLoader(loaderModel, threadController, sender))
                                .collect(Collectors.toList()),
                        valueModelDto.getParents(),
                        sender,
                        new Identifier(valueModelDto.getSourceId(), valueModelDto.getSourceVersion()),
                        valueModelDto.getSourceName(),
                        valueModelDto.getId(),
                        valueModelDto.getName(),
                        threadController,
                        valueModelDto.getMultiplicity(),
                        valueModelDto.getSourceInputVariableModels().stream().map(SourceInputVariable::new)
                                .collect(Collectors.toList()),
                        valueModelDto.getExtractionString()
                );

            default:
                throw  new NullPointerException("Source loader type not found");
        }
    }

    public static ValueLoader createInputLoader(Long id, String name, Long inputValueId, Multiplicity multiplicity,
                                                List<Long> parents, List<ValueLoader> children, String extractionString,
                                                ThreadController threadController) {
        ValueModel valueModel = new ValueModel(id, name, multiplicity, null);
        return new InputLoader(inputValueId, children, parents, extractionString, threadController,valueModel);
    }

    public static ValueLoader createScriptLoader(String groovyScript, Long variableId, String name,
                                                 Multiplicity multiplicity, List<ValueLoader> childValues,
                                                 List<Long> parentValues,
                                                 ThreadController threadController) {
        return new ScriptLoader(groovyScript, variableId, name, multiplicity, childValues, parentValues, threadController);
    }

    public static ValueLoader createSourceLoader(List<ValueLoader> childValues, List<Long> parentValues, Sender sender,
                                                 Identifier sourceId, String sourceName,
                                                 Long currentVariableId, String currentVariableName, ThreadController threadController,
                                                 Multiplicity currentMultiplicity,
                                                 List<SourceInputVariable> sourceInputVariables, String extractionString) {
        return new RemoteSource(childValues, parentValues, sender,
                sourceId, sourceName,
                currentVariableId, currentVariableName,
                threadController, currentMultiplicity,
                sourceInputVariables, extractionString);
    }

    public static ValueLoader createEmptyLoader(Long id, String name, Multiplicity multiplicity,
                                                List<Long> parents, List<ValueLoader> children, ThreadController threadController){
        ValueModel valueModel = new ValueModel(id, name, multiplicity, null);
        return new EmptyLoader(children, parents, threadController, valueModel);
    }
}
